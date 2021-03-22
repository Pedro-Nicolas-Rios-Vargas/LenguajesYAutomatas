package src.datastructures;

public class BinarySearch {


    public static boolean binarySearch(char[] array, int start, int end, char c) {
        if (end >= start) {
            int mid = start + (end - start)/2;

            if(array[mid] == c) {
                return true;
            }

            if(array[mid] > c) {
                return binarySearch(array,start, mid - 1, c);
            }

            if(array[mid] < c) {
                return binarySearch(array,mid + 1, end, c);
            }
        }

        return false;
    }

    public static int binarySearchOfIndex(char[] array, int start, int end, char c) {
        if(end >= start) {
            int mid = start + (end - start)/2;
            if(array[mid] == c)
                return mid;

            if(array[mid] > c)
                return binarySearchOfIndex(array, start, mid - 1, c);

            if(array[mid] < c)
                return binarySearchOfIndex(array, mid + 1, end, c);
        }
        return -1;
    }

}
