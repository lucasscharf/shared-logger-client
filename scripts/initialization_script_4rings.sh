sun=node0
earth=node1
venus=node2
mars=node3
jupyter=node4
uranus=node5
neptune=node6

ips=($sun $earth $venus $mars $jupyter $neptune $uranus)

for ip in "${ips[@]}" 
do
 ssh lucas123@$ip '
	sudo chmod -R 777 /media; \
	rm -rf /media/* ; \
	mkdir /media/disk1 /media/disk2 /media/disk3 /media/disk4 /media/disk5 /media/disk6 ; \
	sudo chmod -R 777 /media'
done 

ips=($jupyter $neptune $uranus)

for ip in "${ips[@]}" 
do
 'sudo mkfs.ext4 /dev/sdb ' 
done 

for ip in "${ips[@]}" 
do
 ssh lucas123@$ip '
sudo mkfs.ext4 /dev/sdb ; \
sudo mkfs.ext4 /dev/sdc ; \
sudo mkfs.ext4 /dev/sdd ; \
sudo mkfs.ext4 /dev/sde ; \
sudo mkfs.ext4 /dev/sdf ; \
sudo mkfs.ext4 /dev/sdg ; \
sudo mount /dev/sdb /media/disk1 ; \
sudo mount /dev/sdc /media/disk2 ; \
sudo mount /dev/sdd /media/disk3 ; \
sudo mount /dev/sde /media/disk4 ; \
sudo mount /dev/sdf /media/disk5 ; \
sudo mount /dev/sdg /media/disk6 ; \
sudo chmod -R 777 /media'
done