import subprocess

def run_test(n_values, thread_values, output_file):
    java_program = "WaveEquationPar3"
    results = []
    
    with open(output_file, "w") as file:
        # Escribir encabezados
        file.write("NUM_THREADS, N, Time (seconds)\n")
        
        for threads in thread_values:
            for n in n_values:
                print(f"Ejecutando con NUM_THREADS={threads} y N={n}...")
                process = subprocess.run(
                    ["java", java_program, str(threads), str(n)],
                    capture_output=True,
                    text=True
                )
                if process.returncode == 0:
                    output = process.stdout.splitlines()
                    for line in output:
                        if "Simulación completada en" in line:
                            execution_time = float(line.split()[-2].replace(',', '.'))
                            results.append((threads, n, execution_time))
                            # Guardar en el archivo
                            file.write(f"{threads}, {n}, {execution_time:.6f}\n")
                            print(f"Completado: NUM_THREADS={threads}, N={n}, Tiempo={execution_time:.6f} segundos")
                else:
                    print(f"Error al ejecutar para NUM_THREADS={threads}, N={n}: {process.stderr}")
    
    return results

if __name__ == "__main__":
    n_values = [100, 1000, 100000, 1000000, 10000000, 100000000]
    thread_values = [1, 2, 4, 8, 10, 12, 14, 16, 18, 20, 22, 24, 28, 30, 32, 36, 40, 45, 50]  # Diferentes números de threads para probar
    output_file = "test_results.txt"
    
    results = run_test(n_values, thread_values, output_file)
    
    print(f"\nResultados guardados en {output_file}")
