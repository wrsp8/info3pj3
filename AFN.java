/*
	Utilice esta clase para guardar la informacion de su
	AFN. NO DEBE CAMBIAR LOS NOMBRES DE LA CLASE NI DE LOS
	METODOS que ya existen, sin embargo, usted es libre de
	agregar los campos y metodos que desee.
*/
import java.util.*;
import java.io.*;
public class AFN{

	/*
		Implemente el constructor de la clase AFN
		que recibe como argumento un string que
		representa el path del archivo que contiene
		la informacion del AFN (i.e. "Documentos/archivo.AFN").
		Puede utilizar la estructura de datos que desee
	*/
	String[] alphabet;
	int total;
	int finalStates[];
	int[][][] transitions;

	public AFN(String path){
		Scanner scan = new Scanner("");
		try {
			scan = new Scanner(path);
		}catch(Exception e){}

			alphabet = scan.nextLine().split(",");
			total = Integer.parseInt(scan.nextLine());
			//finalStates = Arrays.asList(Arrays.stream(scan.nextLine().split(",")).map(val -> Integer.parseInt(val)).toArray()).toArray(new Integer[0]);
			String[] finalSt = scan.nextLine().split(",");
			finalStates = new int[finalSt.length];
			for(int i = 0; i<finalSt.length;i++){
				finalStates[i] = Integer.parseInt(finalSt[i]);
			}
			transitions = new int[alphabet.length+1][total][];
			int row = 0;
			while(scan.hasNextLine()){
				StringTokenizer linea = new StringTokenizer(scan.nextLine(), ",");
				for (int column = 0; column < total; column++) {
					String[] tr = linea.nextToken().split(";");
					transitions[row][column] = new int[tr.length];
					for(int i = 0; i<tr.length;i++){
						transitions[row][column][i] = Integer.parseInt(tr[i]);
					}
				}
				row++;
			}
			scan.close();
	}

	private int getTransitionNumber(char symbol){
		//System.out.println(Arrays.toString(alphabet)+"abcedario, letra,"+ Character.toString(symbol)+".");
		for(int i = 0; i < alphabet.length; i++){
			if(alphabet[i].charAt(0)==symbol)
				return i+1;
		}
		return -1;
	}

	private boolean inFinalState(int[] state){
		//System.out.print("Estado final"+ Arrays.toString(state)+"final state"+ Arrays.toString(finalStates));
		for(int i = 0; i < finalStates.length; i++){
			for(int j = 0; j < state.length; j++)
				if(finalStates[i]==state[j])
					return true;
		}
		return false;
	}

	private int[] parserRecursive(int[] state,String string){
		if(string.length()!=1){
			return parser(parserRecursive(state,string.substring(0,string.length()-1)),string.charAt(string.length()-1));
		} else {
			return parser(state,string.charAt(0));
		}
	}

	private int[] parser(int[] state,char symbol){
		if(!(Character.toString(symbol).equals("")||Character.toString(symbol).equals(" ")
		||Character.toString(symbol).equals("\n")||Character.toString(symbol).equals("\r")))
			return getTransition(state, symbol);
		return state;
	}

	public int[] getTransition(int[] currentState, char symbol){
		int size = 0;
		//include lambda nodes
		for(int i = 0; i < currentState.length; i++){
			size += transitions[0][currentState[i]].length;
		}
		int[] resp = new int[size];
		int pointer = 0;
		for(int i = 0; i < currentState.length; i++){
			int [] temp = transitions[0][currentState[i]];
			for(int j = 0; j < temp.length; j++){
				resp[pointer++] = temp[j];
			}
		}
		currentState = resp;

		//look the places where we can go
		size = 0;
		for(int i = 0; i < currentState.length; i++){
			if (symbol == "#".charAt(0)) {
				size += transitions[0][currentState[i]].length;
			} else {
				size += transitions[getTransitionNumber(symbol)][currentState[i]].length;
			}

		}
		resp = new int[size];
		pointer = 0;
		for(int i = 0; i < currentState.length; i++){

			if (symbol == "#".charAt(0)) {
				int[] temp = transitions[0][currentState[i]];
				for(int j = 0; j < temp.length; j++){
					resp[pointer++] = temp[j];
				}
			} else {
				int[] temp = transitions[getTransitionNumber(symbol)][currentState[i]];
				for(int j = 0; j < temp.length; j++){
					resp[pointer++] = temp[j];
				}
			}


		}
		//ystem.out.println("Estado"+Arrays.toString(currentState)+"Resultado"+Arrays.toString(resp)+"transicion"+Character.toString(symbol));
		return resp;
	}
	/*
		Implemente el metodo accept, que recibe como argumento
		un String que representa la cuerda a evaluar, y devuelve
		un boolean dependiendo de si la cuerda es aceptada o no
		por el AFN. Recuerde lo aprendido en el proyecto 1.
	*/
	public boolean accept(String string){
		//System.out.println(string+"Cuerda a parsear");
		int[] initial = {1};
		return inFinalState(parserRecursive(initial,string));
	}

