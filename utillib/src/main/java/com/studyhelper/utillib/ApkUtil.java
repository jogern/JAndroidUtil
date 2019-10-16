package com.studyhelper.utillib;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Create on 2019/7/4.
 * @author jogern
 */
public final class ApkUtil {

      private ApkUtil() { }

      private static Class<?>[] getParamTypes(Class<?> cls, String mName) {
            Method[] mtd = cls.getMethods();
            if (mtd == null || mtd.length <= 0) {
                  return null;
            }
            Class<?> cs[] = null;
            for (Method aMtd : mtd) {
                  if (!aMtd.getName().equals(mName)) {
                        continue;
                  }
                  cs = aMtd.getParameterTypes();
            }
            return cs;
      }

      /**
       * 静默安装Apk
       * @param apkUri 格式:"file:///mnt/sdcard/starapp/starAppShop/viva.android.tv.apk"
       * @throws Exception
       */
      public static void installSilent(Uri apkUri) throws Exception {
            if (apkUri != null) {
                  Class<?> activityTherad = Class.forName("android.app.ActivityThread");
                  Class<?> paramTypes[] = getParamTypes(activityTherad, "getPackageManager");
                  Method method = activityTherad.getMethod("getPackageManager", paramTypes);
                  Object PackageManagerService = method.invoke(activityTherad);
                  Class<?> pmService = PackageManagerService.getClass();
                  Class<?> paramTypes1[] = getParamTypes(pmService, "installPackage");
                  method = pmService.getMethod("installPackage", paramTypes1);
                  method.invoke(PackageManagerService, apkUri, null, 0, null);
            }
      }

//
//      /**
//       * 杀死除本应用和系统应用之外的所有后台程序
//       */
//      public static void killAllBackgroundApp(Context context) {
//            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//            if (activityManager == null) {
//                  return;
//            }
//            String currentPkgName = context.getPackageName();
//
//            //获取系统中所有正在运行的进程
//            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
//            for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcesses) {
//                  //一个进程中所有运行的应用的包名
//                  String[] pkgNameList = appProcessInfo.pkgList;
//                  if (pkgNameList == null || pkgNameList.length <= 0) {
//                        continue;
//                  }
//                  for (String pkgName : pkgNameList) {
//                        if (currentPkgName.equals(pkgName)){
//                              continue;
//                        }
//                        PackageInfo packageInfo = queryPackageInfo(context, pkgName);
//                        if (packageInfo == null) {
//                              continue;
//                        }
//                        if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
//                              forceStopAPK(context, pkgName);
//                        }
//                  }
//            }
//      }
//
//      /**
//       * 强制停止后台运行的应用
//       * <p/>需要增加权限：android.permission.FORCE_STOP_PACKAGES
//       * @param pkgName 包名
//       */
//      public static void forceStopAPK(Context context, String pkgName) {
//            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//            try {
//                  Class<?> mClass = Class.forName("android.app.ActivityManager");
//                  Method forceStopPackage = mClass.getMethod("forceStopPackage", String.class);
//                  forceStopPackage.setAccessible(true);
//                  forceStopPackage.invoke(am, pkgName);
//            } catch (Exception e) {
//                  //  Logcat.e(e);
//            }
//      }


      public static void killBackgroundProcesses(Context context, String pkgName) {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                  am.killBackgroundProcesses(pkgName);
            }
      }

      /**
       * 根据包名查询PackageInfo（应用信息）
       * @param pkgName
       * @return PackageInfo 没有返回null
       */
      public static PackageInfo queryPackageInfo(Context context, String pkgName) {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = null;
            try {
                  packageInfo = packageManager.getPackageInfo(pkgName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                  // Logcat.e(e);
            }
            return packageInfo;
      }

      /**
       * 获得属于桌面的应用的应用包名称
       */
      public static List<ResolveInfo> getLauncherApps(Context context) {
            PackageManager packageManager = context.getPackageManager();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            return packageManager.queryIntentActivities(intent, PackageManager.SIGNATURE_MATCH);
      }

      public static boolean isSystemApp(Context context, String pkgName) {
            PackageInfo info = queryPackageInfo(context, pkgName);
            if (info == null) {
                  return false;
            }
            boolean isSystem = (info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
            if (isSystem) {
                  return true;
            }
            isSystem = (info.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
            return isSystem;
      }

      public static boolean isUserApp(Context context, String pkgName) {
            return !isSystemApp(context, pkgName);
      }

}
