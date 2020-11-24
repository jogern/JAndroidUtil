package com.studyhelper.utillib;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.security.MessageDigest;
import java.util.Locale;
import java.util.UUID;

/**
 * Create on 2019-07-15.
 * @author zujianliang
 */
public final class StringUtil {

      private StringUtil() { }

      public static boolean isEmpty(CharSequence text) {
            return text == null || text.length() <= 0;
      }

      /** 获取设备ID(不一定唯一) 需要READ_PHONE_STATE权限 */
      public static String getDeviceUUID() {
            String serial = null;
            String m_szDevIDShort = "35" +
                    Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                    Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                    Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                    Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                    Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                    Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                    Build.USER.length() % 10; //13 位
            try {
                  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        serial = android.os.Build.getSerial();
                  } else {
                        serial = Build.SERIAL;
                  }
                  //API>=9 使用serial号
                  return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
            } catch (Exception exception) {
                  //serial需要一个初始化
                  serial = "serial"; // 随便一个初始化
            }
            //使用硬件信息拼凑出来的15位号码
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
      }

      public static String md5(String text) {
            if (text == null || text.isEmpty()) {
                  return "";
            }
            try {
                  byte[] bytes = MessageDigest.getInstance("MD5").digest(text.getBytes());
                  StringBuilder md5 = new StringBuilder();
                  for (byte byteData : bytes) {
                        md5.append(String.format("%02X", byteData & 0xff));
                  }
                  return md5.toString();
            } catch (Exception e) {
                  Logcat.w("\nproduce md5 exception: " + getStackMsg(e));
            }
            return "";
      }

      /** 获取签名SHA1 */
      public static String getSHA1Signature(Context context) {
            try {
                  PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
                          PackageManager.GET_SIGNATURES);
                  byte[] cert = info.signatures[0].toByteArray();
                  MessageDigest md = MessageDigest.getInstance("SHA1");
                  byte[] publicKey = md.digest(cert);
                  StringBuilder hexString = new StringBuilder();
                  for (int i = 0; i < publicKey.length; i++) {
                        String appendString = Integer.toHexString(0xFF & publicKey[i]).toUpperCase(Locale.US);
                        if (i > 0) {
                              hexString.append(":");
                        }
                        if (appendString.length() == 1)
                              hexString.append("0");
                        hexString.append(appendString);
                  }
                  return hexString.toString();
            } catch (Exception e) {
                  e.printStackTrace();
            }
            return null;
      }

      public static String getStackMsg(Throwable e) {
            if (e == null) {
                  return "";
            }
            String text = "\n" + e.getMessage();
            StackTraceElement[] stackTrace = e.getStackTrace();
            if (stackTrace != null && stackTrace.length > 0) {
                  for (StackTraceElement stack : stackTrace) {
                        text += "\n" + stack.toString();
                  }
            }
            return text;
      }

      /**
       * 单位毫秒
       * @param timeMs
       * @return
       */
      public static String timeMsFormat(long timeMs) {
            if (timeMs <= 0) {
                  return "00:00";
            }
            long totalSeconds = timeMs / 1000;
            int seconds = (int) totalSeconds % 60;
            int minutes = (int) totalSeconds / 60;
            return String.format("%02d:%02d", minutes, seconds);
      }

      /**
       * 单位毫秒
       * @param timeMs
       * @return
       */
      public static String timeMsFormatFull(long timeMs) {
            if (timeMs <= 0) {
                  return "00:00:00";
            }
            long totalSeconds = timeMs / 1000;
            int hour = (int) totalSeconds / 60 / 60;
            int minutes = (int) (totalSeconds / 60) % 60;
            int seconds = (int) totalSeconds % 60;
            return String.format("%02d:%02d:%02d", hour, minutes, seconds);
      }

}
