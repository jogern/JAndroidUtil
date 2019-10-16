package com.jogern.jutil;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Create on 2018/12/28.
 * @author jogern
 */
public class JUtilApplication extends Application {

      @Override
      protected void attachBaseContext(Context base) {
            super.attachBaseContext(base);
            MultiDex.install(this);
      }


      @Override
      public void onCreate() {
            super.onCreate();

      }



}
