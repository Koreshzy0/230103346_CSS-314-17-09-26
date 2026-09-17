import java.util.concurrent.ThreadLocalRandom;

public class Part2 {
    static final long TOTAL = 50_000_000L;
    static long totalHits = 0;

    static synchronized void incrementHits() {
        totalHits++;
    }

    static void singleThread() {
        long hits = 0;
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (long i = 0; i < TOTAL; i++) {
            double x = random.nextDouble();
            double y = random.nextDouble();

            if (x * x + y * y <= 1.0) {
                hits++;
            }
        }

        double pi = 4.0 * hits / TOTAL;
        System.out.printf("Single-thread: pi=%.6f%n", pi);
    }

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
                    incrementHits();
                }
            }
        }
    }

    static void synchronizedVersion() throws Exception {
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
            "Synchronized 4-thread: pi=%.6f, time=%.3f ms%n",
            pi,
            (end - start) / 1_000_000.0
        );
    }

    public static void main(String[] args) throws Exception {
        long start = System.nanoTime();
        singleThread();
        long end = System.nanoTime();

        System.out.printf(
            "Single-thread time: %.3f ms%n",
            (end - start) / 1_000_000.0
        );

        synchronizedVersion();
    }
}