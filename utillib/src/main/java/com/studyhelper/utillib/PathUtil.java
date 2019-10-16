package com.studyhelper.utillib;

import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Create on 2019/4/29.
 * @author jogern
 */
public final class PathUtil {

      private static final String USB_STR = "usb";

      private PathUtil() { }

      /**
       * 得到内部sd路径
       * @return
       */
      public static String getExternalPath() {
            //判断sd卡是否存在
            boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
            if (sdCardExist) {
                  return Environment.getExternalStorageDirectory().getAbsolutePath();
            }
            return null;
      }

      /**
       * @param storageManager
       * @return
       */
      public static String getTfPath(StorageManager storageManager) {
            List<String> list = getStoragePath(storageManager);
            if (list != null) {
                  list.remove(getExternalPath());
                  int size = list.size();
                  String path;
                  for (int i = 0; i < size; i++) {
                        path = list.get(i);
                        if (path.toLowerCase().contains(USB_STR)) {
                              list.remove(i);
                              i--;
                              size--;
                        }
                  }
                  if (list.size() > 0) {
                        return list.get(0);
                  }
            }
            return null;
      }

      public static String getUsbPath(StorageManager storageManager) {
            List<String> list = getStoragePath(storageManager);
            if (list != null) {
                  for (String path : list) {
                        if (!path.toLowerCase().contains(USB_STR)) {
                              continue;
                        }
                        return path;
                  }
            }
            return null;
      }

      public static List<String> getStoragePath(StorageManager storageManager) {
            List<String> pathArray = null;
            try {
                  if (storageManager == null) {
                        return null;
                  }
                  //得到StorageManager中的getVolumeList()方法的对象
                  final Method getVolumeList = storageManager.getClass().getMethod("getVolumeList");
                  //得到StorageVolume类的对象
                  final Class<?> storageValumeClazz = Class.forName("android.os.storage.StorageVolume");
                  //---------------------------------------------------------------------
                  //获得StorageVolume中的一些方法
                  final Method getPath = storageValumeClazz.getMethod("getPath");
                  //调用getVolumeList方法，参数为：“谁”中调用这个方法
                  final Object invokeVolumeList = getVolumeList.invoke(storageManager);
                  final int length = Array.getLength(invokeVolumeList);
                  for (int i = 0; i < length; i++) {
                        //得到StorageVolume对象
                        Object storageValume = Array.get(invokeVolumeList, i);
                        String path = (String) getPath.invoke(storageValume);
                        if (TextUtils.isEmpty(path)) {
                              continue;
                        }
                        if (pathArray == null) {
                              pathArray = new ArrayList<>();
                        }
                        pathArray.add(path);
                  }
            } catch (Exception e) {
                  e.printStackTrace();
            }
            return pathArray;
      }
}
