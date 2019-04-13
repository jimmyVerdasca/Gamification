package imu;

import Jama.Matrix;
import org.json.simple.JSONArray;

/**
 * utilitary class giving access to some json and matrix methodes
 * @author jimmy
 */
public class Util {
    /**
     * transform a JSONArray to a double java array
     * @param array
     * @return 
     */
    public static double[] jsonArrayToArray(JSONArray array) {
        double[] result = new double[array.size()]; 
        for (int i = 0; i < array.size(); i++) {
            result[i] = Double.parseDouble(array.get(i).toString());
        }
        return result;
    }
    
    /**
     * multiply two 2D arrays as a matrice multiplication
     * @param firstMatrix
     * @param secondMatrix
     * @return the result of the matrix multiplication
     */
    public static double[][] multiplyMatrices(double[][] firstMatrix, double[][] secondMatrix) {
        Matrix m1 = new Matrix(firstMatrix);
        Matrix m2 = new Matrix(secondMatrix);
        return (m1.times(m2)).getArray();
    }
    
    /**
     * invert a 2D array as a matrix
     * @param matrix
     * @return the inverted array
     */
    public static double[][] invertMatrix(double[][] matrix) {
        Matrix m = new Matrix(matrix);
        return m.inverse().getArray();
    }
    
    /**
     * transform a simple 1D array to a 2D array with the width and height asked
     * @param oneDimensionalArray
     * @param widthWish
     * @param heightWish
     * @throws IllegalArgumentException if the width and
     *      height wished can't result on a proper matrix
     * @return the 2D array gererated
     */
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
