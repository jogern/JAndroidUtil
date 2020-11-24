package com.studyhelper.utillib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * -------------------------------------------------------------------------
 * <br/>本类主要用途描述：<br/>
 * @author 作者：ZujianLiang
 * @version 版本号：v1.0
 * @date 创建时间： 2020/9/22 13:37
 * -------------------------------------------------------------------------
 */
public final class FileUtil {

      private FileUtil() { }

      public static boolean saveBitmapToJpg(File dest, File src, int quality) {
            if (src == null || !src.exists()) {
                  return false;
            }
            Bitmap bitmap = null;
            try {
                  bitmap = BitmapFactory.decodeFile(src.getAbsolutePath());
                  if (bitmap != null) {
                        return saveBitmapToJpg(dest, bitmap, quality);
                  }
            } catch (Exception e) {
                  Logcat.e("\nsave bitmap failed：" + StringUtil.getStackMsg(e));
            } finally {
                  if (bitmap != null) {
                        bitmap.recycle();
                  }
            }
            return false;
      }

      public static boolean saveBitmapToJpg(File dest, Bitmap bitmap, int quality) {
            return saveBitmap(dest, bitmap, Bitmap.CompressFormat.JPEG, quality);
      }

      public static boolean saveBitmapToPng(File dest, Bitmap bitmap, int quality) {
            return saveBitmap(dest, bitmap, Bitmap.CompressFormat.PNG, quality);
      }

      public static boolean saveBitmap(File dest, Bitmap bitmap, Bitmap.CompressFormat format, int quality) {
            if (bitmap == null || bitmap.isRecycled()) {
                  return false;
            }
            OutputStream out = null;
            try {
                  out = new FileOutputStream(dest);
                  return bitmap.compress(format, quality, out);
            } catch (Exception e) {
                  Logcat.e("\nsave bitmap failed：" + StringUtil.getStackMsg(e));
            } finally {
                  CloseUtil.closeIo(out);
            }
            return false;
      }

      public static boolean save(File dest, byte[] bytes) {
            ByteArrayInputStream input = new ByteArrayInputStream(bytes);
            try {
                  return save(dest, input);
            } catch (Exception e) {
                  Logcat.e("\nsave file failed：" + StringUtil.getStackMsg(e));
            } finally {
                  CloseUtil.closeIo(input);
            }
            return false;
      }

      public static boolean save(File dest, InputStream input) {
            File parentFile = dest.getParentFile();
            if (!parentFile.exists()) {
                  parentFile.mkdirs();
            }
            File file = new File(parentFile, dest.getName() + ".cache");
            OutputStream out = null;
            try {
                  out = new FileOutputStream(file);
                  byte[] bytes = new byte[2048];
                  int len;
                  while ((len = input.read(bytes)) != -1) {
                        out.write(bytes, 0, len);
                  }
                  out.flush();
                  return rename(file, dest);
            } catch (Exception e) {
                  Logcat.e("\nsave file failed：" + StringUtil.getStackMsg(e));
            } finally {
                  CloseUtil.closeIo(out);
            }
            return false;
      }

      private static boolean rename(File cache, File dest) {
            if (dest.exists()) {
                  if (dest.delete()) {
                        return cache.renameTo(dest);
                  } else {
                        cache.delete();
                  }
            } else {
                  return cache.renameTo(dest);
            }
            return false;
      }

}
