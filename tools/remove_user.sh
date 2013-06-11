#!/bin/bash
IDP_PASS="$1"
USER="$2"

# Detect paths
MYSQL=$(which mysql)
AWK=$(which awk)
GREP=$(which grep)

MPASSOPT="-p"$MPASS

if [ $# -ne 2 ]
then
	echo "Usage: $0 {IDP DB Pwd} {user email}"
	echo " removes a user by its email and it roles"
	exit 1
fi

echo "removing roles for user $USER"
$MYSQL -u idp -p$IDP_PASS -e "delete from IdpUser_IdpRole where user_id = (select u.id from IdpUser u where u.email = '$USER' ); " idpdb
echo "removing user $USER"
$MYSQL -u idp -p$IDP_PASS -e "delete from IdpUser where email = '$USER'; " idpdb
echo "done"
