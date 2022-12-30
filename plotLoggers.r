# dec_throughputLogger (avg)(kCommands/s)
# dec_throughputLogger (med)(kCommands/s)
# dec_throughputLogger (p95)(kCommands/s)
# dec_latencyLogger (avg)(kCommands/s)
# dec_latencyLogger (med)(kCommands/s)
# dec_latencyLogger (p95)(kCommands/s)
#

title <- "Performance do logger"
max_x=10
max_y=500
one_ring <- "0.29,0.29,0.31,0.14,0.14,0.15,
0.59,0.59,0.61,0.15,0.12,0.26,
1.17,1.17,1.21,0.15,0.13,0.31,
2.34,2.35,2.39,0.26,0.17,0.52,
4.54,4.56,4.62,1.26,0.59,5.35,
4.75,4.80,4.86,24.06,19.61,61.94,
4.67,4.71,4.77,83.27,78.97,185.75,
4.56,4.63,4.67,197.80,185.15,454.87,
4.66,4.67,4.78,418.59,429.64,942.92,"

two_rings <- "0.59,0.59,0.61,0.19,0.20,0.30,
1.17,1.18,1.22,0.19,0.20,0.26,
2.34,2.35,2.38,0.22,0.21,0.43,
4.68,4.68,4.74,0.32,0.24,0.87,
8.60,8.64,8.83,2.89,1.50,10.87,
8.60,8.67,8.76,29.76,23.79,71.41,
8.48,8.53,8.66,100.11,97.58,210.52,
8.23,8.33,8.49,236.31,247.48,497.83,
8.22,8.24,8.42,460.58,449.01,1067.82,"

dat_one_ring <-read.csv(text=one_ring, header=FALSE)
dat_two_rings <-read.csv(text=two_rings, header=FALSE)

dataframe_one_ring <- data.frame(dat_one_ring)
dataframe_two_rings <- data.frame(dat_two_rings)

lst1 = list() 
lst2 = list() 
for(i in 1:ncol(dataframe_one_ring)) {     
  lst1[[i]] <- dataframe_one_ring[ , i]   
  lst2[[i]] <- dataframe_two_rings[ , i]   
}


names = c("1 anel com 1 disco", "2 anéis com 1 disco", "2 anéis com 2 discos")

plot(x=lst1[[2]], y=lst1[[5]], type='b', col="blue", pch=19, xlim=c(0,max_x), ylim=c(0,max_y), ylab="Latencia (ms)", xlab="Vazao (kCommandos/s)", main=title ) 

lines(x=lst2[[2]], y=lst2[[5]], type='b', col="red", pch=18)

#lines(x=lst1[[27]], y=lst1[[30]], type='b', col="green", pch=17)

legend("topleft", legend = names, col = c("blue", "red", "green","orange"), pch = c(19,18,17,16), lty = 1:1, cex = 1)