import subprocess

def run_test(n_values, output_file):
    num_threads = 1  # Número de threads fijo
    results = []
    
    with open(output_file, "w") as file:
        # Escribir encabezados
        file.write("NUM_THREADS, N, Time (seconds)\n")
        
        for n in n_values:
            print(f"Ejecutando con NUM_THREADS={num_threads} y N={n}...")
            process = subprocess.run(
                ["java", "WaveEquationSec2", str(n)],
                capture_output=True,
                text=True
            )
            if process.returncode == 0:
                output = process.stdout.splitlines()
                for line in output:
                    if "Tiempo de ejecución:" in line:
                        execution_time = float(line.split()[-2].replace(',', '.'))
                        results.append((num_threads, n, execution_time))
                        # Guardar en el archivo
                        file.write(f"{num_threads}, {n}, {execution_time:.6f}\n")
                        print(f"Completado: NUM_THREADS={num_threads}, N={n}, Tiempo={execution_time:.6f} segundos")
            else:
                print(f"Error al ejecutar para N={n}: {process.stderr}")
    return results

if __name__ == "__main__":
    n_values = [100, 1000, 100000, 1000000, 10000000, 100000000]  # Valores de N para probar
    output_file = "test_results_sequential.txt"
    
    results = run_test(n_values, output_file)
    
    print(f"\nResultados guardados en {output_file}")
