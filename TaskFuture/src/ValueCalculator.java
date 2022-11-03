import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class ValueCalculator {
    private final int size = 1000000;
    private final int half = size / 2;
    private final float[] arr = new float[size];

    public void doCalc() {
        long start = System.currentTimeMillis();
        float[] a1 = new float[half];
        float[] a2 = new float[half];
        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Future<float[]>> futures = new ArrayList<>();

        Arrays.fill(arr, 1);
        System.arraycopy(arr, 0, a1, 0, half);
        System.arraycopy(arr, half, a2, 0, half);
        
        futures.add(executor.submit(changeValuesGetFutures(a1)));
        futures.add(executor.submit(changeValuesGetFutures(a2)));

        executor.shutdown();

        try {
            a1 = futures.get(0).get();
            a2 = futures.get(1).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        System.out.println(a1[0] + " " + a1[10] + " " + a1[half-1]);
        System.out.println(a2[0] + " " + a2[10] + " " + a2[half-1]);

        System.arraycopy(a1, 0, arr, 0, half);
        System.arraycopy(a2, 0, arr, half, half);

        System.out.printf("time execute program - %s milliseconds%n", System.currentTimeMillis() - start);
    }

    private Callable<float[]> changeValuesGetFutures(final float[] arr) {
        return () -> {
            float[] internalArray = new float[arr.length];
            for (int i = 0; i < arr.length; i++) {
                internalArray[i] = (float) (arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
            }
            return internalArray;
        };
    }
}