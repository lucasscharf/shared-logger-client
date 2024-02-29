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
csv_text_io <- "io_8_90_001,0.25,0.25,0.26,1.99,1.94,2.25,0.25,0.25,0.26,2.33,2.30,2.45,0.25,0.25,0.26,2.18,2.20,2.24
io_16_90_001,0.51,0.51,0.52,1.89,1.82,3.11,0.50,0.50,0.51,2.33,2.17,2.89,0.50,0.50,0.53,2.12,2.13,2.36
io_32_90_001,1.00,1.00,1.03,2.39,2.27,3.26,1.00,1.00,1.02,2.26,2.01,2.98,1.01,1.02,1.04,2.10,1.77,2.75
io_64_90_001,1.97,1.97,2.02,3.05,2.80,4.46,1.92,1.94,1.98,7.13,3.69,5.40,2.02,2.03,2.09,1.92,1.71,2.60
io_128_90_001,3.89,3.90,3.96,2.95,2.90,4.28,3.16,3.41,4.64,8.66,3.89,36.67,3.87,3.90,3.99,3.48,2.90,5.23
io_256_90_001,7.59,7.75,8.73,4.15,2.46,13.85,5.39,5.43,6.13,15.35,10.77,36.68,7.26,7.52,8.26,5.51,3.31,16.57
io_512_90_001,6.89,6.61,9.22,38.81,25.60,79.62,4.84,4.82,5.98,59.68,52.64,87.35,7.94,7.87,10.16,32.16,27.47,66.78
io_1024_90_001,9.31,9.61,11.46,107.14,86.64,199.48,4.99,5.05,5.98,169.38,151.08,288.57,9.36,9.24,11.89,83.70,68.68,160.07
io_2048_90_001,9.52,10.22,11.57,190.29,171.05,326.98,5.08,5.19,6.10,371.20,344.10,575.01,9.68,9.82,11.23,190.37,172.56,263.50"

title_cpu <- "CPU Intensiva"
csv_text_cpu <- "cpu_8_90_001,0.25,0.25,0.26,1.87,1.89,1.89,0.25,0.25,0.26,2.02,2.05,2.10,0.25,0.25,0.26,1.68,1.75,1.77
cpu_16_90_001,0.51,0.51,0.53,1.76,1.72,2.35,0.49,0.50,0.53,1.87,1.87,2.19,0.51,0.51,0.52,1.93,1.93,2.17
cpu_32_90_001,1.01,1.02,1.04,1.72,1.73,1.87,0.86,1.02,1.05,1.65,1.62,1.91,1.03,1.03,1.06,1.60,1.52,2.23
cpu_64_90_001,2.02,2.03,2.06,2.12,2.02,3.20,1.97,1.96,2.01,2.91,2.75,4.87,2.02,2.03,2.06,1.86,1.71,2.71
cpu_128_90_001,4.05,4.07,4.13,1.88,1.72,2.96,3.99,4.01,4.06,2.91,2.04,4.32,4.02,4.03,4.09,2.48,2.02,3.47
cpu_256_90_001,8.18,8.20,8.30,2.11,1.43,2.34,7.60,7.76,9.43,3.70,2.87,6.70,8.11,8.14,8.25,1.78,1.43,2.37
cpu_512_90_001,16.44,16.54,16.72,1.74,1.13,2.55,11.01,11.64,13.44,17.00,10.51,43.55,13.87,14.12,16.29,4.07,1.50,5.89
cpu_1024_90_001,26.75,29.18,31.70,2.12,1.18,3.21,10.23,10.44,12.67,66.08,58.16,142.18,23.31,23.94,29.91,3.70,1.18,11.45
cpu_2048_90_001,41.50,40.75,57.65,331.19,47.26,983.24,10.09,10.47,13.52,155.45,137.24,253.01,30.48,32.50,44.63,3098.99,51.02,8308.87"

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

logger_limit = c(15.41,53.56)

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

cairo_pdf("thr_lat_4_ring_io.pdf", height = 4, width = 4.5)
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
max_x=55
max_y=70

# Plot CPU-bound graph
cairo_pdf("thr_lat_4_ring_cpu.pdf", height = 4, width = 4.5)
par(family = 'sans')

plot(x=lst_cpu[[3]], y=lst_cpu[[6]], type='b', col=color1,pch=19, xlim=c(0,max_x), ylim=c(0,max_y), ylab=yl, xlab=xl, main='') 
lines(x=lst_cpu[[9]], y=lst_cpu[[12]], type='b', lty=2, col=color2, pch=18)
lines(x=lst_cpu[[15]], y=lst_cpu[[18]], type='b', lty=3, col=color3, pch=17)
abline(v=logger_limit, col=c(color4,color5), lty=4)
legend("topleft", legend = names, col = c(color1, color2, color3, color4, color5), pch = c(19,18,17, 15, 15), box.lty=0, cex = 0.75)


dev.off()   # Save the file.

system(paste("pdfcrop", "thr_lat_4_ring_io.pdf", "thr_lat_4_ring_io.pdf"))
system(paste("pdfcrop", "thr_lat_4_ring_cpu.pdf", "thr_lat_4_ring_cpu.pdf"))
