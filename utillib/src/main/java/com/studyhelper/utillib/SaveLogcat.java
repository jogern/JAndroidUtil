package com.studyhelper.utillib;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Create on 2019/4/1.
 * @author JogernHome
 */
public class SaveLogcat {

      private static final int M_START = 0x10;
      private static final int M_END   = 0x11;

      private static SaveLogcat sSaveLogcat;

      public static SaveLogcat getInstance() {
            if (sSaveLogcat == null) {
                  sSaveLogcat = new SaveLogcat();
            }
            return sSaveLogcat;
      }

      private String  mPath;
      private Handler mHandler = new Handler(Looper.getMainLooper()) {
            SaveRun mSaveRun;

            @Override
            public void handleMessage(Message msg) {
                  int what = msg.what;
                  if (what == M_START) {
                        if (mSaveRun != null && mSaveRun.isRunning) {
                              return;
                        }
                        mSaveRun = new SaveRun();
                        ThreadPoolUtil.executeThread(mSaveRun);
                  } else if (what == M_END) {
                        if (mSaveRun != null) {
                              mSaveRun.isRunning = false;
                              mSaveRun = null;
                        }
                  }
            }
      };


      private SaveLogcat() { }

      public void init(Context context) {
            File cacheDir = context.getExternalCacheDir();
            if (cacheDir != null) {
                  mPath = cacheDir.getAbsolutePath();
            }
      }

      public void startSave() {
            mHandler.obtainMessage(M_START).sendToTarget();
      }

      public void endSave() {
            mHandler.obtainMessage(M_END).sendToTarget();
      }


      private class SaveRun implements Runnable {

            boolean isRunning;

            @Override
            public void run() {
                  isRunning = true;
                  //日志等级：*:v , *:d , *:i , *:w , *:e , *:f , *:s
                  String cmds = "logcat *:f *:s *:e *:w *:i | grep \"(" + android.os.Process.myPid() + ")\"";
                  Process process = null;
                  while (isRunning) {
                        if (process == null) {
                              try {
                                    process = Runtime.getRuntime().exec(cmds);
                              } catch (IOException e) {
                                    e.printStackTrace();
                                    process = null;
                              }
                        }

                        if (process == null) {
                              SystemClock.sleep(500);
                              continue;
                        }
                        ExecSave(process);
                  }
                  if (process != null) {
                        process.destroy();
                  }
                  isRunning = false;
            }

            private void ExecSave(Process process) {
                  BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                  File file = null;
                  FileOutputStream out = null;
                  while (isRunning) {
                        if (TextUtils.isEmpty(mPath)){
                              SystemClock.sleep(5000);
                              continue;
                        }
                        try {
                              if (out == null) {
                                    file = new File(mPath, "Logcat-" + System.currentTimeMillis() + ".log");
                                    out = new FileOutputStream(file);
                                    deleteFile(file.getParentFile());
                              }
                              String line = reader.readLine();
                              if (!TextUtils.isEmpty(line)) {
                                    out.write((line.concat("\n")).getBytes());
                                    out.flush();
                              }

                              long length = file.length();
                              if (length > 5L * 1024 * 1024) {
                                    out.close();
                                    out = null;
                              }
                        } catch (IOException e) {
                              e.printStackTrace();
                        }
                  }
                  if (out != null) {
                        try {
                              out.close();
                        } catch (IOException e) {
                              e.printStackTrace();
                        }
                  }
                  try {
                        reader.close();
                  } catch (IOException e) {
                        e.printStackTrace();
                  }
            }

            private void deleteFile(File file) {
                  File[] files = file.listFiles();
                  if (files != null) {
                        List<File> fileList = new ArrayList<>();
                        for (File f : files) {
                              if (f.isDirectory()) {
                                    continue;
                              }
                              if (f.getName().startsWith("Logcat")) {
                                    fileList.add(f);
                              }
                        }
                        if (fileList.size() > 2) {
                              Collections.sort(fileList, new Comparator<File>() {
                                    @Override
                                    public int compare(File o1, File o2) {
                                          long x = o1.lastModified();
                                          long y = o2.lastModified();
                                          return (x < y) ? -1 : ((x == y) ? 0 : 1);
                                    }
                              });
                              while (fileList.size() > 2) {
                                    fileList.get(0).delete();
                                    fileList.remove(0);
                              }
                        }
                  }
            }
      }

}