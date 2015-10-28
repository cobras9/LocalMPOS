package com.mobilis.android.nfc.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * Created by lewischao on 19/06/15.
 */
public class RootUtil {
    public static boolean isDeviceRooted() {
        Log.d("LEWIS", "LEWIS isDeviceRooted checkRootMethod1 " + checkRootMethod1());
        Log.d("LEWIS", "LEWIS isDeviceRooted checkRootMethod2 " + checkRootMethod2());
        Log.d("LEWIS", "LEWIS isDeviceRooted checkRootMethod3 " + checkRootMethod3());
        Log.d("LEWIS", "LEWIS isDeviceRooted checkRootMethod4 " + checkRootMethod4());

        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3() || checkRootMethod4();
    }

    private static boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;
        Log.d("LEWIS", "LEWIS " + buildTags);
        return buildTags != null && buildTags.contains("test-keys");
    }

    private static boolean checkRootMethod2() {
        Log.d("LEWIS", "LEWIS Superuser " + new File("/system/app/Superuser.apk").exists());
        Log.d("LEWIS", "LEWIS superuser " + new File("/system/app/superuser.apk").exists());
        return new File("/system/app/Superuser.apk").exists();
    }

    private static boolean checkRootMethod3() {
        String[] paths = { "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su" };
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }

    private static boolean checkRootMethod4() {
        Process process = null;
        try {
            process= Runtime.getRuntime().exec("su");
            process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) return true;
            return false;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }
}
