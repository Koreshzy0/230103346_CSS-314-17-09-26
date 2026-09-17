import java.util.concurrent.ThreadLocalRandom;

public class Part1 {
    static long totalHits = 0;
    static final long TOTAL = 50_000_000L;

    static class Worker extends Thread {
        private final long iterations;

        Worker(long iterations) {
            this.iterations = iterations;
        }

        public void run() {
            ThreadLocalRandom random = ThreadLocalRandom.current();

            for (long i = 0; i < iterations; i++) {
                double x = random.nextDouble();
                double y = random.nextDouble();

                if (x * x + y * y <= 1.0) {
                    totalHits++;
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        for (int run = 1; run <= 5; run++) {
            totalHits = 0;

            Worker[] workers = new Worker[4];
            long iterationsPerThread = TOTAL / 4;

            long start = System.nanoTime();

            for (int i = 0; i < 4; i++) {
                workers[i] = new Worker(iterationsPerThread);
                workers[i].start();
            }

            for (Worker worker : workers) {
                worker.join();
            }

            long end = System.nanoTime();

            double pi = 4.0 * totalHits / TOTAL;

            System.out.printf(
                "Run %d: hits=%d, pi=%.6f, time=%.3f ms%n",
                run, totalHits, pi, (end - start) / 1_000_000.0
            );
        }
    }
}