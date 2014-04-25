#!/usr/bin/env bash

JBOSS_HOME=/usr/local/share/jboss

apt-get update

apt-get install openjdk-7-jdk
sudo echo "JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64/jre" >> /etc/environment


echo "Setting up mysql..."

sudo debconf-set-selections <<< 'mysql-server mysql-server/root_password password plato123'
sudo debconf-set-selections <<< 'mysql-server mysql-server/root_password_again password plato123'
sudo apt-get -y install mysql-server




# in the Server Section [mysqld] 
# max_allowed_packet = 128M
# max_sp_recursion_depth = 255
# thread_stack = 512K

sed -i.bak -e 's/max_allowed_packet[ \t]*=[ \t]*16M/max_allowed_packet = 128M/g' -e 's/thread_stack[ \t]*=[ \t]*192K/thread_stack            = 512K\nmax_sp_recursion_depth = 255\n /g' /etc/mysql/my.cnf

# character-set-server=utf8
# collation-server=utf8_general_ci
sed -i.utf8.bak -e 's/\[mysqld\]/\[mysqld\]\ncharacter-set-server=utf8\ncollation-server=utf8_general_ci\n/g' /etc/mysql/my.cnf

mysql stop
mysql start


echo "Setting up Plato"

apt-get install -y git 
apt-get install -y maven

cd /tmp
git clone https://github.com/openplanets/plato.git
git checkout vagrant_dev



wget http://download.jboss.org/jbossas/7.1/jboss-as-7.1.0.Final/jboss-as-7.1.0.Final.tar.gz
tar xfvz jboss-as-7.1.0.Final.tar.gz
mv jboss-as-7.1.0.Final $JBOSS_HOME

mkdir -p $JBOSS_HOME/modules/com/mysql/main
cp /tmp/plato/provisional/mysql/* $JBOSS_HOME/modules/com/mysql/main/

rm -r $JBOSS_HOME/modules/org/picketlink/main/*

cd /tmp/plato/tools
./setup-database.sh plato123 plato idp

cp standalone.xml $JBOSS_HOME/standalone/configuration/

adduser appserver
chown -R appserver $JBOSS_HOME


cd plato
mvn clean install -DskipTests





cp /tmp/plato/idp/target/idp.war /usr/local/share/jboss/standalone/deployments/
cp /tmp/plato/planningsuite-ear/target/planningsuite-ear.ear /usr/local/share/jboss/standalone/deployments/

su -c "nohup /home/jboss/jboss7/7.1.0/bin/standalone.sh -b=0.0.0.0 &" - appserver