# dec_throughputLogger (avg)(kCommands/s)
# dec_throughputLogger (med)(kCommands/s)
# dec_throughputLogger (p95)(kCommands/s)
# dec_latencyLogger (avg)(kCommands/s)
# dec_latencyLogger (med)(kCommands/s)
# dec_latencyLogger (p95)(kCommands/s)
#
#install.packages("ggplot2")

#library(ggplot2)

title <- "Performance do Logger"
one_ring <- "0.25,0.25,0.26,0.18,0.19,0.21,
0.51,0.51,0.52,0.17,0.16,0.36,
1.02,1.02,1.05,0.12,0.11,0.17,
2.04,2.04,2.08,0.16,0.12,0.31,
4.02,4.03,4.07,0.55,0.69,0.97,
8.04,8.04,8.24,0.14,0.07,0.54,
14.82,14.92,19.76,0.19,0.09,0.58,
13.15,15.41,22.01,0.25,0.20,0.55,
15.47,14.63,21.14,0.50,0.46,0.79,"

two_rings <- "0.50,0.50,0.52,0.17,0.16,0.24,
0.93,1.00,1.04,0.14,0.14,0.22,
2.04,2.04,2.08,0.23,0.15,0.46,
4.08,4.09,4.15,0.16,0.13,0.36,
8.16,8.18,8.36,0.16,0.11,0.42,
11.36,14.32,18.61,0.29,0.18,0.66,
27.59,26.60,36.93,0.54,0.47,0.79,
23.66,25.07,33.07,0.67,0.57,1.74,
25.71,26.75,37.10,0.65,0.56,1.54,"

four_rings <- "0.96,1.02,1.04,0.16,0.16,0.20,
2.04,2.04,2.07,0.16,0.15,0.26,
4.11,4.11,4.19,0.15,0.13,0.35,
8.10,8.13,8.22,0.28,0.22,0.72,
15.94,16.15,16.38,0.27,0.17,0.84,
31.81,31.80,34.50,0.43,0.46,0.88,
47.73,53.56,63.52,0.60,0.50,1.28,
52.34,50.32,76.34,0.56,0.48,1.10,
47.21,50.88,69.40,0.67,0.51,1.62,"

dat_one_ring <-read.csv(text=one_ring, header=FALSE)
dat_two_rings <-read.csv(text=two_rings, header=FALSE)
dat_four_rings <-read.csv(text=four_rings, header=FALSE)

dataframe_one_ring <- data.frame(dat_one_ring)
dataframe_two_rings <- data.frame(dat_two_rings)
dataframe_four_rings <- data.frame(dat_four_rings)

max_throughput=c(dat_one_ring[8,2], dat_two_rings[9,2], dat_four_rings[7,2])

#labels
yl <- expression("\n
Vazão" ~ máxima ~ (10^3 ~ requisições/s))

#colors
color1 <- "#66c2a5";
color2 <- "#fc8d62";
color3 <- "#0000FF";

cairo_pdf("thr_logger.pdf", height = 4, width = 4.5)
par(family = 'sans')

names = c("Um anel", "Dois anéis", "Quatro anéis")
barplot(names=names, height=max_throughput, col=c(color1, color2, color3), ylab=yl, mgp=c(2,1,0))



dev.off()   # Save the file.

system(paste("pdfcrop", "thr_logger.pdf", "thr_logger.pdf"))
