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
one_ring <- "0.27,0.27,0.28,0.06,0.07,0.07,
0.54,0.54,0.55,0.05,0.04,0.07,
1.08,1.08,1.10,0.06,0.04,0.13,
2.15,2.14,2.19,0.09,0.06,0.25,
4.30,4.30,4.36,0.14,0.07,0.41,
8.39,8.51,8.62,0.64,0.08,0.67,
13.51,14.01,15.56,7.02,0.09,6.54,
15.31,15.81,18.15,6.57,0.08,2.95,
12.82,13.16,15.17,6.86,0.10,14.25,"

two_rings <- "0.54,0.54,0.57,0.04,0.04,0.05,
1.08,1.08,1.10,0.05,0.03,0.08,
2.16,2.15,2.19,0.08,0.04,0.35,
4.31,4.31,4.36,0.08,0.04,0.28,
8.59,8.60,8.65,0.12,0.06,0.42,
16.55,16.92,17.11,2.45,0.09,3.82,
22.11,22.39,23.56,6.85,0.12,9.43,
21.15,21.57,22.79,10.18,0.12,34.26,
20.60,21.28,22.34,106.59,0.14,22.84,"

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