/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author jimmy
 */
public class ArrayUtil {
    public static int findIndexOfMaxIn(double[] array){
        double max = array[0];
        int index = 0;
        for (int i = 1; i < array.length; i++) 
        {
                if (max < array[i]) 
                {
                        max = array[i];
                        index = i;
                }
        }
        return index;
    }
}
