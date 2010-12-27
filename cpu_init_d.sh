#!/system/bin/sh
#
# biffmod CPU settings

CMPARTS_PREF="/data/data/com.cyanogenmod.cmparts/shared_prefs/com.cyanogenmod.cmparts_preferences.xml"
LOGMSG="log -t cpu "



if [ -e $CMPARTS_PREF ]
then
    CPUMAX=`awk '/<string name="pref_freq_max">/ && sub(/<string name="pref_freq_max">/,"") && sub(/<\/string>/,"")' $CMPARTS_PREF `
    CPUMIN=`awk '/<string name="pref_freq_min">/ && sub(/<string name="pref_freq_min">/,"") && sub(/<\/string>/,"")' $CMPARTS_PREF `
    CPUGOV=`awk '/<string name="pref_cpu_gov">/ && sub(/<string name="pref_cpu_gov">/,"") && sub(/<\/string>/,"")' $CMPARTS_PREF `
    $LOGMSG "CMParts CPU Max = $CPUMAX"
    $LOGMSG "CMParts CPU Min = $CPUMIN"
    $LOGMSG "CMParts CPU Gov = $CPUGOV"

    if [ -n $CPUMIN ]
    then
        echo $CPUMIN > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq
    fi
    if [ -n $CPUGOV ]
    then
        echo $CPUGOV > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor
    fi
    if [ -n $CPUMAX ]
    then
        echo $CPUMAX > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq
    fi
fi


