package com.company.iteso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.StreamHandler;

public class Main {

    private static String word;
    private static ArrayList<String> noTerminales;
    private static ArrayList<String>terminales;
    private static ArrayList<String>gramatica;
    private static ArrayList<Integer>pila;
    private static ArrayList<String>pilaFinal;
    private static ArrayList<String>pilaSimbolos;
    private static ArrayList<String>pilaEntrada;
    private static ArrayList<String>pilaAccion;
    private static Stack<Integer>pilaNumber;
    private static String[][] accionTable;
    private static int[][] gotoTable;
    private static String cadenaRefEntrada;
    private static String cadenaEntrada;
    private static String cadenaPila;


    public static void init(){
        noTerminales = new ArrayList<String>();
        terminales = new ArrayList<String>();
        gramatica = new ArrayList<String>();
        pilaSimbolos = new ArrayList<String>();
        pila = new ArrayList<Integer>();
        pilaEntrada = new ArrayList<String>();
        pilaAccion = new ArrayList<String>();
        pilaFinal = new ArrayList<String>();
        pilaNumber = new Stack<Integer>();
        cadenaEntrada = "";
        cadenaRefEntrada = "";
        cadenaPila = "";
    }

    public static void fillData() throws IOException {
        init();
        ReadFile readFile = new ReadFile();
        word = readFile.reader().readLine();
        int noTerm = Integer.parseInt(word);
        for(int i=0; i<noTerm; i++){
            word = readFile.reader().readLine();
            noTerminales.add(word);
        }

        //Llenar el arraylist con los simbolos terminales
        word = readFile.reader().readLine();
        int term = Integer.parseInt(word);
        for(int i=0;i<term;i++){
            word = readFile.reader().readLine();
            terminales.add(word);
        }

        //Llenar el arraylist con la gramatica
        word = readFile.reader().readLine();
        int gram = Integer.parseInt(word);
        for(int i=0;i<gram; i++){
            word = readFile.reader().readLine();
            gramatica.add(word);
        }
        /**
         * Inicializar las tabla
         */
        word = readFile.reader().readLine();
        int numberState = Integer.parseInt(word);
        accionTable = new String[numberState][term];
        for (int i=0;i<numberState;i++){
            for (int j=0;j<term;j++){
                accionTable[i][j] = "0";
            }
        }

        gotoTable = new int[numberState][noTerm];
        for (int i =0; i<numberState; i++){
            for (int j=0; j<noTerm; j++){
                gotoTable[i][j] = 0;
            }
        }

        word = readFile.reader().readLine();
        int numberAccionTable = Integer.parseInt(word);
        for (int i=0; i<numberAccionTable;i++){
            word = readFile.reader().readLine();
            String []rules = word.split(";");
            accionTable[Integer.parseInt(rules[0])][terminales.indexOf(rules[1])] = rules[2];
        }

        word = readFile.reader().readLine();
        int numberGotoTable = Integer.parseInt(word);
        for (int i=0; i<numberGotoTable;i++){
            word = readFile.reader().readLine();
            String []rules = word.split(";");
            gotoTable[Integer.parseInt(rules[0])][noTerminales.indexOf(rules[1])] = Integer.parseInt(rules[2]);
        }

        word = readFile.reader().readLine();
        int casos = Integer.parseInt(word);
        for (int i=0; i<casos;i++){
            word = readFile.reader().readLine();
            int entrada = Integer.parseInt(word);
            for (int j=0; j<entrada;j++){
                word = readFile.reader().readLine();
                String[] entradaSimbolo = word.split(",");
                cadenaEntrada += entradaSimbolo[0];
                cadenaRefEntrada += entradaSimbolo[1];
            }
            cadenaEntrada+="$";
            cadenaRefEntrada+="$";
            cadenaPila+="0";
            pila.add(0);
            pilaFinal.add(cadenaPila);
            pilaNumber.push(0);
            pilaEntrada.add(cadenaEntrada);
            pilaSimbolos.add("");

            AlgoritmoAscendente();
           // Algoritmo();

        }

    }

    public static void AlgoritmoAscendente() {
        int j = 0;
        String cadenaSimbolo = "";
        boolean salida = true;
        while (true) {
            String auxPila = "";
            String simboloEntrada = compareChar(cadenaEntrada);
            String currentAccion = accionTable[pilaNumber.peek()][terminales.indexOf(simboloEntrada)];
            char accionChar = currentAccion.charAt(0);
            String numberStateS = currentAccion.substring(1);
            int numberStateN = Integer.parseInt(numberStateS);
            switch (accionChar){
                case 's':
                    cadenaPila += numberStateS;
                    pilaNumber.push(numberStateN);
                    cadenaSimbolo = pilaSimbolos.get(j) + simboloEntrada;
                    pilaSimbolos.add(cadenaSimbolo);
                    cadenaEntrada = cadenaEntrada.substring(simboloEntrada.length());
                    pilaEntrada.add(cadenaEntrada);
                    pilaAccion.add("Shift " + currentAccion);
                    pilaFinal.add(cadenaPila);
                    break;
                case 'r':
                    String[] regla = gramatica.get(numberStateN - 1).split(">");
                    String[] auxSimbolo = pilaSimbolos.get(j).split(regla[1]);
                    if (auxSimbolo.length == 0) {
                        cadenaSimbolo = regla[0];
                    } else {
                        cadenaSimbolo = auxSimbolo[0] + regla[0];
                    }
                    pilaSimbolos.add(cadenaSimbolo);
                    pilaEntrada.add(cadenaEntrada);
                    pilaAccion.add("Reduce " + gramatica.get(numberStateN - 1));
                    int count = returnValue(regla[1]);
                    for (int k = 0; k < count; k++) {
                        auxPila = pilaNumber.peek() + auxPila;
                        pilaNumber.pop();
                    }
                    String[] referenciaPila = cadenaPila.split(auxPila);
                    cadenaPila = referenciaPila[0];

                    int accionGoto = gotoTable[pilaNumber.peek()][noTerminales.indexOf(regla[0])];
                    pilaNumber.push(accionGoto);
                    cadenaPila += pilaNumber.peek();
                    pilaFinal.add(cadenaPila);
                    break;
                case 'A':
                    pilaAccion.add("Acceptar");
                    salida = false;
                    break;
                default:
                    break;
            }
            if(salida == false)
                break;
            j++;
        }
        System.out.println("Pila"+ "\t" + "\t" +"Simbolo"+ "\t" + "\t" + "Entrada"+ "\t" + "\t" +"Accion");
        for (int k=0; k<pilaFinal.size(); k++){
            System.out.println(pilaFinal.get(k) + "\t" + "\t" +"\t" + pilaSimbolos.get(k) + "\t" + "\t" +"\t" + pilaEntrada.get(k) + "\t" + "\t" +"\t" + pilaAccion.get(k));
        }
        }

