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


title_io <- "Log Síncrono"
csv_text_io <- "cpu_8_90_001_sync,0.25,0.25,0.26,2.02,2.00,2.08\
cpu_16_90_001_sync,0.51,  ,0.52,2.48,2.36,3.08 
cpu_32_90_001_sync,1.00,1.00,1.04,2.46,2.21,3.15
cpu_64_90_001_sync,2.04,2.05,2.08,2.52,3.14,3.82 
cpu_128_90_001_sync,4.02,4.03,4.10,2.69,2.70,3.32 
cpu_256_90_001_sync,8.01,8.06,8.15,3.38,3.30,3.84 
cpu_512_90_001_sync,16.11,16.11,16.46,3.72,3.32,3.61 
cpu_1024_90_001_sync,27.66,29.35,31.83,11.35,3.38,33.91 
cpu_2048_90_001_sync,47.14,46.53,58.30,74.73,39.98,400.99"

title_cpu <- "Log Assíncrono"
csv_text_cpu <- "cpu_8_90_001_async,0.25,0.25,0.26,2.01,2.02,2.05 
cpu_16_90_001_async,0.50,0.50,0.52,1.92,1.89,2.44 
cpu_32_90_001_async,1.01,1.01,1.06,1.50,1.53,1.76 
cpu_64_90_001_async,2.02,2.03,2.06,2.00,1.77,3.11 
cpu_128_90_001_async,4.09,4.09,4.16,1.54,1.45,2.21 
cpu_256_90_001_async,8.12,8.15,8.27,2.36,1.66,2.63 
cpu_512_90_001_async,16.27,16.39,16.57,1.88,1.22,2.49
cpu_1024_90_001_async,26.52,27.09,30.17,9.36,6.00,26.56 
cpu_2048_90_001_async,22.05,21.44,44.81,4.58,1.56,16.72"

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
color1 <- "#16c2a5";
color2 <- "#2c8d62";
color3 <- "#3000FF";
color4 <- "#4F0000";

# Plot I/O-bound graph

cairo_pdf("thr_lat_log_sync.pdf", height = 4, width = 4.5)
par(family = 'sans')

names = c("Log síncrono", "Log assíncrono")
plot(x=lst1[[3]], y=lst1[[6]], type='b', col=color1, pch=20, xlim=c(0,max_x), ylim=c(0,max_y), ylab=yl, xlab=xl, main='' ) 
lines(x=lst_cpu[[3]], y=lst_cpu[[6]], type='b', lty=2, col=color2, pch=21)
legend("topleft", legend = names, col = c(color1, color2), pch = c(20,21), box.lty=0, cex = 0.75)

dev.off()   # Save the file.

system(paste("pdfcrop", "thr_lat_log_sync.pdf", "thr_lat_log_sync.pdf"))
