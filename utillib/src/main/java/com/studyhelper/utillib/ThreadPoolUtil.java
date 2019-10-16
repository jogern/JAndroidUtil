package com.studyhelper.utillib;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Create on 2018/5/21.
 * @author jogern
 */

public class ThreadPoolUtil {

      private ThreadPoolUtil() {}

      private static ThreadPoolExecutor sCachePool;

      /**
       * 线程池
       * @return
       */
      public static ExecutorService getCachePool() {
            if (sCachePool == null) {
                  ThreadFactory factory = Executors.defaultThreadFactory();
                  sCachePool = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), factory);
            }
            return sCachePool;
      }

      /**
       * 执行线程
       * @param runnable
       */
      public static void executeThread(Runnable runnable) {
            getCachePool().execute(runnable);
      }

      /**
       * 创建线程池
       * @param thread 线程数
       * @return
       */
      public static ExecutorService newThread(int thread) {
            if (thread <= 1) {
                  return newSingleThread();
            }
            ThreadFactory factory = Executors.defaultThreadFactory();
            return new ThreadPoolExecutor(thread, thread, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), factory);
      }

      /**
       * 创建单线程
       * @return
       */
      public static ExecutorService newSingleThread() {
            return Executors.newSingleThreadExecutor();
      }

}
