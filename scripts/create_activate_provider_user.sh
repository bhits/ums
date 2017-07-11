#!/bin/bash

echo "This script is used to create and activate provider account"
echo -n "Please enter firstName : "
read firstName

echo -n "Please enter lastName : "
read lastName

echo -n "Please enter birthDate(yyyy-MM-dd) : "
read birthDate

echo -n "Please enter genderCode(male, female) : "
read genderCode

echo -n "Please enter EMAIL : "
read EMAIL

echo -n "Please enter NPI : "
read NPI

#Generate user demographic to create user
generate_post_create_user_data()
{
  cat <<EOF
      {	"lastName": "$lastName",
          "firstName": "$firstName",
          "birthDate": "$birthDate",
          "genderCode": "$genderCode",
          "telecoms": [{
          		"system": "EMAIL",
          		"value": "$EMAIL",
          		"use":"WORK"
          	}],
          "roles": [
                     {
                       "code": "provider",
                       "name": "Provider"
                     }
                   ],
          "identifiers": [
                     {
                       "value": "$NPI",
                       "system": "http://hl7.org/fhir/sid/us-npi"
                     }
                   ],
          "addresses":  [],
          "locale": "en"
          }
EOF
}

#Get property value from json response
function jsonValue() {
KEY=$1
awk -F"[{,:}]" '{for(i=1;i<=NF;i++){if($i~/'$KEY'/){print $(i+1)}}}'|tr -d '"'| sed -n 1p
}

#Create user and retrieve user Id
userId=$(curl --silent\
        -H "Accept: application/json" \
        -H "Content-Type:application/json" \
        -X POST --data "$(generate_post_create_user_data)" "http://localhost:8461/users" | jsonValue id)

echo "Here is provider user id = $userId."

#Initialize activation user account and get verification code
verificationCode=$(curl --silent\
        -H "Accept: application/json"\
        -H "Content-Type:application/json"\
        -H "X-Forwarded-Proto:abc"\
        -H "X-Forwarded-Host:localhost"\
        -H "X-Forwarded-Port:80"\
        -X POST "http://localhost:8461/users/$userId/activation" | jsonValue verificationCode)

echo "Here is provider user verification code = $verificationCode."

#Get Email token
emailToken=$(curl --silent\
         -H "Accept: application/json"\
         -H "Content-Type:application/json"\
         -X GET http://localhost:8461/users/${userId}/emailToken | jsonValue emailToken )

echo "Here is provider user token = $emailToken."

#Activate user account with username and password
echo -n "Please enter username : "
read username

echo -n "Please enter password : "
read password

echo -n "Please confirm password : "
read confirmPassword

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

#Activate user account
verified=$(curl --silent\
        -H "Accept: application/json" \
        -H "Content-Type:application/json" \
        -H "X-Forwarded-Proto:http" \
        -H "X-Forwarded-Host:localhost" \
        -H "X-Forwarded-Port:80" \
        -X POST --data "$(generate_post_activation_user_data)" "http://localhost:8461/users/activation"| jsonValue verified )

echo "User activated: $verified."