	/*
		El metodo main debe recibir como primer argumento el path
		donde se encuentra el archivo ".afd", como segundo argumento
		una bandera ("-f" o "-i"). Si la bandera es "-f", debe recibir
		como tercer argumento el path del archivo con las cuerdas a
		evaluar, y si es "-i", debe empezar a evaluar cuerdas ingresadas
		por el usuario una a una hasta leer una cuerda vacia (""), en cuyo
		caso debe terminar. Tiene la libertad de implementar este metodo
		de la forma que desee.
	*/
	public static void main(String[] args) throws Exception{
		String afnName;
		String executionMode;
		String ropes;
		afnName = args[0];
		executionMode = args[1];
		//System.out.println("Modo de ejecucion " + executionMode);
		if (executionMode.equals("-f")) {
			ropes = args[2];
			fMode(afnName, ropes);
		} else {
			if(executionMode.equals("-i")) {
				iMode(afnName);
			} else {
				System.out.println("Modo de ejecucion Incorrecto");
				System.exit(0);
			}
		}

	}

	public static void fMode(String afnName, String ropes){
		AFN afn = new AFN(afnName);
		try {
			Scanner scanner = new Scanner(new File(ropes));
			System.out.println();
			System.out.println("               Alfabeto: " + Arrays.toString(afn.alphabet));
			System.out.println("    Cantidad de Estados: " + afn.total);
			System.out.println("        Estados Finales: " + Arrays.toString(afn.finalStates));
			System.out.format("Transiciones con lambda: %s%n", Arrays.deepToString(afn.transitions[0]));
			for (int i = 0, j = 1; i < afn.alphabet.length; i++){
				System.out.format("     Transiciones con " + afn.alphabet[i] + ": %s%n", Arrays.deepToString(afn.transitions[j++]));
			}
			while(scanner.hasNextLine()){
				System.out.println();
				String newRope = scanner.nextLine();
				System.out.println("Cuerda a evaluar: " + newRope);
				if(afn.accept(newRope)){
					System.out.println("Resultado: Cuerda aceptada!");
				} else {
					System.out.println("Resultado: Cuerda rechazada!");
				}

			}
		}	catch(Exception e){
			System.out.println("Archivo de cuerdas no existe");
		}
	}

	public static void iMode(String afnName){
		AFN afn = new AFN(afnName);
		Scanner scanner = new Scanner(System.in);
		System.out.println();
		System.out.println("               Alfabeto: " + Arrays.toString(afn.alphabet));
		System.out.println("    Cantidad de Estados: " + afn.total);
		System.out.println("        Estados Finales: " + Arrays.toString(afn.finalStates));
		System.out.format("Transiciones con lambda: %s%n", Arrays.deepToString(afn.transitions[0]));
		for (int i = 0, j = 1; i < afn.alphabet.length; i++){
			System.out.format("     Transiciones con " + afn.alphabet[i] + ": %s%n", Arrays.deepToString(afn.transitions[j++]));
		}
		while(true){
			try {
				System.out.println();
				System.out.print("Ingresar cuerda: ");
				String newRope = scanner.nextLine();
				if(newRope.length() > 0){
					if(afn.accept(newRope)){
						System.out.println("Resultado: Cuerda aceptada!");
					} else {
						System.out.println("Resultado: Cuerda rechazada!");
					}
					System.out.println();
				} else{
					System.exit(0);
				}
			} catch(Exception e){
				System.out.println("Resultado: La cuerda no cumple con el alfabeto");
			}
		}
	}
}
