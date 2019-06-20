package util;

/**
 * utilitary class helping manipulate arrays
 * 
 * @author jimmy
 */
public class ArrayUtil {
    
    /**
     * Return the index of the maximum value in the given array
     * 
     * @param array that we iterate on
     * @return the index of the maximum value in the given array
     */
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
    
    /**
     * Return the maximum value in a given array.
     * 
     * @param array that we iterate on
     * @return the maximum value in the array
     */
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
    
    /**
     * Return the minimum value in a given array.
     * 
     * @param array that we iterate on
     * @return the minimum value in the array
     */
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
    
    /**
     * Sort the k biggest element of the given array (modify the array)
     * and return the kth element.
     * 
     * @param array to sort the k biggest element
     * @param length length of the array
     * @param k number of element we want to sorted
     * @return the kth biggest element of the array.
     */
    public static double getKLargest(double[] array, int length, int k){
        double temp;
        for (int i = 0; i < k; i++)
        {
            for (int j = i + 1; j < length; j++)
            {
                if (array[j] > array[i])
                {
                    temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;
                }
            }
        }
        return array[k - 1];
    }
    
    /**
     * Sort the k smallest element of the given array (modify the array)
     * and return the kth element.
     * 
     * @param array to sort the k smallest element
     * @param length length of the array
     * @param k number of element we want to sorted
     * @return the kth smallest element of the array.
     */
    public static double getKSmallest(double[] array, int length, int k){
        double temp;
        for (int i = 0; i < k; i++)
        {
            for (int j = i + 1; j < length; j++)
            {
                if (array[j] < array[i])
                {
                    temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;
                }
            }
        }
        return array[k - 1];
    }
}
