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

    public static double findMax(double[] array) {
        double max = array[0];
        for (int i = 1; i < array.length; i++) 
        {
                if (max < array[i]) 
                {
                        max = array[i];
                }
        }
        return max;
    }
    
    public static double findMin(double[] array) {
        double min = array[0];
        for (int i = 1; i < array.length; i++) 
        {
                if (min > array[i]) 
                {
                        min = array[i];
                }
        }
        return min;
    }
}
