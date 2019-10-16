package com.jogern.jutil.ui;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

/**
 * Create on 2019-08-23.
 * @author zujianliang
 */
public class LauncherActivity extends PermissionActivity {

      @Override
      protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            checkPermission();
      }

      @Override
      protected void startLauncherActivity() {
            startActivity(new Intent(this, MainActivity.class));
      }

}
