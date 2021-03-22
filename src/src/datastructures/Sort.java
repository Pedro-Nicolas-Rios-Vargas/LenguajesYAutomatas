package src.datastructures;

public class Sort {

    private static char[] swap(char a, char b) {
        char aux = a;
        a = b;
        b = aux;
        return new char[] {a,b};
    }

    private static int partition(char[] array, int low, int high) {
        int pivot = array[high];
        int i = low - 1;
        char[] swapped;

        for(int j = low; j <= high - 1; j++) {
            if(array[j] < pivot) {
                i++;
                swapped = swap(array[i], array[j]);
                array[i] = swapped[0];
                array[j] = swapped[1];
            }
        }
        swapped = swap(array[i + 1], array[high]);
        array[i + 1] = swapped[0];
        array[high] = swapped[1];
        return i + 1;
    }

    public static void quick(char[] array, int low, int high) {
        if(low < high) {
            int pi = partition(array,low,high);

            quick(array,low,pi - 1);
            quick(array,pi + 1, high);
        }
    }
}