    public static void Algoritmo(){
        System.out.println("Entra a Algoritmo");
        int j=0, count =0;
        String auxCurrentPila = "0";
        while (true){
            System.out.println("Entra ciclo j = " + j);
            String currentSimbolo = compareChar(pilaEntrada.get(j));
            System.out.println("Simbolo = " + currentSimbolo);
            String currentPila = accionTable[Integer.parseInt(auxCurrentPila)][terminales.indexOf(currentSimbolo)];
            System.out.println("Tabla de Acción = " + currentPila);
            auxCurrentPila = currentPila.substring(1);
            System.out.println("Numero de estado a moverse = " + auxCurrentPila);
            char currentChar = currentPila.charAt(0);
            System.out.println("Accion a realizar = " + currentChar);
            if(currentChar == 's'){
                System.out.println("Entra para realizar shift");
                count++;
                System.out.println("Número de count, sirve para ver cuantos numeros vamos a eliminar = " + count);
                pila.add(Integer.parseInt(auxCurrentPila));
                cadenaPila+=auxCurrentPila;
                pilaFinal.add(cadenaPila);
                System.out.println("Valor de pilaFinal = " + pilaFinal.get(j+1));
                System.out.println("Valor de pila en j + 1 = "+ j + 1 + " , " + pila.get(j+1));
                cadenaEntrada = cadenaEntrada.substring(currentSimbolo.length());
                System.out.println("cadenaEntrada = " + cadenaEntrada);
                pilaSimbolos.add(pilaSimbolos.get(j) + currentSimbolo);
                System.out.println("Pila Simbolo = " + pilaSimbolos.get(j + 1));
                pilaEntrada.add(cadenaEntrada);
                System.out.println("Pila Entrada = " + pilaEntrada.get(j + 1));
                pilaAccion.add("Shift  " + currentPila);
                System.out.println("Pila Accion = " + pilaAccion.get(j));
            }else if(currentChar == 'r'){
                System.out.println("Entra a hacer Reduce");
                int auxGramatica = Integer.parseInt(auxCurrentPila) -1;
                System.out.println("auxGramatica = " + auxGramatica);
                String[] currentGramatica = gramatica.get(auxGramatica).split(">");
                System.out.println(currentGramatica[0] + ">" + currentGramatica[1]);
                pilaAccion.add("Reduce " + gramatica.get(auxGramatica));
                System.out.println("Pila Accion = " + pilaAccion.get(j));

                String[] currentSimboloChange = pilaSimbolos.get(j).split(currentGramatica[1]);
                if(currentSimboloChange.length == 0)

                //currentSimbolo = currentGramatica[0];
                //int deleteValue = reduce(pilaSimbolos.get(j));
                //cadenaPila = invertir(pilaFinal.get(j), deleteValue);
                pilaFinal.add(cadenaPila);
                System.out.println("Pila final: " + pilaFinal.get(j +1));
                String auxPilaSimbolo = pilaSimbolos.get(j-1) + currentSimbolo;
                pilaEntrada.add(pilaEntrada.get(j));
                System.out.println("Pila Entrada: " + pilaEntrada.get(j+1));
                if(auxPilaSimbolo.equals(currentGramatica[1])){
                    pilaSimbolos.add(currentGramatica[0]);
                }else
                    pilaSimbolos.add(auxPilaSimbolo);
            }else if(currentChar == 'A'){
                pilaAccion.add("Aceptar");
                break;
            }
            j++;
        }
    }

    public static String compareChar(String currentPila){
        String aux = "" + currentPila.charAt(0);
        if(currentPila.length()>1){
            for (int x=0; x<currentPila.length();x++){
                if(noTerminales.indexOf(aux)>=0 && currentPila.charAt(x + 1) == '!'){
                    aux = aux + currentPila.charAt(x + 1);
                    break;
                } else if(noTerminales.indexOf(aux)<0 && terminales.indexOf(aux)<0){
                    aux = aux + currentPila.charAt(x + 1);
                } else if(noTerminales.indexOf(aux)>=0){
                    break;
                }else if (terminales.indexOf(aux)>=0) {
                    break;
                }
            }
        }
        return aux;
    }

    public static int returnValue(String entrada){
        int countAux = 0;
        while (!entrada.isEmpty()){
            String aux = compareChar(entrada);
            entrada = entrada.substring(aux.length());
            countAux ++;
        }
        return countAux;
    }

    public static void main(String[] args) throws IOException {
	 fillData();
    }
}
