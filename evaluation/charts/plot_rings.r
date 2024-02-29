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
csv_text_io <- "io_8_90_001,0.25,0.25,0.27,1.83,1.83,1.85,0.25,0.25,0.26,2.05,2.04,2.16,0.25,0.25,0.27,2.16,2.12,2.26
io_16_90_001,0.51,0.51,0.53,1.85,1.82,2.51,0.50,0.51,0.52,2.40,2.50,3.19,0.51,0.51,0.53,3.60,1.92,18.61
io_32_90_001,1.01,1.01,1.04,1.79,1.72,2.52,1.01,1.01,1.05,1.82,1.83,2.15,1.00,1.00,1.02,2.62,2.16,3.14
io_64_90_001,2.03,2.04,2.08,1.68,1.62,2.32,2.02,2.03,2.08,1.75,1.67,2.34,2.02,2.01,2.07,2.11,2.02,3.26
io_128_90_001,3.92,3.95,4.02,3.63,2.84,4.08,3.80,3.85,3.91,3.73,3.46,5.06,4.04,4.05,4.14,2.33,1.68,4.75
io_256_90_001,7.56,7.82,7.95,4.22,2.73,6.56,5.15,5.25,6.02,21.81,17.35,49.90,7.53,7.78,8.00,3.75,2.87,6.87
io_512_90_001,9.59,9.92,11.17,22.46,17.77,31.66,5.47,5.53,6.45,61.60,55.03,82.64,9.47,9.90,11.77,24.60,17.98,57.39
io_1024_90_001,9.21,10.33,12.03,68.71,59.64,99.99,4.80,5.29,6.18,167.83,148.63,318.09,10.00,10.30,12.09,72.24,63.08,136.20
io_2048_90_001,9.85,9.90,11.46,175.82,159.70,302.35,5.16,5.32,6.07,357.20,336.15,469.56,9.86,10.43,12.08,179.52,154.86,305.35"

title_cpu <- "CPU Intensiva"
csv_text_cpu <- "cpu_8_90_001,0.25,0.26,0.26,1.60,1.64,1.64,0.25,0.26,0.27,1.98,1.92,2.11,0.25,0.25,0.26,2.27,2.11,2.78
cpu_16_90_001,0.51,0.51,0.53,1.77,1.85,2.21,0.51,0.51,0.53,1.72,1.69,1.89,0.51,0.51,0.52,1.96,1.80,3.29
cpu_32_90_001,0.89,1.02,1.04,1.56,1.53,1.87,1.02,1.02,1.04,1.62,1.60,1.81,1.02,1.02,1.05,1.60,1.63,1.77
cpu_64_90_001,2.03,2.03,2.06,1.84,1.68,2.80,1.98,1.98,2.02,2.89,2.68,4.12,2.04,2.04,2.07,1.67,1.61,2.33
cpu_128_90_001,4.06,4.07,4.13,1.88,1.81,3.03,3.96,3.97,4.04,2.58,2.50,3.73,4.02,4.04,4.09,2.15,2.10,3.17
cpu_256_90_001,7.32,7.82,8.11,2.04,1.60,3.28,7.86,7.92,8.07,2.97,2.12,5.95,8.04,8.03,8.25,2.05,1.72,2.93
cpu_512_90_001,16.26,16.39,16.67,1.49,1.21,2.30,9.95,10.38,12.11,18.31,13.47,40.43,14.70,14.81,16.16,1.96,1.39,2.58
cpu_1024_90_001,30.83,31.81,33.42,4.43,1.08,24.24,11.21,11.45,12.65,61.50,54.14,89.91,11.86,15.43,28.38,2.76,1.71,7.36
cpu_2048_90_001,41.38,41.29,56.64,19.75,21.31,34.89,10.87,11.21,12.77,138.85,123.10,196.24,27.20,27.40,51.34,115.08,11.94,883.05"

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

logger_limit = c(15.41)
#ranges 
max_x=17
max_y=60

#colors
color1 <- "#66c2a5";
color2 <- "#fc8d62";
color3 <- "#0000FF";
color4 <- "#FF0000";

# Plot I/O-bound graph

cairo_pdf("thr_lat_1_ring_io.pdf", height = 4, width = 4.5)
par(family = 'sans')

names = c("Sem Log", "Log Acoplado", "Log Desacoplado", "Vazão do Logger")
plot(x=lst1[[3]], y=lst1[[6]], type='b', col=color1, pch=19, xlim=c(0,max_x), ylim=c(0,max_y), ylab=yl, xlab=xl, main='' ) 
lines(x=lst1[[9]], y=lst1[[12]], type='b', lty=2, col=color2, pch=18)
lines(x=lst1[[15]], y=lst1[[18]], type='b', lty=3, col=color3, pch=17)
abline(v=logger_limit, col=color4, lty=4)
legend("topleft", legend = names, col = c(color1, color2, color3, color4), pch = c(19,18,17, 15), box.lty=0, cex = 0.75)

dev.off()   # Save the file.


#ranges 
max_x=40
max_y=70

# Plot CPU-bound graph
cairo_pdf("thr_lat_1_ring_cpu.pdf", height = 4, width = 4.5)
par(family = 'sans')

plot(x=lst_cpu[[3]], y=lst_cpu[[6]], type='b', col=color1,pch=19, xlim=c(0,max_x), ylim=c(0,max_y), ylab=yl, xlab=xl, main='') 
lines(x=lst_cpu[[9]], y=lst_cpu[[12]], type='b', lty=2, col=color2, pch=18)
lines(x=lst_cpu[[15]], y=lst_cpu[[18]], type='b', lty=3, col=color3, pch=17)
abline(v=logger_limit, col=color4, lty=4)
legend("topleft", legend = names, col = c(color1, color2, color3, color4), pch = c(19,18,17, 15), box.lty=0, cex = 0.75)

dev.off()   # Save the file.

system(paste("pdfcrop", "thr_lat_1_ring_io.pdf", "thr_lat_1_ring_io.pdf"))
system(paste("pdfcrop", "thr_lat_1_ring_cpu.pdf", "thr_lat_1_ring_cpu.pdf"))
