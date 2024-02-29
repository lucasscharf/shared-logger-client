# name
# sem_throughputReplica (avg)(kCommands/s)
# sem_throughputReplica (med)(kCommands/s)
# sem_throughputReplica (p95)(kCommands/s)
# sem_latency (avg)(ms)
# sem_latency (med)(ms)
# sem_latency (p95)(ms)
# cou_throughputReplica (avg)(kCommands/s)
# cou_throughputReplica (med)(kCommands/s)
# cou_throughputReplica (p95)(kCommands/s)
# cou_latency (avg)(ms)
# cou_latency (med)(ms)
# cou_latency (med)(ms)
# dec_throughputReplica (avg)(kCommands/s)
# dec_throughputReplica (med)(kCommands/s)
# dec_throughputReplica (p95)(kCommands/s)
# dec_latency (avg)(ms)
# dec_latency (med)(ms)
# dec_latency (p95)(ms)
#


title_io <- "E/S Intensiva"
csv_text_io <- "io_8_90_001,0.25,0.25,0.26,2.32,2.28,2.43,0.25,0.24,0.26,2.38,2.38,2.47,0.25,0.25,0.26,2.79,2.78,3.09
io_16_90_001,0.51,0.51,0.53,5.43,2.03,35.54,0.50,0.50,0.52,2.05,1.98,2.35,0.50,0.50,0.52,2.13,2.14,2.59
io_32_90_001,1.02,1.02,1.04,1.76,1.76,2.03,1.00,1.00,1.02,2.20,1.95,3.34,1.02,1.02,1.04,1.88,1.86,2.25
io_64_90_001,1.96,2.01,2.08,1.96,1.62,2.47,1.95,1.97,2.02,2.44,2.30,3.78,1.99,2.00,2.03,2.40,2.21,3.91
io_128_90_001,3.88,3.90,3.96,3.95,3.20,4.40,3.88,3.97,4.09,3.36,2.14,7.99,3.87,3.89,3.95,4.27,3.15,4.92
io_256_90_001,7.50,7.76,7.95,4.03,2.87,5.72,4.65,4.77,6.14,26.09,21.10,63.30,6.24,6.95,8.01,22.63,2.25,16.56
io_512_90_001,8.78,8.87,10.68,25.61,17.91,62.11,5.25,5.26,6.27,63.92,55.04,125.09,9.66,10.29,12.15,22.93,15.90,56.75
io_1024_90_001,9.93,10.22,12.18,77.33,66.00,148.81,4.72,4.69,6.21,153.00,134.37,257.16,9.07,9.36,11.63,76.75,65.13,155.29
io_2048_90_001,9.23,9.48,11.77,193.37,169.65,354.92,5.67,5.96,6.89,311.04,273.10,440.18,9.23,10.06,12.02,179.35,163.27,296.72"

title_cpu <- "CPU Intensiva"
csv_text_cpu <- "cpu_8_90_001,0.25,0.25,0.27,1.96,1.97,2.03,0.25,0.25,0.26,2.27,2.21,2.42,0.25,0.25,0.26,4.64,2.23,9.57
cpu_16_90_001,0.51,0.51,0.52,1.92,1.93,2.11,0.51,0.51,0.52,1.90,1.92,2.06,0.49,0.50,0.51,1.91,1.95,2.15
cpu_32_90_001,1.03,1.03,1.06,1.48,1.45,1.92,1.00,1.00,1.02,2.70,2.49,4.30,1.03,1.03,1.05,1.58,1.59,1.82
cpu_64_90_001,1.98,2.05,2.11,2.09,1.41,1.99,1.99,1.99,2.03,2.35,2.19,3.60,2.04,2.05,2.07,2.11,1.46,2.52
cpu_128_90_001,4.05,4.07,4.10,1.87,1.57,2.97,3.95,3.98,4.03,2.70,2.51,4.46,4.04,4.05,4.11,1.90,1.75,2.86
cpu_256_90_001,6.16,7.11,7.97,2.12,1.88,3.50,7.81,7.80,8.04,3.32,2.59,6.11,8.05,8.09,8.16,2.35,1.82,3.77
cpu_512_90_001,15.17,16.16,16.54,1.87,1.39,3.46,10.94,10.96,13.33,17.04,11.04,51.12,16.17,16.24,16.61,1.65,1.25,2.20
cpu_1024_90_001,30.55,31.66,32.85,1.97,1.54,3.25,10.54,10.42,12.55,67.27,58.25,118.49,17.26,17.85,26.33,5.89,1.11,5.77
cpu_2048_90_001,41.96,43.47,56.81,85.11,85.27,111.23,10.58,10.40,12.45,158.38,145.22,252.32,26.58,31.97,46.44,16.91,18.46,37.28"

