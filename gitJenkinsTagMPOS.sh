#!/bin/bash
#Updating git tags
git pull origin master
#Setting latest tag
PREVTAG=`git describe --abbrev=0`
#Converting tags
PREVTAG=${PREVTAG:4}
TAG='1.2.'$((PREVTAG+1))

FINAL_PROFILE=$1
SELECTED_IP=$2
SELECTED_PORT=$3
optional_server=$4
optional_port=$5
nfc_enabled=$6
need_mobmonpin_instead_of_ussd=$7
load_transfer_coder_from_server=$8

if [ "$optional_server" != "" ] && [ "$optional_server" != "X" ]
then
   DESIRED_SERVER_IP=$optional_server
else
   DESIRED_SERVER_IP=$SELECTED_IP
fi
if [ "$optional_port" != "" ] && [ "$optional_port" != "X" ]
then
   DESIRED_PORT=$optional_port
else
   DESIRED_PORT=$SELECTED_PORT
fi
export DESIRED_SERVER_IP
export DESIRED_PORT

echo 'Last tag number:' $PREVTAG
echo "Final taging number :" $TAG
echo "Profile for this tag :" $FINAL_PROFILE
echo "IP used for tag :" $DESIRED_SERVER_IP
echo "Port used for this tag :" $DESIRED_PORT
echo "nfc_enabled used for this tag :" $nfc_enabled
echo "need_mobmonpin_instead_of_ussd  used for this tag :" $need_mobmonpin_instead_of_ussd
echo "load_transfer_coder_from_server used for this tag :" $load_transfer_coder_from_server

git tag -a $TAG -m "Release Tag $TAG for $FINAL_PROFILE  IP:$DESIRED_SERVER_IP  PORT:$DESIRED_PORT NFC:$nfc_enabled USSD:$need_mobmonpin_instead_of_ussd LOAD_Transfer_Code_From_Server: $load_transfer_coder_from_server"

git config user.name "Build (devwiki)"
git config user.email "info.gitlab@mobilis.com"

#Pushing tag to remote
git push --tags
echo "Finished tagging for : Release Tag $TAG for Profile: $FINAL_PROFILE  IP:$DESIRED_SERVER_IP  PORT:$DESIRED_PORT NFC:$nfc_enabled USSD:$need_mobmonpin_instead_of_ussd LOAD_Transfer_Code_From_Server: $load_transfer_coder_from_server"