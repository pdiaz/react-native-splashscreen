package com.remobile.splashscreen;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;


public class RCTSplashScreen extends ReactContextBaseJavaModule {
    private static Dialog splashDialog;
    private ImageView splashImageView;

    private Activity activity;

    public RCTSplashScreen(ReactApplicationContext reactContext, Activity activity) {
        super(reactContext);
        this.activity = activity;
        showSplashScreen();
    }

    @Override
    public String getName() {
        return "SplashScreen";
    }
    
    @ReactMethod
    public void hide() {
        if (splashDialog != null) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    removeSplashScreen();
                }
            }, 500);
        }
    }

    private void removeSplashScreen() {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                if (splashDialog != null && splashDialog.isShowing()) {
                    AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
                    fadeOut.setDuration(400);
                    View view = ((ViewGroup) splashDialog.getWindow().getDecorView()).getChildAt(0);
                    view.startAnimation(fadeOut);
                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            splashDialog.dismiss();
                            splashDialog = null;
                            splashImageView = null;
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                }
            }
        });
    }

    private int getSplashId() {
        Resources res = activity.getResources();
        int drawableId = res.getIdentifier("splash", "drawable", activity.getClass().getPackage().getName());
        if (drawableId == 0) {
            drawableId = res.getIdentifier("splash", "drawable", activity.getPackageName());
        }
        return drawableId;
    }

    private void showSplashScreen() {
        final int drawableId = getSplashId();
        if ((splashDialog != null && splashDialog.isShowing())||(drawableId == 0)) {
            return;
        }
        activity.runOnUiThread(new Runnable() {
            public void run() {
                // Get reference to display
                Context context = activity;
                Display display = activity.getWindowManager().getDefaultDisplay();

                // Use an ImageView to render the image because of its flexible scaling options.
                splashImageView = new ImageView(context);
                splashImageView.setImageResource(drawableId);
                LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                splashImageView.setLayoutParams(layoutParams);
                splashImageView.setMinimumHeight(display.getHeight());
                splashImageView.setMinimumWidth(display.getWidth());
                splashImageView.setBackgroundColor(Color.TRANSPARENT);
                splashImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                Window activityWindow = activity.getWindow();
                // Create and show the dialog
                splashDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
                Window splashWindow = splashDialog.getWindow();

                // check to see if the splash screen should be full screen
                if ((activityWindow.getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
                    splashWindow.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }

                if (Build.VERSION.SDK_INT >= 21) {
                    splashWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    splashWindow.setStatusBarColor(activityWindow.getStatusBarColor());
                }

                splashDialog.setContentView(splashImageView);

                splashDialog.setCancelable(false);
                splashDialog.show();
            }
        });
    }
}
