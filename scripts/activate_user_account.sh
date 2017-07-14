#!/bin/bash

echo "This script is used to activate account"

echo -n "Please enter birthDate(yyyy-MM-dd) : "
read birthDate

echo -n "Please enter emailToken : "
read emailToken

echo -n "Please enter verificationCode : "
read verificationCode

#Activate user account with username and password
echo -n "Please enter username : "
read username

echo -n "Please enter password : "
read password

echo -n "Please confirm password : "
read confirmPassword

#Get property value from json response
function jsonValue() {
KEY=$1
awk -F"[{,:}]" '{for(i=1;i<=NF;i++){if($i~/'$KEY'/){print $(i+1)}}}'|tr -d '"'| sed -n 1p
}

#Generate activation request json
generate_post_activation_user_data()
{
  cat <<EOF
      {	"emailToken": "$emailToken",
        "verificationCode": "$verificationCode",
        "birthDate": [$(echo $birthDate|awk -F"[-]" '{print $1}'),$(echo $birthDate|awk -F"[-]" '{print $2}'|sed 's/^0*//'),$(echo $birthDate|awk -F"[-]" '{print $3}'|sed 's/^0*//')],
        "password":  "$password",
        "confirmPassword":  "$confirmPassword",
        "username": "$username"
      }
EOF
}

# Activate user account
verified=$(curl --silent\
        -H "Accept: application/json" \
        -H "Content-Type:application/json" \
        -H "X-Forwarded-Proto:http" \
        -H "X-Forwarded-Host:localhost" \
        -H "X-Forwarded-Port:80" \
        -X POST --data "$(generate_post_activation_user_data)" "http://localhost:8461/users/activation"| jsonValue verified )

echo "User activated: $verified."