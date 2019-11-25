package com.jogern.jutil.ui;

import android.os.Bundle;
import android.view.View;

import com.jogern.jutil.R;
import com.studyhelper.utillib.ApkUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Create on 2018/5/4.
 * @author jogern
 */
public class MainActivity extends AppCompatActivity {

      @Override
      protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
      }

      private void testMethod(String msg){
            System.out.println("test method"+msg);
      }


      public void onClickTest(View view) {
            Method testMethod = ApkUtil.findMethod(getClass(), "testMethod", 1);
            if (testMethod!=null){
                  try {
                        testMethod.invoke(this,"今天你好吗?");
                  } catch (IllegalAccessException e) {
                        e.printStackTrace();
                  } catch (InvocationTargetException e) {
                        e.printStackTrace();
                  }
            }

      }
}
