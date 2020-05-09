
import java.io.*;
import java.util.*;


public class Gramatica{
	public static void main(String[] args) throws Exception{
		String gramatica;
		String executionMode;
		String saveFile;
		String ropes;
		//if (args.length < 3)
		//System.exit(0);
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
		}catch(Exception e){
			System.out.println("Archivo " + gramatica + " no existe");
			//System.exit(0);
		}
		states = scan.nextLine().split(",");
		alphabet = scan.nextLine().split(",");
		initState = scan.nextLine();
		while(scan.hasNextLine()){
			transitions.add(scan.nextLine());
		}
		scan.close();

		if (executionMode.equals("-check")) {
			//if (args.length < 4)
			//System.exit(0);
			ropes = args[3];
			//metodo para evaluar cuerdas
			AFN afn = new AFN(GLDtoAFN(states, alphabet, initState, transitions));
			Scanner cuerdas = new Scanner("");
			try {
				cuerdas = new Scanner(new File(ropes));
			}catch(Exception e){}

			FileWriter file = new FileWriter(saveFile);;
			try
			{
				while(cuerdas.hasNextLine()){
					if(afn.accept(cuerdas.nextLine())){
						file.write("aceptada\n");
					} else {
						file.write("rechazada\n");
					}
				}
			} catch (Exception e){}
			file.close();
			cuerdas.close();
		} else {
			if(executionMode.equals("-afd")) {
				//metodo para generar el afd
				String answer =GLDtoAFN(states, alphabet, initState,transitions);
				answer = AFNtoAFD(answer,alphabet);
				AFNFile(answer, saveFile);

			} else {
				//metodo para generar el afn
				String answer =GLDtoAFN(states, alphabet, initState,transitions);
				AFNFile(answer, saveFile);
				//System.exit(0);
			}
		}

	}


	public static String GLDtoAFN(String[] symbols, String[] endSymbols, String initial, LinkedList<String> transitions){
		int states = symbols.length+2;

		for(String s: transitions){
			int len = s.length();
			if(len>=5&&findPos(symbols,s.charAt(len-1))==-1){
				states += len - 4;
			} else if (len>5&&findPos(symbols,s.charAt(len-1))!=-1){
				states += len - 5;
			}
		}
		String[][] mat = new String[endSymbols.length+1][states];

		for(int i =0 ; i < states; i++ ){
			mat[0][i] = Integer.toString(i);
		}
		int currentState = symbols.length+1;
		for(String s: transitions){
			int len = s.length();
			if(findPos(symbols,s.charAt(len-1))==-1){ // Caso que termine en simboloTerminal
				int initialState = findPos(symbols,s.charAt(0))+1;
				for(int i=3; i<len-1;i++){
					if(mat[findPos(endSymbols,s.charAt(i))+1][initialState]==null){
						
						mat[findPos(endSymbols,s.charAt(i))+1][initialState] = Integer.toString(currentState);
						initialState = currentState;
						currentState++;
					} else {
						
						mat[findPos(endSymbols,s.charAt(i))+1][initialState] += ";"+Integer.toString(currentState);
						initialState = currentState;
						currentState++;
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
						
						mat[findPos(endSymbols,s.charAt(i))+1][initialState] = Integer.toString(currentState);
						initialState = currentState;
						currentState++;
					} else {
						
						mat[findPos(endSymbols,s.charAt(i))+1][initialState] += ";"+Integer.toString(currentState);
						initialState = currentState;
						currentState++;
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
			resp += mat[i][mat[i].length-1];
			resp+="\n";
		}
		//System.out.println(resp);

		//System.out.println(Arrays.deepToString(mat));
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

	public static void AFNFile(String afn, String saveFile){

		try{
			File file = new File(saveFile);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(afn);
			bw.close();
		} catch (Exception e){

		}

	}

	public static String AFNtoAFD(String afn, String[] alphabet){
		AFN objAFN = new AFN(afn);
		LinkedList<int[]> states = new LinkedList<int[]>();
		int[] first = {1};
		int[] errorState = {0};
		first = removeDuplicates(objAFN.getTransition(first,'#'));
		//System.out.println(Arrays.toString(first));
		states.add(errorState);
		states.addLast(first);
		LinkedList<int[]> transitions = new LinkedList<int[]>();
		LinkedList<int[]> toVisit = new LinkedList<int[]>();
		toVisit.add(first);
		transitions.add(new int[alphabet.length]);
		while(!toVisit.isEmpty()){
			int[] thisTransitions = new int[alphabet.length];
			int pos = 0;
			for(String s: alphabet){
				int[] result = removeDuplicates(objAFN.getTransition(toVisit.getFirst(),s.charAt(0)));
				result = removeDuplicates(objAFN.getTransition(result,'#'));
				Arrays.sort(result);

				int exists = findElement(states,result);
				//System.out.println(Arrays.toString(result)+exists+Arrays.toString(states.get(1)));
				if(exists == -1){
					toVisit.addLast(result);
					states.addLast(result);
					exists = states.size()-1;
				}
				thisTransitions[pos] = exists;
				pos++;
			}

			transitions.addLast(thisTransitions);

			toVisit.removeFirst();

		}

		String resp = "";
		for(int i = 0; i< alphabet.length-1;i++){
			resp+=alphabet[i]+",";
		}
		resp+=alphabet[alphabet.length-1];
		resp+="\n";
		resp+=Integer.toString(transitions.size());
		resp+="\n";
		int AFNFinalState = objAFN.finalStates[0];
		//System.out.println("FINAL STATE"+AFNFinalState);
		LinkedList<Integer> finalAFD = new LinkedList<Integer>();
		int pos = 0;
		for(int[] i : states){
			//System.out.println("ESTADOS"+Arrays.toString(i));
			loop:
			for(int j = 0; j < i.length; j++){
				if(AFNFinalState == i[j]){
					finalAFD.add(pos);
					break loop;
				}
			}
			pos++;
		}
		
		//System.out.println("FINAL STATE NEW"+finalAFD.toString());
		int[] arrayFinal = finalAFD.stream().mapToInt(i->i).toArray();
		for(int i = 0; i < arrayFinal.length -1; i++){
			resp+=Integer.toString(arrayFinal[i])+",";
		}
		resp+=Integer.toString(arrayFinal[arrayFinal.length -1]);
		resp+="\n";
		for(int j = 0; j < alphabet.length; j++){
			for(int i = 0 ; i < transitions.size() -1; i++){
				resp+=Integer.toString(transitions.get(i)[j])+",";
			}
			resp += Integer.toString(transitions.get(transitions.size()-1)[j]);
			resp+="\n";
		}

		//System.out.println(resp);
		return resp;

	}

	public static int[] removeDuplicates(int[] arr){
		return Arrays.stream(arr).distinct().toArray();
	}

	public static int findElement(LinkedList<int[]> list,int[] object){
		int pos = 0;
		listLabel:
		for(int[] i : list){
			if(i.length==object.length){
				for(int j = 0; j < object.length; j++){
					if(i[j]!=object[j]) {
						pos++;
						continue listLabel;
					}
				}
				return pos;
			}
			pos++;
		}

		return -1;
	}

}
