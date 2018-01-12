package com.xinge.chat.util.environment;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

public class AppInfo {

    // 获得指定包名的签名
    public static String getSignature(Context context) {
        PackageManager manager = context.getPackageManager();
        PackageInfo packageInfo;
        Signature[] signatures;
        StringBuilder builder = new StringBuilder();
        try {
            packageInfo = manager.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            signatures = packageInfo.signatures;
            for (Signature sign : signatures) {
                builder.append(sign.toCharsString());
            }
            return builder.toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
