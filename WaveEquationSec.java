import java.io.FileWriter;
import java.io.IOException;

public class WaveEquationSec{
    // Constantes globales
    private static final int N = 100; // Hemos aumentado el N para afinar la curva
    private static final int T = 100000; // Número de pasos temporales
    private static final double C = 0.1; // Velocidad de propagación de la onda
    private static final double DX = 0.01; // Tamaño del paso espacial
    private static final double DT = 0.005; // Tamaño del paso temporal

    public static void main(String[] args) {
        // Inicialización de los datos
        double[] A = new double[N]; // Valores actuales
        double[] A_prev = new double[N]; // Valores en el paso anterior
        double[] A_next = new double[N]; // Valores en el siguiente paso

        // Medir el tiempo de ejecución
        long startTime = System.nanoTime();
        
        // Inicialización de la onda
        initializeWave(A, A_prev); 

        // Gestión de puntos frontera
        manageBoundaryConditions(A_next);
        
        // Iteración temporal
        for (int t = 0; t < T; t++) {
            // Cálculo de valores
            for (int i = 1; i < N - 1; i++) {
                A_next[i] = (2.0 * A[i]) - A_prev[i]
                        + (Math.pow(C * DT / DX, 2) * (A[i - 1] - 2.0 * A[i] + A[i + 1]));
            }

            // Actualización de arrays (paso al siguiente tiempo)
            System.arraycopy(A, 0, A_prev, 0, N);
            System.arraycopy(A_next, 0, A, 0, N);
            
            // Almacenamiento y visualización de resultados
           /*/ if (t % 100 == 0) {
                saveResultsToFile(A_next);
            }*/
        }
        long endTime = System.nanoTime();
        double executionTime = (endTime - startTime) / 1e9; // Convertir a segundos

        System.out.println("Simulación completada. Resultados guardados en wave_output_secuential.");
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

    // Gestión de los puntos frontera
    private static void manageBoundaryConditions(double[] A_next) {
        A_next[0] = 0.0; // Frontera izquierda
        A_next[N - 1] = 0.0; // Frontera derecha
    }

    // Almacenamiento de resultados

    private static void saveResultsToFile(double[] values) {
        try (FileWriter writer = new FileWriter("wave_output_sequential.txt", true)) {
            for (double value : values) {
                writer.write(value + " ");
            }
            writer.write("\n");
        } catch (IOException e) {
            System.err.println("Error al escribir resultados: " + e.getMessage());
        }
    }
    
}

