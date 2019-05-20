/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author jimmy
 */
public class DataFileUtil {
    static long index = 0;
    public static void writeToFile(double[] data, String fileName) throws IOException {
        File file = new File(fileName);
        FileWriter fstream = new FileWriter(file, true);
        BufferedWriter out = new BufferedWriter(fstream);
        for (double d : data) {
            out.write(Double.toString(d) + ";" + Long.toString(index++) + ";\n");
        }
        out.close();
    }
    
    public static void writeToFile(long[] data, String fileName) throws IOException {
        File file = new File(fileName);
        FileWriter fstream = new FileWriter(file, true);
        BufferedWriter out = new BufferedWriter(fstream);
        for (long d : data) {
            out.write(Long.toString(d) + ";\n");
        }
        out.close();
    }
    
    public static void writeToFile(double data, String fileName) throws IOException {
        File file = new File(fileName);
        FileWriter fstream = new FileWriter(file, true);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(Double.toString(data) + ";\n");
        out.close();
    }
}
