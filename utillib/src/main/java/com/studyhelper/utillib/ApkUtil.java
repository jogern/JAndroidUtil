package com.studyhelper.utillib;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Create on 2019/7/4.
 * @author jogern
 */
public final class ApkUtil {

      private static final String M_REMOVE_TASK = "removeTask";

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


      public static Method findMethod(Class<?> cls, String methodName, int paramsCount) {
            if (cls != null) {
                  //先获取该类中的所有方法(不包括父类中继承的方法)
                  Method[] methods = cls.getDeclaredMethods();
                  Method method = findMethod(methods, methodName, paramsCount);
                  if (method != null) {
                        return method;
                  }
                  //获取有访问权限的方法(包括父类中继承的方法)
                  methods =  cls.getMethods();
                  method = findMethod(methods, methodName, paramsCount);
                  return method;
            }
            return null;
      }

      private static Method findMethod(Method[] methods, String methodName, int paramsCount) {
            if (methods == null || methods.length <= 0) {
                  return null;
            }
            if (paramsCount < 0) {
                  paramsCount = 0;
            }
            Class<?>[] parameterTypes;
            for (Method method : methods) {
                  if (!method.getName().equals(methodName)) {
                        continue;
                  }
                  parameterTypes = method.getParameterTypes();
                  Logcat.w("method: " + method.getName() + " parameterTypes.length: " + parameterTypes.length);
                  if (parameterTypes.length == paramsCount) {
                        return method;
                  }
            }
            return null;
      }

      /**
       * 静默安装Apk
       * @param apkUri 格式:"file:///mnt/sdcard/starapp/starAppShop/viva.android.tv.apk"
       * @throws Exception 异常
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
//            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context
//            .ACTIVITY_SERVICE);
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


//      public static void killBackgroundProcesses(Context context, String pkgName) {
//            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//            if (am != null) {
//                  am.killBackgroundProcesses(pkgName);
//            }
//      }


      /**
       * 获取最近任务对象中的包名
       * @param taskInfo 最近任务对象
       * @return 包名, 可能为 null
       */
      public static String getInfoPkg(ActivityManager.RecentTaskInfo taskInfo) {
            String pkg = null;
            Intent intent = taskInfo.baseIntent;
            if (intent != null) {
                  pkg = intent.getPackage();
                  if (pkg == null || pkg.length() <= 0) {
                        ComponentName component = intent.getComponent();
                        pkg = component == null ? null : component.getPackageName();
                  }
            }
            if (pkg == null || pkg.length() <= 0) {
                  if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        ComponentName baseActivity = taskInfo.baseActivity;
                        if (baseActivity != null) {
                              pkg = baseActivity.getPackageName();
                        }
                        if (pkg == null || pkg.length() <= 0) {
                              baseActivity = taskInfo.topActivity;
                              if (baseActivity != null) {
                                    pkg = baseActivity.getPackageName();
                              }
                        }
                  }
            }
            return pkg;
      }

      /**
       * 获取全部最近任务
       * @param context 上下文
       * @return 最近任务列表
       */
      public static List<ActivityManager.RecentTaskInfo> getRecentTask(Context context) {
            ActivityManager activityManager = context == null ? null :
                    (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (activityManager == null) {
                  return null;
            }
            return activityManager.getRecentTasks(500, ActivityManager.RECENT_WITH_EXCLUDED);
      }

      /**
       * 移除全部最近任务结束 apk
       * @param context        上下文
       * @param isRemoveSelf   是否移除上下文的包名应用
       * @param filterPkgArray 不移除的的应用
       */
      public static void removeAllTask(Context context, boolean isRemoveSelf, String... filterPkgArray) {
            ActivityManager activityManager = context == null ? null :
                    (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (activityManager == null) {
                  return;
            }
            List<ActivityManager.RecentTaskInfo> recentTasks = activityManager.getRecentTasks(500,
                    ActivityManager.RECENT_WITH_EXCLUDED);
            if (recentTasks == null || recentTasks.isEmpty()) {
                  return;
            }
            List<String> filterPkgList = new ArrayList<>();
            if (filterPkgArray != null && filterPkgArray.length > 0) {
                  Collections.addAll(filterPkgList, filterPkgArray);
            }
            if (isRemoveSelf) {
                  filterPkgList.add(context.getPackageName());
            }
            String pkg;
            for (ActivityManager.RecentTaskInfo recentTask : recentTasks) {
                  pkg = getInfoPkg(recentTask);
                  if (filterPkgList.contains(pkg)) {
                        continue;
                  }
                  if (!removeTask(activityManager, recentTask.persistentId)) {
                        Logcat.e("remove task(" + pkg + ":" + recentTask.persistentId + ") fail .....");
                  }
            }
      }

      /**
       * 移除最近任务结束 apk
       * @param context          上下文
       * @param taskPersistentId 任务 ID
       * @return 是否移除成功
       */
      public static boolean removeTask(Context context, int taskPersistentId) {
            ActivityManager activityManager = context == null ? null :
                    (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (activityManager == null) {
                  return false;
            }
            return removeTask(activityManager, taskPersistentId);
      }

      /**
       * 移除最近任务结束 apk
       * @param manager ActivityManager
       * @param taskId  任务 ID
       * @return 是否移除成功
       */
      private static boolean removeTask(ActivityManager manager, int taskId) {
            Class<?> managerClass = manager.getClass();
            try {
                  Object returnParam = Boolean.FALSE;
                  int sdkInt = Build.VERSION.SDK_INT;
                  if (sdkInt >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && sdkInt <= Build.VERSION_CODES.LOLLIPOP) {
                        Method method = findMethod(managerClass, M_REMOVE_TASK, 2);
                        if (method != null) {
                              returnParam = method.invoke(manager, taskId, 0x0001);
                        }
                  } else if (sdkInt >= Build.VERSION_CODES.LOLLIPOP_MR1 && sdkInt <= Build.VERSION_CODES.O_MR1) {
                        Method method = findMethod(managerClass, M_REMOVE_TASK, 1);
                        if (method != null) {
                              returnParam = method.invoke(manager, taskId);
                        }
                  } else if (sdkInt == Build.VERSION_CODES.P) {
                        Method method = managerClass.getMethod("getService");
                        Object iService = method.invoke(manager);
                        if (iService != null) {
                              method = findMethod(iService.getClass(), M_REMOVE_TASK, 1);
                              if (method != null) {
                                    returnParam = method.invoke(iService, taskId);
                              }
                        }
                  }
                  if (returnParam instanceof Boolean) {
                        return (Boolean) returnParam;
                  }
            } catch (Exception e) {
                  Logcat.e(e);
            }
            return false;
      }

      /**
       * 根据包名查询PackageInfo（应用信息）
       * @param pkgName 包名
       * @return PackageInfo 没有返回null
       */
      public static PackageInfo queryPackageInfo(Context context, String pkgName) {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = null;
            try {
                  packageInfo = packageManager.getPackageInfo(pkgName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                  Logcat.e(e);
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
            return packageManager.queryIntentActivities(intent, 0);
      }

      /**
       * 判断是否是系统的应用
       * @param context 上下文
       * @param pkgName 包名
       * @return true 为系统的应用
       */
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

      /**
       * 判断是否是用户安装的应用
       * @param context 上下文
       * @param pkgName 包名
       * @return true 为用户安装的应用
       */
      public static boolean isUserApp(Context context, String pkgName) {
            return !isSystemApp(context, pkgName);
      }

}
