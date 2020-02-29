/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.graduation_project;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;

/*
    작성자 : 김평기
    작성일 : 2019-08-05
    제공 기능 : 모름
    관련 액티비티 : ARActivity
    외부 라이브러리 및 플러그인 :


    기능 설명 : ARCore 설치용. 출처는 구글 ARCore 공식 예제

    상세정보 단계 :
        1.
 */


public class DemoUtils {
  private static final String TAG = "SceneformDemoUtils";
  private static final double MIN_OPENGL_VERSION = 3.0;

  private DemoUtils() {}


  public static void displayError(
          final Context context, final String errorMsg, @Nullable final Throwable problem) {
    final String tag = context.getClass().getSimpleName();
    final String toastText;
    if (problem != null && problem.getMessage() != null) {
      Log.e(tag, errorMsg, problem);
      toastText = errorMsg + ": " + problem.getMessage();
    } else if (problem != null) {
      Log.e(tag, errorMsg, problem);
      toastText = errorMsg;
    } else {
      Log.e(tag, errorMsg);
      toastText = errorMsg;
    }

    new Handler(Looper.getMainLooper())
        .post(
            () -> {
              Toast toast = Toast.makeText(context, toastText, Toast.LENGTH_LONG);
              toast.setGravity(Gravity.CENTER, 0, 0);
              toast.show();
            });
  }


  public static Session createArSession(Activity activity, boolean installRequested)
      throws UnavailableException {
    Session session = null;
    // if we have the camera permission, create the session
    if (hasCameraPermission(activity)) {
      switch (ArCoreApk.getInstance().requestInstall(activity, !installRequested)) {
        case INSTALL_REQUESTED:
          return null;
        case INSTALLED:
          break;
      }
      session = new Session(activity);
      // IMPORTANT!!!  ArSceneView requires the `LATEST_CAMERA_IMAGE` non-blocking update mode.
      Config config = new Config(session);
      config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
      session.configure(config);
    }
    return session;
  }

  public static void requestCameraPermission(Activity activity, int requestCode) {
    ActivityCompat.requestPermissions(
        activity, new String[] {Manifest.permission.CAMERA}, requestCode);
  }

  public static boolean hasCameraPermission(Activity activity) {
    return ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
        == PackageManager.PERMISSION_GRANTED;
  }

  public static boolean shouldShowRequestPermissionRationale(Activity activity) {
    return ActivityCompat.shouldShowRequestPermissionRationale(
        activity, Manifest.permission.CAMERA);
  }

  public static void launchPermissionSettings(Activity activity) {
    Intent intent = new Intent();
    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
    activity.startActivity(intent);
  }

  public static void handleSessionException(
          Activity activity, UnavailableException sessionException) {

    String message;
    if (sessionException instanceof UnavailableArcoreNotInstalledException) {
      message = "Please install ARCore";
    } else if (sessionException instanceof UnavailableApkTooOldException) {
      message = "Please update ARCore";
    } else if (sessionException instanceof UnavailableSdkTooOldException) {
      message = "Please update this app";
    } else if (sessionException instanceof UnavailableDeviceNotCompatibleException) {
      message = "This device does not support AR";
    } else {
      message = "Failed to create AR session";
      Log.e(TAG, "Exception: " + sessionException);
    }
    Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
  }

  public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
    if (Build.VERSION.SDK_INT < VERSION_CODES.N) {
      Log.e(TAG, "Sceneform requires Android N or later");
      Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
      activity.finish();
      return false;
    }

    String openGlVersionString =
        ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
            .getDeviceConfigurationInfo()
            .getGlEsVersion();
    if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
      Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
      Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
          .show();
      activity.finish();
      return false;
    }
    return true;
  }
}
