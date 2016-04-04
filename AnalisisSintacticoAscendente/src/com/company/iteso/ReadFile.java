package com.company.iteso;

import java.io.*;

/**
 * Created by houstonsalgado on 02/04/16.
 */
public class ReadFile {

    public static BufferedReader br;

    public ReadFile() throws FileNotFoundException {
        FileInputStream file;
        file = new FileInputStream("entrada.txt");
        DataInputStream entrada = new DataInputStream(file);
        br = new BufferedReader(new InputStreamReader(entrada));
    }

    public BufferedReader reader(){
        return  br;
    }
}
