package com.studyhelper.utillib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

/**
 * Create on 2019-09-11.
 * @author zujianliang
 */
public final class NetworkUtil {

      private NetworkUtil() {
      }


      /**
       * 检测当的网络（WLAN、3G/2G）状态
       * @param context Context
       * @return true 表示网络可用
       */
      public static boolean isNetworkAvailable(Context context) {
            ConnectivityManager connectivity =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                  NetworkInfo info = connectivity.getActiveNetworkInfo();
                  if (info != null && info.isConnected()) {
                        // 当前网络是连接的
                        if (info.getState() == NetworkInfo.State.CONNECTED) {
                              // 当前所连接的网络可用
                              return true;
                        }
                  }
            }
            return false;
      }

      /**
       * Android 6.0 之前（不包括6.0）获取mac地址
       * 必须的权限 <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
       * @param context
       * @return
       */
      private static String getMacDefault(Context context) {
            String mac = "";
            if (context == null) {
                  return mac;
            }
            WifiManager wifi =
                    (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = null;
            try {
                  info = wifi.getConnectionInfo();
            } catch (Exception e) {
                  e.printStackTrace();
            }

            if (info == null) {
                  return null;
            }
            mac = info.getMacAddress();
            if (!TextUtils.isEmpty(mac)) {
                  mac = mac.toUpperCase(Locale.ENGLISH);
            } else {
                  mac = " ";
            }
            Logcat.e("mac address: " + mac);
            return mac;
      }

      /**
       * Android 6.0-Android 7.0 获取mac地址
       */
      private static String getMacAddress() {
            String macSerial = null;
            String str = "";

            Process pp = null;
            try {
                  pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
                  InputStreamReader ir = new InputStreamReader(pp.getInputStream());
                  LineNumberReader input = new LineNumberReader(ir);

                  while (null != str) {
                        str = input.readLine();
                        if (str != null) {
                              //去空格
                              macSerial = str.trim();
                              break;
                        }
                  }
            } catch (IOException ex) {
                  // 赋予默认值
                  ex.printStackTrace();
            }finally {
                  if (pp!=null){
                        pp.destroy();
                  }
            }
            return macSerial;
      }

      /**
       * Android 7.0之后获取Mac地址
       * 遍历循环所有的网络接口，找到接口是 wlan0
       * 必须的权限 <uses-permission android:name="android.permission.INTERNET"></uses-permission>
       * @return
       */
      private static String getMacFromHardware() {
            try {
                  List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
                  for (NetworkInterface nif : all) {
                        if (!"wlan0".equals(nif.getName())) {
                              continue;
                        }
                        byte[] macBytes = nif.getHardwareAddress();
                        if (macBytes == null) {
                              return "";
                        }
                        StringBuilder res1 = new StringBuilder();
                        for (Byte b : macBytes) {
                              res1.append(String.format("%02X:", b));
                        }
                        if (!TextUtils.isEmpty(res1)) {
                              res1.deleteCharAt(res1.length() - 1);
                        }
                        return res1.toString();
                  }
            } catch (Exception e) {
                  e.printStackTrace();
            }

            return "";
      }

      /**
       * 通用获取 mac 地址
       * @param context
       * @return
       */
      public static String macAddress(Context context) {
            String mac = "";
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                  mac = getMacDefault(context);
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                  mac = getMacAddress();
            } else {
                  mac = getMacFromHardware();
            }
            return mac;
      }


      public static String getMacAddress(Context context) {
            WifiManager wifi =
                    (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            boolean wifiEnable = wifi.isWifiEnabled();
            int i = 0;
            while (!wifiEnable) {
                  // Log.e(TAG, "wifi close");
                  wifi.setWifiEnabled(true);
                  SystemClock.sleep(1000);
                  wifiEnable = wifi.isWifiEnabled();
                  if (i++ >= 5) {
                        // Log.e(TAG, "WifiEnabled timeout");
                        break;
                  }
            }

            String m_szWLANMAC = null;

            i = 0;
            while (m_szWLANMAC == null || "".equals(m_szWLANMAC) || "unknown".equals(m_szWLANMAC)) {
                  if (Build.VERSION.SDK_INT >= 21) {
                        m_szWLANMAC = getHighVersionMac();
                  } else {
                        WifiInfo info = wifi.getConnectionInfo();
                        m_szWLANMAC = info.getMacAddress();
                  }
                  if ((!TextUtils.isEmpty(m_szWLANMAC) && !"unknown".equals(m_szWLANMAC))) {
                        break;
                  }
                  // Log.e("mac", "" + m_szWLANMAC);
                  SystemClock.sleep(1000);
                  if (i++ >= 5) {
                        //Log.e(TAG, "get mac timeout");
                        break;
                  }
            }

//        if (wifiEnable == false) {
//            //wifi.setWifiEnabled(wifiEnable);
//        }

            return m_szWLANMAC;
      }

      private static String getHighVersionMac() {
            try {
                  Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                  while (interfaces.hasMoreElements()) {
                        NetworkInterface iF = interfaces.nextElement();

                        byte[] addr = iF.getHardwareAddress();
                        if (addr == null || addr.length == 0) {
                              continue;
                        }

                        StringBuilder buf = new StringBuilder();
                        for (byte b : addr) {
                              buf.append(String.format("%02x:", b));
                        }
                        if (buf.length() > 0) {
                              buf.deleteCharAt(buf.length() - 1);
                        }
                        String mac = buf.toString();
                        //Log.d("mac", "interfaceName=" + iF.getName() + ", mac=" + mac);
                        if ("wlan0".equals(iF.getName())) {
                              return mac;
                        }
                  }
            } catch (SocketException e) {
                  return Build.UNKNOWN;
            }
            return Build.UNKNOWN;
      }
}
