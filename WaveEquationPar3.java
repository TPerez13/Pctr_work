import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class WaveEquationPar3 {
    // Constantes globales
    private static final int T = 1000; // Número de pasos temporales
    private static final double C = 0.1; // Velocidad de la onda
    private static final double DX = 0.01; // Tamaño del paso espacial
    private static final double DT = 0.005; // Tamaño del paso temporal

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Uso: java WaveEquationPar2 <NUM_THREADS> <N>");
            return;
        }

        int numThreads = Integer.parseInt(args[0]);
        int N = Integer.parseInt(args[1]);

        double[] A = new double[N];
        double[] A_prev = new double[N];
        double[] A_next = new double[N];

        long startTime = System.nanoTime();

        initializeWave(A, A_prev, N);
        manageBoundaryConditions(A_next, N);

        Semaphore semaphore = new Semaphore(numThreads);
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);

        for (int t = 0; t < T; t++) {
            semaphore.drainPermits();
            int domain = (N - 2) / numThreads;

            for (int threadId = 0; threadId < numThreads; threadId++) {
                final int start = 1 + threadId * domain;
                final int end = (threadId == numThreads - 1) ? N - 1 : start + domain;

                executor.submit(() -> {
                    for (int i = start; i < end; i++) {
                        A_next[i] = (2.0 * A[i]) - A_prev[i] +
                                    (Math.pow(C * DT / DX, 2) * (A[i - 1] - 2.0 * A[i] + A[i + 1]));
                    }
                    semaphore.release();
                });
            }

            try {
                semaphore.acquire(numThreads);
                semaphore.release(numThreads);
            } catch (InterruptedException e) {
                System.err.println("Error al esperar en el semáforo: " + e.getMessage());
            }

            System.arraycopy(A, 0, A_prev, 0, N);
            System.arraycopy(A_next, 0, A, 0, N);

            /*if (t % 100 == 0) {
                saveResultsToFile(A_next);
            }*/
        }

        executor.shutdown();

        long endTime = System.nanoTime();
        double executionTime = (endTime - startTime) / 1e9;

        System.out.printf("Simulación completada en %.6f segundos\n", executionTime);
    }

    private static void initializeWave(double[] A, double[] A_prev, int N) {
        for (int i = 0; i < N; i++) {
            double x = i * DX;
            A[i] = Math.sin(Math.PI * x);
            A_prev[i] = A[i];
        }
    }

    private static void manageBoundaryConditions(double[] A_next, int N) {
        A_next[0] = 0.0;
        A_next[N - 1] = 0.0;
    }

    private static void saveResultsToFile(double[] values) {
        try (FileWriter writer = new FileWriter("wave_output_parallel.txt", true)) {
            for (double value : values) {
                writer.write(value + " ");
            }
            writer.write("\n");
        } catch (IOException e) {
            System.err.println("Error al escribir resultados: " + e.getMessage());
        }
    }
}
