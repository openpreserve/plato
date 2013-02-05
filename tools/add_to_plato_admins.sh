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
	echo "Usage: $0 {IDP DB Pwd} {user name}"
	echo " adds user to plato admins"
	exit 1
fi

$MYSQL -u idp -p$IDP_PASS -e "insert into IdpUser_IdpRole (user_id, roles_id) select u.id, r.id from IdpUser u, IdpRole r where u.username = '$USER' and r.roleName='admin' and not exists (select * from IdpUser_IdpRole ur where ur.user_id=u.id and ur.roles_id=r.id ); " idpdb

