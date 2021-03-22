package src.datastructures;

public class PrintCollections {
    public static void printArray(char[] array) {
        int length = array.length;
        String coma;
        System.out.print("[");
        for(int i = 0; i < length; i++) {
            if(i != length-1)
                coma = ", ";
            else
                coma = "]";
            System.out.print(array[i] + coma);
        }
        System.out.println();
    }

    public static void printMatrix(int[][] matrix) {
        String coma;
        for(int i = 0; i < matrix.length; i++) {
            System.out.print("[");
            for(int j = 0; j < matrix[1].length; j++) {
                if(j != matrix[0].length - 1)
                    coma = ", ";
                else
                    coma = "]\n";
                System.out.print(matrix[i][j] + coma);
            }
        }
    }
}
