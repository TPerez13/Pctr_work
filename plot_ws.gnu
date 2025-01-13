set terminal pngcairo size 800,600 enhanced font 'Verdana,10'
set output 'wave_plot_sequential.png'
set title "Evolución de la Ecuación de Onda Unidimensional"
set xlabel "Posición"
set ylabel "Amplitud"
set grid
set key outside

plot for [i=2:10] 'wave_output_sequential.txt' using 0:i with lines title sprintf("Timestep %d", i-1)