#!/bin/bash
MPASS="$1"
PLATO_PASS="$2"
IDP_PASS="$3"

VERSION="7.1.0"
 
# Detect paths
MYSQL=$(which mysql)
AWK=$(which awk)
GREP=$(which grep)

MPASSOPT="-p"$MPASS

if [ $# -lt 3 ]
then
	echo "Usage: $0 {MySQL-Root Pwd} {Plato DB Pwd} {IDP DB Pwd}"
	echo "Creating Plato and IDP databases and users"
	exit 1
fi

echo standalone-$VERSION-template.xml

cat standalone-$VERSION-template.xml | \
sed -e 's/{PLATO_PWD}/'$PLATO_PASS'/g' -e's/{IDP_PWD}/'$IDP_PASS'/g' > standalone.xml

$MYSQL -u root -p$MPASS -e "create user 'plato'@'localhost' identified by '"$PLATO_PASS"';"
$MYSQL -u root -p$MPASS -e "create database platodb;"
$MYSQL -u root -p$MPASS -e "grant all privileges on platodb.* to 'plato'@'localhost';"

$MYSQL -u root -p$MPASS -e "create user 'idp'@'localhost' identified by '"$IDP_PASS"';"
$MYSQL -u root -p$MPASS -e "create database idpdb;"
$MYSQL -u root -p$MPASS -e "grant all privileges on idpdb.* to 'idp'@'localhost';"

$MYSQL -u root -p$MPASS -e "create user 'platotest'@'localhost' identified by 'platotest';"
$MYSQL -u root -p$MPASS -e "create database platodbtest;"
$MYSQL -u root -p$MPASS -e "grant all privileges on platodbtest.* to 'platotest'@'localhost';"

