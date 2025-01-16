# Configuración básica
set terminal pngcairo size 1200,900 enhanced font 'Arial,12'
set output 'results_per_thread.png'

# Etiquetas
set title "Crecimiento del tiempo de ejecución en función de N para cada NUM_THREADS"
set xlabel "Número de puntos espaciales (N)"
set ylabel "Tiempo de ejecución (segundos)"
set logscale x
set grid

# Configuración de datos
set key outside
set style data linespoints

# Lista de valores de threads utilizados en el script de Python
threads_to_plot = "1 2 4 8 10 12 14 16 18 20 22 24 28 30 32 36 40 45 50"

# Generar una línea por cada valor de NUM_THREADS
plot for [t in threads_to_plot] \
    'test_results.txt' using ($1 == int(t) ? $2 : 1/0):($1 == int(t) ? $3 : 1/0) \
    with linespoints title sprintf("Threads: %s", t)
