# iSprinklerSystem

sudo apt-get remove wiringpi -y
sudo apt-get --yes install git-core gcc make
cd ~
git clone https://github.com/WiringPi/WiringPi --branch master --single-branch wiringpi
cd ~/wiringpi
sudo ./build
