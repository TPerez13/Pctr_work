import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WaveEquationPar {
    // Constantes globales
    private static final int N = 5000000; // Número de puntos espaciales
    private static final int T = 10000; // Número de pasos temporales
    private static final double C = 0.1; // Velocidad de la onda
    private static final double DX = 0.01; // Tamaño del paso espacial
    private static final double DT = 0.005; // Tamaño del paso temporal
    private static final int NUM_THREADS = 15; // Número de hilos en el pool
    public static void main(String[] args) {
        // Declaración de arrays
        double[] A = new double[N];
        double[] A_prev = new double[N];
        double[] A_next = new double[N];

        // Medir el tiempo de ejecución
        long startTime = System.nanoTime();

        // Inicialización de la onda
        initializeWave(A, A_prev);

        // Gestión de puntos frontera
        manageBoundaryConditions(A_next);

        // Crear un ThreadPoolExecutor
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(NUM_THREADS);

        // Crear un CyclicBarrier para sincronizar los hilos
        CyclicBarrier barrier = new CyclicBarrier(NUM_THREADS+1);

        // Iteración temporal
        for (int t = 0; t < T; t++) {
            // Dividir el dominio espacial entre hilos
            int dominio = (N - 2) / NUM_THREADS;
            for (int threadId = 0; threadId < NUM_THREADS; threadId++) {
                final int start = 1 + threadId * dominio;
                final int end = (threadId == NUM_THREADS - 1) ? N - 1 : start + dominio;

                executor.submit(() -> {
                    // Actualizar valores en este dominio
                    updateWave(A, A_prev, A_next, start, end);

                    // Esperar a que todos los hilos terminen este paso temporal
                    try {
                        barrier.await();
                    } catch (Exception e) {
                        System.err.println("Error en la barrera: " + e.getMessage());
                    }
                });
            }
            try {
                barrier.await();
            } catch (Exception e) {
                System.err.println("Error en la barrera: " + e.getMessage());
            }
            // Intercambio de buffers (una vez todos los hilos han terminado)
            System.arraycopy(A, 0, A_prev, 0, N);
            System.arraycopy(A_next, 0, A, 0, N);

            // Guardar resultados cada 100 pasos temporales
            if (t % 100 == 0) {
                saveResultsToFile(A_next);
            }
        }

        // Cerrar el ThreadPoolExecutor al final
        executor.shutdown();

        long endTime = System.nanoTime();
        double executionTime = (endTime - startTime) / 1e9; // Convertir a segundos

        System.out.println("Simulación completada. Resultados guardados en wave_output_parallel.txt.");
        System.out.printf("Tiempo de ejecución: %.6f segundos\n", executionTime);
    }

    // Inicialización de los datos
    private static void initializeWave(double[] A, double[] A_prev) {
        for (int i = 0; i < N; i++) {
            double x = i * DX;
            A[i] = Math.sin(Math.PI * x); // Forma inicial de la onda (sinusoidal)
            A_prev[i] = A[i]; // Copia inicial para el paso anterior
        }
    }

    // Actualización de los valores en un paso temporal
    private static void updateWave(double[] values, double[] oldValues, double[] newValues, int start, int end) {
        double coeff = (C * C * DT * DT) / (DX * DX);
        for (int i = start; i < end; i++) {
            newValues[i] = (2.0 * values[i]) - oldValues[i] + coeff * (values[i - 1] - 2.0 * values[i] + values[i + 1]);
        }
    }
    
    // Gestión de los puntos frontera
    private static void manageBoundaryConditions(double[] A_next) {
        A_next[0] = 0.0; // Frontera izquierda
        A_next[N - 1] = 0.0; // Frontera derecha
    }
    
    // Escribir los resultados en un archivo
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
