package com.studyhelper.utillib;

import android.database.Cursor;

/**
 * Create on 2019-07-29.
 * @author zujianliang
 */
public final class CursorUtil {

      private CursorUtil() { }

      public static int getInt(Cursor cursor, String columnName) {
            int index = cursor.getColumnIndex(columnName);
            if (index < 0) {
                  return 0;
            }
            return cursor.getInt(index);
      }

      public static  int getInt(Cursor cursor, String columnName,int defValue) {
            int index = cursor.getColumnIndex(columnName);
            if (index < 0) {
                  return defValue;
            }
            return cursor.getInt(index);
      }

      public static long getLong(Cursor cursor, String columnName) {
            int index = cursor.getColumnIndex(columnName);
            if (index < 0) {
                  return 0;
            }
            return cursor.getLong(index);
      }

      public static float getFloat(Cursor cursor, String columnName) {
            int index = cursor.getColumnIndex(columnName);
            if (index < 0) {
                  return 0;
            }
            return cursor.getFloat(index);
      }

      public static String getString(Cursor cursor, String columnName) {
            int index = cursor.getColumnIndex(columnName);
            if (index < 0) {
                  return null;
            }
            return cursor.getString(index);
      }


}
