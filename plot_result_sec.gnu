# Configuración básica
set terminal pngcairo size 1200,800 enhanced font 'Arial,12'
set output 'time_execution.png'

# Etiquetas del gráfico
set title "Crecimiento del tiempo de ejecución en función de N"
set xlabel "Número de puntos espaciales (N)"
set ylabel "Tiempo de ejecución (segundos)"
set grid

# Escala logarítmica para el eje X
set logscale x

# Estilo de la gráfica
set style data linespoints

# Archivo de datos y configuración del plot
plot 'test_results_sequential.txt' using 2:3 with linespoints title "Tiempo de ejecución"
