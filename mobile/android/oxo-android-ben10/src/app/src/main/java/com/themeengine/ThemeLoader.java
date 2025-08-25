package com.themeengine;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class ThemeLoader {
    private static final String TAG = "ThemeLoader";
    private static final String THEME_PACKAGE_PREFIX = "com.themeengine.theme.";

    public static IThemePlugin loadTheme(Context context, String themePackage) {
        try {
            // Vulnerability: No signature verification
            Context themeContext = context.createPackageContext(
                themePackage,
                Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY
            );
            
            ClassLoader loader = themeContext.getClassLoader();
            Class<?> themeClass = loader.loadClass(themePackage + ".ThemeImplementation");
            
            return (IThemePlugin) themeClass.newInstance();
        } catch (Exception e) {
            Log.e(TAG, "Failed to load theme: " + e.getMessage());
            return null;
        }
    }

    public static boolean isValidThemePackage(String packageName) {
        return packageName != null && packageName.startsWith(THEME_PACKAGE_PREFIX);
    }

    public static PackageInfo[] getInstalledThemes(Context context) {
        PackageManager pm = context.getPackageManager();
        return pm.getInstalledPackages(0).stream()
                .filter(info -> isValidThemePackage(info.packageName))
                .toArray(PackageInfo[]::new);
    }
}
