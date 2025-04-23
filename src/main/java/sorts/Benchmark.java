package sorts;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.BufferedWriter;
import java.io.FileWriter;

import util.Timer;

/**
 * Very basic benchmarking data collection class.
 * Creates arrays of certain cases (Sorted, Reverse Sorted, Random)
 * and handles the timing as well as .csv outputs.
 * Has some oversights in relation to data collected as does not implement
 * a way of gathering samples at certain input sizes, rather just total wall time.
 */

public class Benchmark {
    static String[] cases = {"Warmup", "Best", "Worst", "Random"};
    static String[] algorithms = {"Bubble", "Merge", "Quick", "Counting"};
    static List<String[]> results = new ArrayList<>();

    public static Integer[] generateInput(int n, String cs) {
        Integer[] arr = new Integer[n];
        switch (cs) {
            case "Warmup":
                for (int i = 0; i < n; i++) arr[i] = i;
                break;
            case "Best":
                for (int i = 0; i < n; i++) arr[i] = i;
                break;
            case "Worst":
                for (int i = 0; i < n; i++) arr[i] = n - i;
                break;
            case "Random":
                Random rand = new Random(1);
                arr = rand.ints(n, 0, n * 10).distinct().limit(n).boxed().toArray(Integer[]::new);
                break;
        }
        return arr;
    }

    public static double timeSort(String algo, Integer[] arr, int n, String cs) {
        List<Double> times = new ArrayList<>();
        Comparator<Integer> comp = Integer::compareTo;
        for (int i = 0; i < Timer.MIN_REPEATS; i++) {
            Integer[] arrCopy = Arrays.copyOf(arr, arr.length);
            Runnable worker = () -> {
                switch (algo) {
                    case "Bubble": SortingAlgorithms.bubbleSort(arrCopy, comp); break;
                    case "Merge": SortingAlgorithms.mergeSort(arrCopy, comp); break;
                    case "Quick": SortingAlgorithms.quickSort(arrCopy, comp); break;
                }
            };
            double nanos = Timer.measure(worker);
            times.add(nanos);
        }
        double avgNanos = times.stream().mapToDouble(d -> d).average().orElse(0);
        if (!cs.equals("Warmup")) {
            results.add(new String[]{algo, String.valueOf(n), cs, String.format("%.6f", avgNanos)});
        }
        return avgNanos;
    }

    public static double timeCountingSort(int[] arr, int n, String cs) {
        List<Double> times = new ArrayList<>();
        for (int i = 0; i < Timer.MIN_REPEATS; i++) {
            int[] arrCopy = Arrays.copyOf(arr, arr.length);
            Runnable worker = () -> SortingAlgorithms.countingSort(arrCopy);
            double nanos = Timer.measure(worker);
            times.add(nanos);
        }
        double avgNanos = times.stream().mapToDouble(d -> d).average().orElse(0);
        if (!cs.equals("Warmup")) {
            results.add(new String[]{"Counting", String.valueOf(n), cs, String.format("%.6f", avgNanos)});
        }
        return avgNanos;
    }

    public static void writeTimingToCSV(String filename, List<String[]> rows){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("Algorithm,Size,Case,Time (s),System Time (ms)\n");
            for (String[] row : rows) {
                writer.write(String.join(",", row) + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int[] sizes = new int[100];
        long start = 0, end = 0;
        int caseI = 0;
        String algo = algorithms[3]; // Bubble = 0, Merge = 1, Quick = 2, Counting = 3
        long[][] times = new long[4][2];
        Timer.MIN_REPEATS = 35;
        Timer.MAX_REPEATS = 35;


        for (int i = 0; i < 100; i++) {
            sizes[i] = (i + 1) * 1000;
        } // 100 samples, increment of 1000. Adjust if too slow, or adjust Timer.MIN/MAX_REPEATS
          // to change the sample amount.

        for (String cs : cases) {
            boolean first = true;
            for (int n : sizes) {
                Integer[] arr = generateInput(n, cs);
                int[] arrInt = Arrays.stream(arr).mapToInt(Integer::intValue).toArray();
                if (first) {
                    start = System.currentTimeMillis();
                    first = false;
                }
//                double avgNanos = timeSort(algo, arr, n, cs);
                double avgNanos = timeCountingSort(arrInt, n, cs);
                System.out.printf("%s | %d | %s | %.6f s\n", algo, n, cs, avgNanos);
            }
            end = System.currentTimeMillis();
            times[caseI] = new long[]{start, end};
            caseI++; // Best (Sorted) = 0, Worst (Reverse Sorted) = 1, Random (Worst for Counting) = 2
            Thread.sleep(3000);
        }

        writeTimingToCSV("sorting_benchmarks.csv", results);
        System.out.print("\nBenchmark Finished.");
        for (long[] time : times) {
            // Prints start and end system times prior to each case.
            // To be used to filter out the energy consumption of the system from the logger file.
            System.out.printf("\nStart: " + new Date(time[0]) + "  End: " + new Date(time[1]));
        }
    }
}
