#!/bin/bash
MPASS="$1"
PLATO_PASS="$2"
IDP_PASS="$3"
DB_URL="$4"


VERSION="7.1.0"
 
# Detect paths
MYSQL=$(which mysql)
AWK=$(which awk)
GREP=$(which grep)

MPASSOPT="-p"$MPASS

if [ $# -lt 3 ]
then
	echo "Usage: $0 {MySQL-Root Pwd} {Plato DB Pwd} {IDP DB Pwd} {DB URL}]"
	echo " Creates users and databases for Plato and IDP"
	echo " - if no DB URL is provided 'localhost' is used "
	exit 1
fi

if [ "test$DB_URL" == "test" ]
then
	DB_URL="localhost"
fi

echo  "Generating configuration file for JBoss AS 7-$VERSION: standalone.xml"

cat standalone-$VERSION-template.xml | \
sed -e 's/{PLATO_PWD}/'$PLATO_PASS'/g' -e's/{IDP_PWD}/'$IDP_PASS'/g' -e's/{DB_URL}/'$DB_URL'/g' > standalone.xml

echo "Creating databases and users..."
$MYSQL -u root -p$MPASS -e "create user 'plato'@'localhost' identified by '"$PLATO_PASS"';"
$MYSQL -u root -p$MPASS -e "create database platodb;"
$MYSQL -u root -p$MPASS -e "grant all privileges on platodb.* to 'plato'@'localhost';"

$MYSQL -u root -p$MPASS -e "create user 'idp'@'localhost' identified by '"$IDP_PASS"';"
$MYSQL -u root -p$MPASS -e "create database idpdb;"
$MYSQL -u root -p$MPASS -e "grant all privileges on idpdb.* to 'idp'@'localhost';"

echo "Creating databases and users for tests..."
$MYSQL -u root -p$MPASS -e "create user 'platotest'@'localhost' identified by 'platotest';"
$MYSQL -u root -p$MPASS -e "create database platodbtest;"
$MYSQL -u root -p$MPASS -e "grant all privileges on platodbtest.* to 'platotest'@'localhost';"

$MYSQL -u root -p$MPASS -e "create user 'idptest'@'localhost' identified by 'idptest';"
$MYSQL -u root -p$MPASS -e "create database idpdbtest;"
$MYSQL -u root -p$MPASS -e "grant all privileges on idpdbtest.* to 'idptest'@'localhost';"
