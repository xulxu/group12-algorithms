package util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
//import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 * Timer class for measuring the execution time of a function.
 * Taken from Data Structures' util package.
 */

public class Timer {
    static Logger log = Logger.getLogger("Timer");
    /*
     * This is a class for timing the execution of a function.
     * Runs the function at least MIN_REPEATS times, never more
     * than MAX_REPEATS times and stops when the execution time
     * exceeds MIN_SECONDS.
     */
    public static int MIN_REPEATS = 10; // repeat the execution at least this number of times
    public static int MAX_REPEATS = 100000; // repeat the execution at least this number of times
    public static long MIN_SECONDS = (long) (5 * 1e9); // repeat execution of the work
    // until at least this amount of time

    public static double measure(Runnable worker) {
        long n_repeats = 0;
        long start = System.nanoTime();

        while (true) {
            worker.run(); // do work
            ++n_repeats;

            if ( n_repeats > Timer.MIN_REPEATS && n_repeats % Timer.MIN_REPEATS == 0) { // has to run a minimum number of times
                var delta_t = (System.nanoTime() - start);
                if (n_repeats >= Timer.MAX_REPEATS || delta_t > Timer.MIN_SECONDS ) {
                    //System.out.println(n_repeats + ":" + ((1.0*delta_t)/1e9) + " [" + (n_repeats > Timer.MIN_REPEATS) + ", " + (n_repeats >= Timer.MAX_REPEATS) + ", " + (delta_t > Timer.MIN_SECONDS) + "]");
                    break;
                }
            }
        }
        long elapsed = System.nanoTime() - start;

        // System.out.println("# elapsed: " + elapsed + ", " + elapsed/1e9 + " -> " + "n_repeats:" + " " + n_repeats);
        return 1e-9 * elapsed / n_repeats;
    }

    public static void main(String [] args) {
        main_sort_ds(args);
    }

    public static boolean isSorted(Integer[] array) {
        return IntStream.range(0, array.length - 1).allMatch(i -> array[i] <= array[i + 1]);
    }

    public static void main_sort_ds(String[] args) {
        Random rnd = new Random();
        rnd.setSeed(1024);
        Timer.MIN_SECONDS = (long) (3*1e9);
        //Timer.MAX_REPEATS = 1000000000;
        //int size = 100, lowBound=0, highBound = 1000;

        //int n_min = 100, n_max = 100000, n_samples = 10;
        //int n_min = 1000, n_max = 5000000, n_samples = 50;

        Timer.MAX_REPEATS = 1;
        Timer.MIN_REPEATS = 1;
        Timer.MIN_SECONDS = 0;
        int n_min = 20, n_max = 30, n_samples = 1;

        // we want to even divide the range on a log scale
        // n_min^{1+alpha*0}, ..., n_min^{1+alpha*i}, ..., n_min^{1 + alpha(n-1)} = n_max
        // alpha = (log(n_max)/log(n_min) - 1) / (n_samples - 1)
        double alpha = ( (Math.log(n_max) / Math.log(n_min)) - 1) / (n_samples-1);

        for(int i = 0; i < n_samples; ++i) {
            int n = (int) Math.pow(n_min, (1 + i * alpha));
            //int [] arr = new Random().ints(n).toArray();
            Integer [] arr = new Random().ints(n).distinct().boxed().toArray(Integer[]::new);
            //Integer[] what = Arrays.stream( data ).boxed().toArray( Integer[]::new );
            //Arrays.sort(arr);
            //MergeSort ob = new MergeSort();


            Runnable worker_arrays = () -> {
                Integer [] c_arr = Arrays.copyOf(arr, arr.length);
                Arrays.sort(c_arr);
//                boolean eq = isSorted(c_arr);
//                System.out.println(eq);
//                if(eq == false) {
//                    System.out.println(Arrays.toString(c_arr));
//                }
            };


            double result = Timer.measure(worker_arrays);

            System.out.println(i + "\t" + n + "\t" + result);
        }
    }
}
