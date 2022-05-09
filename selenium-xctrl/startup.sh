#!/bin/bash
#
# Appium automation laucher script
#
# @author dmarafetti
# @since 1.0.1
#
#

YELLOW='\033[1;33m'
RED='\033[1;31m'
GREEN='\033[1;32m'
NC='\033[0m'

clear;
echo -e "${GREEN}"
echo "  ___        _                        _   _                             _       _   "
echo " / _ \      | |                      | | (_)                           (_)     | |  "
echo "/ /_\ \_   _| |_ ___  _ __ ___   __ _| |_ _  ___  _ __    ___  ___ _ __ _ _ __ | |_ "
echo "|  _  | | | | __/ _ \| '_ \` _ \ / _\` | __| |/ _ \| '_ \  / __|/ __| '__| | '_ \| __|"
echo "| | | | |_| | || (_) | | | | | | (_| | |_| | (_) | | | | \__ | (__| |  | | |_) | |_ "
echo "\_| |_/\__,_|\__\___/|_| |_| |_|\__,_|\__|_|\___/|_| |_| |___/\___|_|  |_| .__/ \__|"
echo "                                                                         | |        "
echo "                                                                         |_|        "
echo -e "${NC}"


if ! pgrep "adb" > /dev/null; then

    echo -n "Starting Android Debug Bridge... "

    adb start-server &> /dev/null

    wait
    echo "done!"
fi



devices=($(adb devices | grep device$ | awk '{print $1}'))

if [ ${#devices[@]} -eq 0 ]; then

    echo "No devices were found. done!"
    echo ""
    echo ""
    exit

else

    sort <(for f in "${devices[@]}" ; do echo "$f" ; done) |

        awk 'BEGIN { \
                      print "List of Android devices connected"; \
                      print "----------------------------------" \
                   } \
        {print "device: ", $1}'
fi




pgrep -fl 'appium' |

     awk 'BEGIN { \
                    print ""; \
                    print "Killing previous Appium instances"; \
                    print "---------------------------------"; \
                } \
                { \
                    print "Instance found: ", $0; \
                    cmd = "kill -9 "$1; \
                    system(cmd); \
                } \
          END {print "done!"}'



echo ""
echo "Reading configuration file"
echo "--------------------------"

if [ ! -f ./src/test/resources/com/ideasquefluyen/selenium/xctrl/appium/device.properties ]; then

    echo "File not found!"
    exit

else

    . ./src/test/resources/com/ideasquefluyen/selenium/xctrl/appium/device.properties
    echo "done!"
    echo ""
fi


echo ""
echo "Unlocking device"
echo "--------------------------"

for d in "${devices[@]}";
do

    #
    # Force screen unlocking
    #

    adb -P 5037 -s $d shell am start -S io.appium.unlock/.Unlock
    adb -P 5037 -s $d shell am start io.appium.unlock/.Unlock
    wait

done


echo ""
echo "Executing test suite"
echo "--------------------------"
echo ""


host="localhost"
port=4721
bport=2251
cdport=9515
pids=()

for index in `seq 1 ${#devices[@]}`;
do


    #
    # Get variables from properties file
    #

    device=`printf "appium_test_%s_device" "$index"`
    apk=`printf "appium_test_%s_apk" "$index"`
    version=`printf "appium_test_%s_version" "$index"`
    package=`printf "appium_test_%s_package" "$index"`
    suite=`printf "appium_test_%s_suite" "$index"`

    echo -e "${YELLOW}Configuration - ${!device}${NC}"
    echo "apk  : ${!apk}"
    echo "suite: ${!suite}"



    #
    # Start Appium server this device
    #

    echo "starting Appium: "
	appiumCommand="appium -p ${port} -bp ${bport} --chromedriver-port ${cdport} --log-level debug"
    echo "${appiumCommand}"
    $appiumCommand &> ./appium_${index}.log&



    #
    # Execute maven for test suite
    #

    echo "running suite..."
    mavenCommand="mvn clean test -Dselenium.browser=hybrid \
                                 -Dselenium.hybridHost=${host} \
                                 -Dselenium.hybridPort=${port} \
                                 -Dselenium.hybridDeviceName=${!device} \
                                 -Dselenium.hybridApk=${!apk} \
                                 -DselbridPackage={!package} \
                                 -Dselenium.hybridEngine=Appium \
                                 -Dselenium.hybridAndroidVersion=${!version} \
                                 -Dselenium.suiteFile=${!suite} \
                                 -PotherOutputDir \
                                 -DbuildDirectory=target_${index}"

    $mavenCommand &> ./debug_${index}.log&
    pids[index]=$!


    #
    # increment port number
    #

    port=$((port+1))
    bport=$((bport+1))
    cdport=$((cdport+1))

    echo ""
    echo ""
done

#
# Wait for Maven child process to finish
#
echo  "working..."

for d in "${pids[@]}";
do
    wait $d;
    echo -e "${RED}Maven instance $d has ended.${NC}"
done
echo "end"

