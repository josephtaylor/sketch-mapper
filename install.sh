#!/bin/sh

echo 'Beginning installation...'
cd ~
echo workding dir: `pwd`
echo 'Downloading maven...'
wget http://mirror.symnds.com/software/Apache/maven/maven-3/3.2.5/binaries/apache-maven-3.2.5-bin.zip
unzip apache-maven-3.2.5-bin.zip
printf '\nexport MAVEN_HOME=~/apache-maven-3.2.5\n' >> ~/.bashrc
printf 'export PATH=$PATH:$MAVEN_HOME/bin\n' >> ~/.bashrc
source ~/.bashrc
mvn -version
echo 'git is located at' `which git`
git --version
echo 'Cloning the repository'
git clone https://github.com/josephtaylor/surface-mapper-gui.git
cd surface-mapper-gui
git checkout code-tn
mvn clean compile assembly:single
