package sorts;

import java.util.*;

/**
 * Sorting Algorithms. Array inputs.
 * Taken from previous labs or other resources as generic algorithms.
 */
public class SortingAlgorithms {
    public static <T extends Comparable<T>> void mergeSort(T[] array, Comparator<T> comparator) {
        if (array.length < 2) return;
        mergeSortRecursive(array, 0, array.length - 1, comparator);
    }

    private static <T extends Comparable<T>> void mergeSortRecursive(T[] array, int left, int right, Comparator<T> comparator) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSortRecursive(array, left, mid, comparator);
            mergeSortRecursive(array, mid + 1, right, comparator);
            merge(array, left, mid, right, comparator);
        }
    }

    private static <T extends Comparable<T>> void merge(T[] array, int left, int mid, int right, Comparator<T> comparator) {
        T[] leftArray = Arrays.copyOfRange(array, left, mid + 1);
        T[] rightArray = Arrays.copyOfRange(array, mid + 1, right + 1);
        int i = 0, j = 0, k = left;

        while (i < leftArray.length && j < rightArray.length) {
            if (comparator.compare(leftArray[i], rightArray[j]) <= 0) {
                array[k++] = leftArray[i++];
            } else {
                array[k++] = rightArray[j++];
            }
        }
        while (i < leftArray.length) array[k++] = leftArray[i++];
        while (j < rightArray.length) array[k++] = rightArray[j++];
    }

    public static <T extends Comparable<T>> void quickSort(T[] array, Comparator<T> comparator) {
        quickSortRecursive(array, 0, array.length - 1, comparator);
    }

    private static <T extends Comparable<T>> void quickSortRecursive(T[] array, int low, int high, Comparator<T> comparator) {
        if (low < high) {
            int pi = partition(array, low, high, comparator);
            quickSortRecursive(array, low, pi - 1, comparator);
            quickSortRecursive(array, pi + 1, high, comparator);
        }
    }

    private static <T extends Comparable<T>> int partition(T[] array, int low, int high, Comparator<T> comparator) {
        T pivot = array[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (comparator.compare(array[j], pivot) <= 0) {
                i++;
                T temp = array[i]; array[i] = array[j]; array[j] = temp;
            }
        }
        T temp = array[i + 1]; array[i + 1] = array[high]; array[high] = temp;
        return i + 1;
    }

    public static void countingSort(int[] array) {
        if (array.length == 0) return;
        int max = Arrays.stream(array).max().getAsInt();
        int min = Arrays.stream(array).min().getAsInt();
        int range = max - min + 1;

        int[] count = new int[range];
        int[] output = new int[array.length];

        for (int i : array) count[i - min]++;
        for (int i = 1; i < count.length; i++) count[i] += count[i - 1];
        for (int i = array.length - 1; i >= 0; i--) {
            output[count[array[i] - min] - 1] = array[i];
            count[array[i] - min]--;
        }

        System.arraycopy(output, 0, array, 0, array.length);
    }

    public static <T extends Comparable<T>> void bubbleSort(T[] array, final Comparator<T> comparator) {
        if (array == null || array.length <= 1) {
            return;
        }
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - i - 1; j++) {
                if (comparator.compare(array[j], array[j + 1]) > 0) {
                    T temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
    }

    public static void main(String[] args) {

    }
}