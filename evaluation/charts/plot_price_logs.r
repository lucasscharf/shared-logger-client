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


title_same <- "Mesmo quantidade"
csv_same <- "1,578.15,694.21,650.15
2,1156.30,1272.36,1300.30
3,1734.45,1850.51,1950.45
4,2312.60,2428.66,2600.60
5,2890.75,3006.81,3250.75
6,3468.90,3584.96,3900.90
7,4047.05,4163.11,4551.05
8,4625.20,4741.26,5201.20
9,5203.35,5319.41,5851.35
10,5781.50,5897.56,6501.50"

title_replica <- "Mais réplicas"
csv_replica <- "1,780.18,824.24,780.18
2,1560.36,1532.42,1560.36
3,2340.54,2240.60,2340.54
4,3120.72,2948.78,3120.72
5,3900.90,3656.96,3900.90
6,4681.08,4365.14,4681.08
7,5461.26,5073.32,5461.26
8,6241.44,5781.50,6241.44
9,7021.62,6489.68,7021.62
10,7801.80,7197.86,7801.80"

title_log <- "Mais log"
csv_log <- "1,896.24,940.30,910.21
2,1792.48,1706.51,1820.42
3,2688.72,2472.72,2730.63
4,3584.96,3238.93,3640.84
5,4481.20,4005.14,4551.05
6,5377.44,4771.35,5461.26
7,6273.68,5537.56,6371.47
8,7169.92,6303.77,7281.68
9,8066.16,7069.98,8191.89
10,8962.40,7836.19,9102.10"

dat_same <-read.csv(text=csv_same, header=FALSE)
dat_replica <-read.csv(text=csv_replica, header=FALSE)
dat_log <-read.csv(text=csv_log, header=FALSE)

dataframe_same <- data.frame(dat_same)
dataframe_replica <- data.frame(dat_replica)
dataframe_log <- data.frame(dat_log)

lst_same = list() 
for(i in 1:ncol(dataframe_same)) {     
  lst_same[[i]] <- dataframe_same[ , i]   
}

lst_replica = list() 
for(i in 1:ncol(dataframe_replica)) {     
  lst_replica[[i]] <- dataframe_replica[ , i]   
}

lst_log = list() 
for(i in 1:ncol(dataframe_log)) {     
  lst_log[[i]] <- dataframe_log[ , i]   
}

#labels
xl <- "Número de Aplicações"
yl <- "Custo (R$ / mês)"

logger_limit = c(15.41)
#ranges 
max_x=11
max_y=10000

#colors
color1 <- "#33ff33";
color2 <- "#ffb521";
color3 <- "#7111ff";

# Plot I/O-bound graph

names = c("Log acoplado", "Log desacoplado", "Log acceptor")

cairo_pdf("price_same.pdf", height = 3.5, width = 4.5)
par(family = 'sans')

plot(x=lst_same[[1]], y=lst_same[[2]], type='l', col=color1, pch=20, xlim=c(0,max_x), ylim=c(0,max_y), ylab=yl, xlab=xl, main='' ) 
lines(x=lst_same[[1]], y=lst_same[[3]], type='l', lty=2, col=color2, pch=21)
lines(x=lst_same[[1]], y=lst_same[[4]], type='l', lty=3, col=color3, pch=22)
legend("topleft", legend = names, col = c(color1, color2, color3), lty = c(1,2,3), box.lty=0, cex = 0.75)

dev.off()   # Save the file.

########
cairo_pdf("price_replica.pdf", height = 4, width = 4.5)
par(family = 'sans')

plot(x=lst_replica[[1]], y=lst_replica[[2]], type='l', col=color1, pch=20, xlim=c(0,max_x), ylim=c(0,max_y), ylab=yl, xlab=xl, main='' ) 
lines(x=lst_replica[[1]], y=lst_replica[[3]], type='l', lty=2, col=color2, pch=21)
lines(x=lst_replica[[1]], y=lst_replica[[4]], type='l', lty=3, col=color3, pch=21)
legend("topleft", legend = names, col = c(color1, color2, color3), lty = c(1,2,3), box.lty=0, cex = 0.75)

dev.off()   # Save the file.


########
cairo_pdf("price_log.pdf", height = 4, width = 4.5)
par(family = 'sans')

plot(x=lst_log[[1]], y=lst_log[[2]], type='l', col=color1, pch=20, xlim=c(0,max_x), ylim=c(0,max_y), ylab=yl, xlab=xl, main='' ) 
lines(x=lst_log[[1]], y=lst_log[[3]], type='l', lty=2, col=color2, pch=21)
lines(x=lst_log[[1]], y=lst_log[[4]], type='l', lty=3, col=color3, pch=21)
legend("topleft", legend = names, col = c(color1, color2, color3), lty = c(1,2,3), box.lty=0, cex = 0.75)

dev.off()   # Save the file.
