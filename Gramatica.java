
import java.io.*;
import java.util.*;


public class Gramatica{
	public static void main(String[] args) throws Exception{
		String gramatica;
		String executionMode;
		String saveFile;
		String ropes;
		if (args.length < 3) 
		System.exit(0);
		gramatica = args[0];
		executionMode = args[1];
		saveFile = args[2];
		//System.out.println("Modo de ejecucion " + executionMode);

		String[] states;
		String[] alphabet;
		String initState;
		LinkedList<String> transitions = new LinkedList<String>();
		Scanner scan = new Scanner("");
		try {
			scan = new Scanner(new File(gramatica));
		}catch(Exception e){}
		states = scan.nextLine().split(",");
		alphabet = scan.nextLine().split(",");
		initState = scan.nextLine();
		while(scan.hasNextLine()){
			transitions.add(scan.nextLine());
		}
		scan.close();

		if (executionMode.equals("-check")) {
			if (args.length < 4) 
			System.exit(0);
			ropes = args[3];
			//metodo para evaluar cuerdas
		} else {
			if(executionMode.equals("-afd")) {
				//metodo para generar el afd
			} else {
				//metodo para generar el afn
				GLDtoAFN(states, alphabet, initState,transitions);
				System.exit(0);
			}
		}

	}


	public static String GLDtoAFN(String[] symbols, String[] endSymbols, String initial, LinkedList<String> transitions){
		int states = symbols.length+2;

		for(String s: transitions){
			int len = s.length();
			if(len>=5){
				states += len - 5;
				if(findPos(symbols,s.charAt(len-1))==-1){
					states++;	
				}
			}
		}
		
		
		for(String s: transitions){
			int len = s.length();
			if(len>=5){
				states += len - 5;
				if(findPos(symbols,s.charAt(len-1))==-1){
					states++;	
				}
			}
		}
		String[][] mat = new String[endSymbols.length+1][states];

		for(int i =0 ; i < states; i++ ){
			mat[0][i] = Integer.toString(i);
		}
		int currentState = symbols.length;
		for(String s: transitions){
			int len = s.length();
			if(findPos(symbols,s.charAt(len-1))==-1){ // Caso que termine en simboloTerminal 
				int initialState = findPos(symbols,s.charAt(0))+1;
				for(int i=3; i<len-1;i++){
					if(mat[findPos(endSymbols,s.charAt(i))+1][initialState]==null){
						mat[findPos(endSymbols,s.charAt(i))+1][initialState] = Integer.toString(currentState++);
						initialState = currentState;
					} else {
						mat[findPos(endSymbols,s.charAt(i))+1][initialState] += ";"+Integer.toString(currentState++);
						initialState = currentState;
					}
				}
				if(mat[findPos(endSymbols,s.charAt(len-1))+1][initialState]==null){
					mat[findPos(endSymbols,s.charAt(len-1))+1][initialState] = Integer.toString(states-1);
				} else {
					mat[findPos(endSymbols,s.charAt(len-1))+1][initialState] +=";"+ Integer.toString(states-1);
				}
			} else { //En caso termine en una Variable
				int initialState = findPos(symbols,s.charAt(0))+1;
				for(int i=3; i<len-2;i++){
					if(mat[findPos(endSymbols,s.charAt(i))+1][initialState]==null){
						mat[findPos(endSymbols,s.charAt(i))+1][initialState] = Integer.toString(currentState++);
						initialState = currentState;
					} else {
						mat[findPos(endSymbols,s.charAt(i))+1][initialState] += ";"+Integer.toString(currentState++);
						initialState = currentState;
					}
				}
				if(mat[findPos(endSymbols,s.charAt(len-2))+1][initialState]==null){
					mat[findPos(endSymbols,s.charAt(len-2))+1][initialState] = Integer.toString(findPos(symbols,s.charAt(len-1))+1);
				} else {
					mat[findPos(endSymbols,s.charAt(len-2))+1][initialState] +=";"+ Integer.toString(findPos(symbols,s.charAt(len-1))+1);
				}
			}
		}
			
		for(int i =0 ; i < states; i++ ){
			for(int j = 0; j < mat.length; j++ ){
				if(mat[j][i]==null){
					mat[j][i] = "0";
				}
			}
		}

		String resp = "";
		for(int i = 0; i< endSymbols.length-1;i++){
			resp+=endSymbols[i]+",";
		}
		resp+=endSymbols[endSymbols.length-1];
		resp+="\n";
		resp+=Integer.toString(states);
		resp+="\n";
		resp+=Integer.toString(states-1);
		resp+="\n";
		for(int i = 0; i < mat.length;i++){
			for(int j = 0; j< mat[i].length-1; j++){
				resp += mat[i][j] + ",";
			}
			resp += mat[i][mat.length-1];
			resp+="\n";
		}
		System.out.println(resp);
		return resp;
	}	

	public static int findPos(String[] arr, char c){
		String s = Character.toString(c);
		for(int i = 0; i < arr.length; i++){
			if(arr[i].equals(s)){
				return i;
			}
		}
		return -1;
	}

}