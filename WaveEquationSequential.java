/**
 * Clase WaveEquation2 que implementa la simulación de la ecuación de ondas unidimensional.
 * Se utiliza un esquema explícito para calcular la evolución de la onda en el tiempo.
 */
import java.io.FileWriter;
import java.io.IOException;

public class WaveEquationSequential {

    /**
     * Número de puntos espaciales.
     */
    private static final int N = 10000;

    /**
     * Número de pasos temporales.
     */
    private static final int T = 10000;

    /**
     * Velocidad de propagación de la onda.
     */
    private static final double C = 0.1;

    /**
     * Tamaño del paso espacial.
     */
    private static final double DX = 0.01;

    /**
     * Tamaño del paso temporal.
     */
    private static final double DT = 0.005;

    /**
     * Método principal que ejecuta la simulación de la ecuación de ondas.
     * @param args argumentos de línea de comando (no utilizados).
     */
    public static void main(String[] args) {
        // Inicialización de los datos
        double[] A = new double[N]; // Valores actuales
        double[] A_prev = new double[N]; // Valores en el paso anterior
        double[] A_next = new double[N]; // Valores en el siguiente paso

        initializeWave(A, A_prev); // Inicialización de los valores

        // Gestión de puntos frontera
        manageBoundaryConditions(A_next);

        // Iteración temporal
        for (int t = 0; t < T; t++) {
            // Cálculo de valores
            for (int i = 1; i < N - 1; i++) {
                A_next[i] = (2.0 * A[i]) - A_prev[i]
                        + (Math.pow(C * DT / DX, 2) * (A[i - 1] - 2.0 * A[i] + A[i + 1]));
            }

            // Almacenamiento y visualización de resultados
            if (t % 100 == 0) {
                saveResultsToFile(A_next, t);
            }

            // Actualización de arrays (paso al siguiente tiempo)
            System.arraycopy(A, 0, A_prev, 0, N);
            System.arraycopy(A_next, 0, A, 0, N);
        }

        System.out.println("Simulación completada. Resultados almacenados.");
    }

    /**
     * Inicializa los valores iniciales de la onda en los arreglos.
     * @param A arreglo que contiene los valores actuales de la onda.
     * @param A_prev arreglo que contiene los valores de la onda en el paso anterior.
     */
    private static void initializeWave(double[] A, double[] A_prev) {
        for (int i = 0; i < N; i++) {
            double x = i * DX;
            A[i] = Math.sin(Math.PI * x); // Forma inicial de la onda (sinusoidal)
            A_prev[i] = A[i]; // Copia inicial para el paso anterior
        }
    }

    /**
     * Aplica las condiciones de frontera a los extremos del dominio.
     * @param A_next arreglo que contiene los valores calculados de la onda.
     */
    private static void manageBoundaryConditions(double[] A_next) {
        A_next[0] = 0.0; // Frontera izquierda
        A_next[N - 1] = 0.0; // Frontera derecha
    }

    /**
     * Guarda los resultados de la simulación en un archivo.
     * @param A arreglo que contiene los valores actuales de la onda.
     * @param timestep el paso temporal actual.
     */
    private static void saveResultsToFile(double[] A, int timestep) {
        try (FileWriter writer = new FileWriter("wave_sequential_results.txt", true)) {
            for (double value : A) {
                writer.write(value + " ");
            }
            writer.write("\n");
        } catch (IOException e) {
            System.err.println("Error al escribir resultados: " + e.getMessage());
        }
    }
}
