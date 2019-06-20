package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Utilitary class that helps writing data in a file.
 * 
 * We should use genericity to transform 4 method into only 1
 * 
 * @author jimmy
 */
public class DataFileUtil {
    
    /**
     * Write at the end of the given file the datas in the array "data".
     * 
     * @param data to write at the end of the file
     * @param fileName name of the file to create or append.
     * @throws IOException If an error occure while writing
     */
    public static void writeToFile(double[] data, String fileName) throws IOException {
        File file = new File(fileName);
        FileWriter fstream = new FileWriter(file, true);
        BufferedWriter out = new BufferedWriter(fstream);
        for (double d : data) {
            out.write(Double.toString(d) + ";");
        }
        out.write("\n");
        out.close();
    }
    /**
     * Write at the end of the given file the datas in the array "data".
     * 
     * @param data to write at the end of the file
     * @param fileName name of the file to create or append.
     * @throws IOException If an error occure while writing
     */
    public static void writeToFile(long[] data, String fileName) throws IOException {
        File file = new File(fileName);
        FileWriter fstream = new FileWriter(file, true);
        BufferedWriter out = new BufferedWriter(fstream);
        for (long d : data) {
            out.write(Long.toString(d) + ";");
        }
        out.write("\n");
        out.close();
    }
    
    /**
     * Write at the end of the given file the string data
     * 
     * @param data to write at the end of the file
     * @param fileName name of the file to create or append.
     * @throws IOException If an error occure while writing
     */
    public static void writeToFile(String data, String fileName) throws IOException {
        File file = new File(fileName);
        FileWriter fstream = new FileWriter(file, true);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(data);
        out.close();
    }
    
    /**
     * Write at the end of the given file the double data.
     * 
     * @param data to write at the end of the file
     * @param fileName name of the file to create or append.
     * @throws IOException If an error occure while writing
     */
    public static void writeToFile(double data, String fileName) throws IOException {
        File file = new File(fileName);
        FileWriter fstream = new FileWriter(file, true);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(Double.toString(data) + ";\n");
        out.close();
    }
}
