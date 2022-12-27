# name
# sem_throughputReplica (avg)(kCommands/s)
# sem_throughputReplica (med)(kCommands/s)
# sem_throughputReplica (p95)(kCommands/s)
# sem_latency (avg)(ms)
# sem_latency (med)(ms)
# sem_latency (p95)(ms),
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
# dec_throughputLogger (avg)(kCommands/s)
# dec_throughputLogger (med)(kCommands/s)
# dec_throughputLogger (p95)(kCommands/s),

title <- "Acesso ao disco"
max_x=20
max_y=300
csv_text <- "io_2_90_001,0.06,0.06,0.07,1.96,1.92,2.17,0.06,0.06,0.07,2.31,2.30,2.57,0.06,0.06,0.07,2.46,2.42,2.85,0.06,0.06,0.07,
io_4_90_001,0.13,0.13,0.14,1.78,1.76,2.00,0.13,0.13,0.14,2.31,2.23,2.81,0.12,0.13,0.13,2.42,2.36,2.86,0.13,0.13,0.13,
io_8_90_001,0.25,0.25,0.27,1.79,1.78,2.00,0.25,0.25,0.26,2.44,2.20,3.21,0.25,0.25,0.26,2.35,2.26,2.95,0.25,0.25,0.26,
io_16_90_001,0.51,0.51,0.53,1.78,1.77,2.17,0.50,0.50,0.52,2.06,2.00,2.48,0.50,0.50,0.52,2.42,2.30,3.06,0.50,0.50,0.52,
io_32_90_001,1.02,1.01,1.04,1.91,1.79,2.62,1.00,1.00,1.03,2.33,2.11,3.42,1.00,1.00,1.03,2.35,2.16,3.16,1.00,1.00,1.03,
io_64_90_001,2.03,2.03,2.07,1.89,1.75,2.69,1.98,1.99,2.03,2.66,2.40,4.28,1.99,1.99,2.02,2.61,2.35,4.03,1.99,1.99,2.02,
io_128_90_001,3.95,3.94,5.64,2.94,2.77,4.12,3.77,3.87,5.21,4.46,3.17,5.41,3.91,3.91,5.05,3.33,2.98,4.34,3.89,3.90,3.95,
io_256_90_001,7.27,7.85,9.17,6.65,2.70,4.89,6.01,6.57,7.67,14.58,7.09,27.72,7.02,7.80,10.01,5.05,2.54,6.98,6.86,7.75,7.95,
io_512_90_001,11.10,11.68,15.67,19.81,4.17,48.19,5.56,6.13,8.14,57.68,41.73,83.53,11.23,11.70,15.42,17.74,3.78,27.77,10.45,10.25,15.50,
io_1024_90_001,11.02,11.46,16.58,66.66,36.63,125.13,6.61,6.83,8.38,139.92,115.93,191.18,11.73,11.88,15.56,49.16,29.86,67.05,12.29,13.32,16.66,
io_2048_90_001,10.57,10.65,15.48,166.79,104.86,542.11,5.77,6.42,8.19,334.25,277.84,831.89,13.55,14.76,16.90,133.38,100.75,207.36,13.14,13.76,16.87,"

# title <- "Acesso em memória"
# max_x=30
# max_y=150
# csv_text <- "cpu_2_90_001,0.06,0.06,0.07,2.15,2.14,2.31,0.06,0.06,0.07,2.32,2.24,2.75,0.06,0.06,0.07,2.39,2.33,2.95,0.06,0.06,0.07,
# cpu_4_90_001,0.13,0.13,0.13,2.05,2.01,2.29,0.12,0.12,0.13,2.22,2.22,2.43,0.12,0.12,0.13,2.26,2.25,2.52,0.12,0.12,0.13,
# cpu_8_90_001,0.25,0.25,0.27,2.11,2.00,2.68,0.25,0.25,0.26,2.37,2.15,3.24,0.25,0.25,0.27,2.32,2.22,2.70,0.25,0.25,0.26,
# cpu_16_90_001,0.51,0.51,0.52,1.99,1.93,2.50,0.50,0.50,0.52,2.08,2.05,2.52,0.50,0.50,0.52,1.97,1.95,2.36,0.50,0.50,0.52,
# cpu_32_90_001,1.01,1.02,1.05,1.94,1.81,2.18,1.01,1.01,1.03,2.06,1.95,2.87,1.01,1.01,1.04,2.15,2.01,2.72,1.01,1.01,1.03,
# cpu_64_90_001,1.98,2.03,2.08,1.85,1.74,2.41,1.95,1.95,1.99,3.26,3.01,5.00,2.03,2.03,2.08,1.90,1.83,2.54,2.03,2.04,2.07,
# cpu_128_90_001,3.58,3.55,4.09,1.68,1.52,2.33,3.99,3.99,4.05,2.40,2.06,3.79,3.45,3.61,4.07,1.71,1.57,2.07,3.48,3.61,4.07,
# cpu_256_90_001,8.16,8.18,8.25,6.72,2.58,4.06,7.94,7.98,8.15,2.62,2.25,3.63,8.05,8.11,8.21,2.02,1.85,2.74,8.05,8.11,8.22,
# cpu_512_90_001,15.16,15.96,16.48,1.95,1.62,2.71,10.21,10.30,12.22,21.37,16.47,48.28,11.60,16.16,16.43,1.89,1.63,2.81,11.58,12.73,20.10,
# cpu_1024_90_001,22.85,25.59,29.96,1.86,1.28,2.39,11.47,11.71,13.68,56.58,54.06,96.98,19.81,20.22,32.02,1.94,1.45,3.45,16.08,15.45,21.83,
# cpu_2048_90_001,25.51,27.76,46.05,2.25,1.55,4.68,11.50,11.78,14.37,154.28,142.21,265.18,22.69,26.41,35.22,9.27,2.45,33.99,16.23,16.59,17.72,"

dat <-read.csv(text=csv_text, header=FALSE)

dataframe <- data.frame(dat)

lst1 = list() 
for(i in 1:ncol(dataframe)) {     
  lst1[[i]] <- dataframe[ , i]   
}

names = c("Sem log", "Log acoplado", "Log desacoplado (Replica)")

plot(x=lst1[[3]], y=lst1[[6]], type='b', col="blue", pch=18, xlim=c(0,max_x), ylim=c(0,max_y), ylab="Latencia (ms)", xlab="Vazao (kCommandos/s)", main=title ) 

lines(x=lst1[[9]], y=lst1[[12]], type='b', col="red", pch=19)

lines(x=lst1[[15]], y=lst1[[18]], type='b', col="green", pch=17)

legend("topleft", legend = names, col = c("blue", "red", "green"), pch = c(19,18,17), lty = 1:2, cex = 0.8)

