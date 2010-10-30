#!/system/bin/sh
#
# biffmod dex2sd
# /sd-ext must be mounted before this script is ran
# script by ezterry; however I'm sure firerat seeded some of the ideas

EXT_IS_MOUNTED=`grep /sd-ext /proc/mounts | wc -l`
CMPARTS_PREF="/data/data/com.cyanogenmod.cmparts/shared_prefs/com.cyanogenmod.cmparts_preferences.xml"
LOGMSG="log -t dex2sd "

#If /sd-ext is not mounted there is nothing we can do
if [ "$EXT_IS_MOUNTED" = "0" ]
then
    $LOGMSG "/sd-ext not mounted skipping dex2sd functionality"
    exit 0
fi

$LOGMSG "/sd-ext mounted; check CMParts preferences"

#get the value of pref_enable_dex2sd from CMParts pref pereferences xml
DEX2SD_ENABLE="false"
if [ -e $CMPARTS_PREF ]
then
    $LOGMSG "CMParts Preferances Found; parsing"
    DEX2SD_ENABLE=`cat $CMPARTS_PREF | awk '/pref_enable_dex2sd/ {m=match($0,/value=\"([^\"]+)/); print(substr($0,RSTART+7,RLENGTH-7))}'`
    $LOGMSG "CMParts enable DEX2SD = $DEX2SD_ENABLE"
fi

#Now remove old dex dirs and create bind mounts as needed

if [ "$DEX2SD_ENABLE" = "true" ]
then
    #push to sdcard
    logwrapper busybox rm -rf /data/dalvik-cache
    mkdir /data/dalvik-cache
    logwrapper busybox chown system:system /data/dalvik-cache
    logwrapper busybox chmod 771 /data/dalvik-cache
    
    if [ -d /sd-ext/dalvik-cache ] 
    then
        #sd-ext dalvik already exists good; just clean up perms
        logwrapper busybox chown system:system /sd-ext/dalvik-cache
        logwrapper busybox chmod 771 /sd-ext/dalvik-cache
    else
        #no dir create it
        mkdir /sd-ext/dalvik-cache
        logwrapper busybox chown system:system /sd-ext/dalvik-cache
        logwrapper busybox chmod 771 /sd-ext/dalvik-cache
    fi
    $LOGMSG "make bind mount"
    logwrapper busybox mount -o bind /sd-ext/dalvik-cache /data/dalvik-cache
    $LOGMSG "dex2sd mounted"
else
    #keep internal (kill any dex files on SD)
    if [ -d /sd-ext/dalvik-cache ]
    then
        logwrapper busybox rm -rf /sd-ext/dalvik-cache
        $LOGMSG "cleared /sd-ext/dalvik-cache"
    fi
    $LOGMSG "Using internal no bind mount"
fi
$LOGMSG "done"

