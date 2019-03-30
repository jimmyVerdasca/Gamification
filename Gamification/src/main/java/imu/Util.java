/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imu;

import Jama.Matrix;
import org.json.simple.JSONArray;

/**
 *
 * @author jimmy
 */
public class Util {
    public static double[] jsonArrayToArray(JSONArray array) {
        double[] result = new double[array.size()]; 
        for (int i = 0; i < array.size(); i++) {
            result[i] = Double.parseDouble(array.get(i).toString());
        }
        return result;
    }
    
    public static double[][] multiplyMatrices(double[][] firstMatrix, double[][] secondMatrix) {
        Matrix m1 = new Matrix(firstMatrix);
        Matrix m2 = new Matrix(secondMatrix);
        return (m1.times(m2)).getArray();
    }
    
    public static double[][] invertMatrix(double[][] matrix) {
        Matrix m = new Matrix(matrix);
        return m.inverse().getArray();
    }
    
    public static double[][] oneDArrayTo2DArray(double[] oneDimensionalArray, int widthWish, int heightWish) {
        if (oneDimensionalArray.length != (widthWish * heightWish)) {
            throw new IllegalArgumentException("width and height passed don't correspond to the array passed");
        }
        
        double[][] result = new double[heightWish][widthWish];
        for (int i = 0; i < heightWish; i++) {
            for (int j = 0; j < widthWish; j++) {
                result[i][j] = oneDimensionalArray[i * widthWish + j];
            }
        }
        return result;
    }
}
