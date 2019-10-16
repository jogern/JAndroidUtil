package com.studyhelper.utillib;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import java.lang.reflect.Method;

/**
 * Create on 2019/6/10.
 * @author jogern
 */
public final class ScreenUtil {

      private static final String RES_NAME = "status_bar_height";

      private ScreenUtil() { }

      public static int getStatusBarHeight() {
            int result = 0;
            try {
                  int resourceId = Resources.getSystem().getIdentifier(RES_NAME, "dimen", "android");
                  if (resourceId > 0) {
                        result = Resources.getSystem().getDimensionPixelSize(resourceId);
                  }
            } catch (Resources.NotFoundException e) {
                  e.printStackTrace();
            }
            return result;
      }

      /**
       * 获取当前的屏幕尺寸
       * @param context {@link Context}
       * @return 屏幕尺寸
       */
      public static int[] getScreenSize(Context context) {
            int[] size = new int[2];

            WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (w != null) {
                  Display d = w.getDefaultDisplay();
                  DisplayMetrics metrics = new DisplayMetrics();
                  d.getMetrics(metrics);

                  size[0] = metrics.widthPixels;
                  size[1] = metrics.heightPixels;
            }
            return size;
      }


      /**
       * 非全面屏下 虚拟键高度(无论是否隐藏)
       * @param context
       * @return
       */
      public static int getNavigationBarHeight(Context context){
            int result = 0;
            int resourceId = context.getResources().getIdentifier("navigation_bar_height","dimen", "android");
            if (resourceId > 0) {
                  result = context.getResources().getDimensionPixelSize(resourceId);
            }
            return result;
      }


      /**
       * 非全面屏下 虚拟按键是否打开
       * @param activity
       * @return
       */
      public static boolean isNavigationBarShown(Activity activity) {
            //虚拟键的view,为空或者不可见时是隐藏状态
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                  View view = activity.findViewById(android.R.id.navigationBarBackground);
                  if (view == null) {
                        return false;
                  }
                  int visible = view.getVisibility();
                  if (visible == View.GONE || visible == View.INVISIBLE) {
                        return false;
                  }
                  return true;
            }
            //else if (android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            Resources res = activity.getResources();
            int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
            if (resourceId != 0) {
                  boolean hasNav = res.getBoolean(resourceId);
                  // check override flag
                  String sNavBarOverride = getNavBarOverride();
                  if ("1".equals(sNavBarOverride)) {
                        hasNav = false;
                  } else if ("0".equals(sNavBarOverride)) {
                        hasNav = true;
                  }
                  return hasNav;
            } else { // fallback
                  return !ViewConfiguration.get(activity).hasPermanentMenuKey();
            }
            //  }

      }

      /**
       * 判断虚拟按键栏是否重写
       * @return
       */
      private static String getNavBarOverride() {
            String sNavBarOverride = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                  try {
                        Class c = Class.forName("android.os.SystemProperties");
                        Method m = c.getDeclaredMethod("get", String.class);
                        m.setAccessible(true);
                        sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
                  } catch (Throwable e) {
                  }
            }
            return sNavBarOverride;
      }


      public static void showNavigationBar(Activity activity, boolean show) {
            try {
                  if (show) {
                        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
                              // lower api
                              View v = activity.getWindow().getDecorView();
                              v.setSystemUiVisibility(View.GONE);
                        } else if (Build.VERSION.SDK_INT >= 19) {
                              activity.getWindow().getDecorView().setSystemUiVisibility(
                                      View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                              // | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                              | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                              );
                        }

                  } else {
                        // Set the IMMERSIVE flag.
                        // Set the content to appear under the system bars so that the content
                        // doesn't resize when the system bars hide and show.
                        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
                              // lower api
                              View v = activity.getWindow().getDecorView();
                              v.setSystemUiVisibility(View.VISIBLE);
                        } else if (Build.VERSION.SDK_INT >= 19) {
                              activity.getWindow().getDecorView().setSystemUiVisibility(
                                      View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                              | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                              | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                              | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                              | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                              | View.SYSTEM_UI_FLAG_IMMERSIVE);
                        }
                  }
            } catch (Exception e) {
                  e.printStackTrace();
            }
      }

      public static int dp2px(Context context, float value) {
            final float scale = context.getResources().getDisplayMetrics().densityDpi;
            return (int) (value * (scale / 160) + 0.5f);
      }

}
