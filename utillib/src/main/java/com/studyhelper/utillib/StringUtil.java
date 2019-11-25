package com.studyhelper.utillib;

/**
 * Create on 2019-07-15.
 * @author zujianliang
 */
public final class StringUtil {

      private StringUtil() { }

      public static boolean isEmpty(CharSequence text) {
            return text == null || text.length() <= 0;
      }

      /**
       * 单位毫秒
       * @param timeMs
       * @return
       */
      public static String timeMsFormat(int timeMs) {
            if (timeMs <= 0) {
                  return "00:00";
            }
            int totalSeconds = timeMs / 1000;
            int seconds = totalSeconds % 60;
            int minutes = totalSeconds / 60;
            return String.format("%02d:%02d", minutes, seconds);
      }

      /**
       * 单位毫秒
       * @param timeMs
       * @return
       */
      public static String timeMsFormatFull(int timeMs) {
            if (timeMs <= 0) {
                  return "00:00";
            }
            int totalSeconds = timeMs / 1000;
            int seconds = totalSeconds % 60;
            int minutes = totalSeconds / 60;
            return String.format("%02d:%02d:%02d", minutes / 60, minutes, seconds);
      }

}
