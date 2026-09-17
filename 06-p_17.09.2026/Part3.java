import java.util.concurrent.ThreadLocalRandom;

public class Part3 {

    static final long TOTAL = 100_000_000L;

    static class Worker extends Thread {
        private final long iterations;
        long localHits = 0;

        Worker(long iterations) {
            this.iterations = iterations;
        }

        public void run() {
            ThreadLocalRandom random = ThreadLocalRandom.current();

            for (long i = 0; i < iterations; i++) {
                double x = random.nextDouble();
                double y = random.nextDouble();

                if (x * x + y * y <= 1.0) {
                    localHits++;
                }
            }
        }
    }

    static double runTest(int threads) throws Exception {
        Worker[] workers = new Worker[threads];

        long base = TOTAL / threads;
        long remainder = TOTAL % threads;

        long start = System.nanoTime();

        for (int i = 0; i < threads; i++) {
            long iterations = base + (i < remainder ? 1 : 0);

            workers[i] = new Worker(iterations);
            workers[i].start();
        }

        long totalHits = 0;

        for (Worker worker : workers) {
            worker.join();
            totalHits += worker.localHits;
        }

        long end = System.nanoTime();

        double pi = 4.0 * totalHits / TOTAL;
        double runtimeMs = (end - start) / 1_000_000.0;

        System.out.printf(
            "Threads=%d | Runtime=%.3f ms | Pi=%.6f%n",
            threads, runtimeMs, pi
        );

        return runtimeMs;
    }

    public static void main(String[] args) throws Exception {

        int[] threadCounts = {1, 2, 4, 8, 16, 32};

        double baseline = 0;

        for (int threads : threadCounts) {

            // Warm-up
            if (threads == 1) {
                runTest(threads);
                System.out.println("Starting benchmark...");
            }

            double runtime = runTest(threads);

            if (threads == 1) {
                baseline = runtime;
            }

            double speedup = baseline / runtime;
            double efficiency = speedup / threads * 100.0;

            System.out.printf(
                "Speedup=%.3fX | Efficiency=%.2f%%%n%n",
                speedup, efficiency
            );
        }
    }
}