dat_io <-read.csv(text=csv_text_io, header=FALSE)
dat_cpu <-read.csv(text=csv_text_cpu, header=FALSE)

dataframe_io <- data.frame(dat_io)
dataframe_cpu <- data.frame(dat_cpu)

lst1 = list() 
for(i in 1:ncol(dataframe_io)) {     
  lst1[[i]] <- dataframe_io[ , i]   
}

lst_cpu = list() 
for(i in 1:ncol(dataframe_cpu)) {     
  lst_cpu[[i]] <- dataframe_cpu[ , i]   
}

#labels
xl <- expression(Vazão ~ (10^3 ~ requisições/s))
yl <- "Latência (ms)"

logger_limit = c(15.41,26.75)

#ranges 
max_x=17
max_y=60

#colors
color1 <- "#66c2a5";
color2 <- "#fc8d62";
color3 <- "#0000FF";
color4 <- "#FF0000";
color5 <- "#789012";

# Plot I/O-bound graph

cairo_pdf("thr_lat_2_ring_io.pdf", height = 4, width = 4.5)
par(family = 'sans')

# names = c("Without Log", "Coupled Log", "Decoupled Log", "Logger Throughput", "Agg Logger Throughput")
names = c("Sem Log", "Log Acoplado", "Log Desacoplado", "Vazão do Logger")
plot(x=lst1[[3]], y=lst1[[6]], type='b', col=color1, pch=19, xlim=c(0,max_x), ylim=c(0,max_y), ylab=yl, xlab=xl, main='' ) 
lines(x=lst1[[9]], y=lst1[[12]], type='b', lty=2, col=color2, pch=18)
lines(x=lst1[[15]], y=lst1[[18]], type='b', lty=3, col=color3, pch=17)
abline(v=logger_limit, col=c(color4,color5), lty=4)
legend("topleft", legend = names, col = c(color1, color2, color3, color4, color5), pch = c(19,18,17, 15, 15), box.lty=0, cex = 0.75)


dev.off()   # Save the file.


#ranges 
max_x=40
max_y=70

# Plot CPU-bound graph
cairo_pdf("thr_lat_2_ring_cpu.pdf", height = 4, width = 4.5)
par(family = 'sans')

plot(x=lst_cpu[[3]], y=lst_cpu[[6]], type='b', col=color1,pch=19, xlim=c(0,max_x), ylim=c(0,max_y), ylab=yl, xlab=xl, main='') 
lines(x=lst_cpu[[9]], y=lst_cpu[[12]], type='b', lty=2, col=color2, pch=18)
lines(x=lst_cpu[[15]], y=lst_cpu[[18]], type='b', lty=3, col=color3, pch=17)
abline(v=logger_limit, col=c(color4,color5), lty=4)
legend("topleft", legend = names, col = c(color1, color2, color3, color4, color5), pch = c(19,18,17, 15, 15), box.lty=0, cex = 0.75)


dev.off()   # Save the file.

system(paste("pdfcrop", "thr_lat_2_ring_io.pdf", "thr_lat_2_ring_io.pdf"))
system(paste("pdfcrop", "thr_lat_2_ring_cpu.pdf", "thr_lat_2_ring_cpu.pdf"))
