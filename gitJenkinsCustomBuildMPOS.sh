#!/bin/bash
FINAL_PROFILE=$3
nfc_enabled=$4
optional_server=$5
optional_port=$6
need_mobmonpin_instead_of_ussd=$7
load_transfer_coder_from_server=$8
customVersion=$9
TAG=${customVersion:4}
APP_VERSION_INTERNAL=$customVersion

if [ $FINAL_PROFILE == "main" ]
then
   FINAL_PROFILE="MobilisDemo"
else
   FINAL_PROFILE=$3
fi
export FINAL_PROFILE

if [ "$optional_server" != "" ] && [ "$optional_server" != "X" ]
then
   DESIRED_SERVER_IP=$optional_server
else
   DESIRED_SERVER_IP=$1
fi
if [ "$optional_port" != "" ] && [ "$optional_port" != "X" ]
then
   DESIRED_PORT=$optional_port
else
   DESIRED_PORT=$2
fi

export TAG
export PREVTAG
export DESIRED_SERVER_IP
export DESIRED_PORT
export APP_VERSION_INTERNAL
export nfc_enabled
export need_mobmonpin_instead_of_ussd
export load_transfer_coder_from_server

echo 'TAG:' $TAG
echo 'PREVTAG:' $PREVTAG
echo 'DESIRED_SERVER_IP:' $DESIRED_SERVER_IP
echo 'DESIRED_PORT:' $DESIRED_PORT
echo 'APP_VERSION_INTERNAL:' $APP_VERSION_INTERNAL
echo 'FINAL_PROFILE:' $FINAL_PROFILE
export Profile=$FINAL_PROFILE
echo 'Profile:' $Profile
echo 'nfc_enabled:' $nfc_enabled
echo 'need_mobmonpin_instead_of_ussd : ' $need_mobmonpin_instead_of_ussd
echo 'load_transfer_coder_from_server : ' $load_transfer_coder_from_server

sed -e "s/@string\/APP_VERSION_INTERNAL/$APP_VERSION_INTERNAL/g" MMWalletAndroid/AndroidManifest.xml > MMWalletAndroid/AndroidManifest.xml.tmp && mv MMWalletAndroid/AndroidManifest.xml.tmp MMWalletAndroid/AndroidManifest.xml
sed -e "s/@integer\/APP_VERSION_CODE/$TAG/g" MMWalletAndroid/AndroidManifest.xml > MMWalletAndroid/AndroidManifest.xml.tmp && mv MMWalletAndroid/AndroidManifest.xml.tmp MMWalletAndroid/AndroidManifest.xml
sed "s/.*APP_VERSION_INTERNAL.*/<string name=\"APP_VERSION_INTERNAL\">$APP_VERSION_INTERNAL<\/string>/g" MMWalletAndroid/src/main/res/values/donottranslate.xml > MMWalletAndroid/src/main/res/values/donottranslate.xml.tmp && mv MMWalletAndroid/src/main/res/values/donottranslate.xml.tmp MMWalletAndroid/src/main/res/values/donottranslate.xml
sed "s/.*APP_VERSION_CODE.*/<integer name=\"APP_VERSION_CODE\">$TAG<\/integer>/g" MMWalletAndroid/src/main/res/values/donottranslate.xml > MMWalletAndroid/src/main/res/values/donottranslate.xml.tmp && mv MMWalletAndroid/src/main/res/values/donottranslate.xml.tmp MMWalletAndroid/src/main/res/values/donottranslate.xml
sed "s/.*name=\"SERVER_IP\".*/<string name=\"SERVER_IP\">$DESIRED_SERVER_IP<\/string>/g" MMWalletAndroid/src/main/res/values/donottranslate.xml > MMWalletAndroid/src/main/res/values/donottranslate.xml.tmp && mv MMWalletAndroid/src/main/res/values/donottranslate.xml.tmp MMWalletAndroid/src/main/res/values/donottranslate.xml
sed "s/.*name=\"SERVER_PORT\".*/<string name=\"SERVER_PORT\">$DESIRED_PORT<\/string>/g" MMWalletAndroid/src/main/res/values/donottranslate.xml > MMWalletAndroid/src/main/res/values/donottranslate.xml.tmp && mv MMWalletAndroid/src/main/res/values/donottranslate.xml.tmp MMWalletAndroid/src/main/res/values/donottranslate.xml
sed "s/.*name=\"LOAD_TRANSFERCODES_FROM_SERVER\".*/<bool name=\"LOAD_TRANSFERCODES_FROM_SERVER\">$load_transfer_coder_from_server<\/bool>/g" MMWalletAndroid/src/main/res/values/donottranslate.xml > MMWalletAndroid/src/main/res/values/donottranslate.xml.tmp && mv MMWalletAndroid/src/main/res/values/donottranslate.xml.tmp MMWalletAndroid/src/main/res/values/donottranslate.xml


i=0; 

for file in $(grep --include=\*.xml -l -R 'SERVER_IP' ./MMWalletAndroid/src/)
do
  sed "s/.*name=.*\"SERVER_IP\".*/<string name=\"SERVER_IP\">$DESIRED_SERVER_IP<\/string>/g" $file > tempfile.tmp && mv tempfile.tmp $file
  sed "s/.*name=.*\"SERVER_PORT\".*/<string name=\"SERVER_PORT\">$DESIRED_PORT<\/string>/g" $file > tempfile.tmp && mv tempfile.tmp $file
  let i++;
  echo "Modified: " $file
done

i=0;
for file in $(grep --include=\*.xml -l -R 'ONBOARD_NFC' ./MMWalletAndroid/src/)
do
  sed "s/.*name=.*\"ONBOARD_NFC\".*/<string translatable=\"false\" name=\"ONBOARD_NFC\">$nfc_enabled<\/string>/g" $file > tempfile.tmp && mv tempfile.tmp $file
    let i++;
  echo "Modified NFC function: " $file
done


i=0;
for file in $(grep --include=\*.xml -l -R 'PAY_PIN_REQUIRED' ./MMWalletAndroid/src/)
do
  sed "s/.*name=.*\"PAY_PIN_REQUIRED\".*/<bool name=\"PAY_PIN_REQUIRED\">$need_mobmonpin_instead_of_ussd<\/bool>/g" $file > tempfile.tmp && mv tempfile.tmp $file
    let i++;
  echo "Modified USSD pin entry value: " $file
done

CONFIGURATION_FILE=${JENKINS_HOME}/jobs/${JOB_NAME}/builds/${BUILD_NUMBER}/fileParameters/configurations.zip

if [ -f $CONFIGURATION_FILE ];
then
   echo "File $FILE exists"
   unzip -o ${JENKINS_HOME}/jobs/${JOB_NAME}/builds/${BUILD_NUMBER}/fileParameters/configurations.zip -d ${WORKSPACE}/MMWalletAndroid/assets/
else
   echo "File $FILE does not exists, skipping extracting custom configurations"
fi
