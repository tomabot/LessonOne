package com.example.tomabot.lessonone;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class LessonOneActivity extends Activity {

    private static final String LESSONONE_ACTIVITY = "LessonOneActivity:";
    /** Hold a reference to our GLSurfaceView
     *  The GLSurfaceView manages OpenGL surfaces and draws into the Android
     *  view system. It also adds features making it ieasier to use OpenGL, including:
     *      - providing a dedicated render thread
     *      - supports continuous or on-demand rendering
     *      - takes care of screen setup for using EGL, the interface between
     *            openGL and the window system
     * */
    private GLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LESSONONE_ACTIVITY, "onCreate");

        mGLSurfaceView = new GLSurfaceView(this);

        // see if the system supports OpenGL ES 2.0
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        //if( supportsEs2 ) {
            mGLSurfaceView.setEGLContextClientVersion(2);

            // set the renderer to our demo renderer, defined below
            mGLSurfaceView.setRenderer(new LessonOneRenderer(getApplicationContext()));
        //} else {
            // oops ...system does not support OpenGL ES 2.0...
        //    return;
        //}

        setContentView(mGLSurfaceView);
    }

    @Override
    protected void onResume()
    {
        // the activity must call the GL surface view's onResume() on activity onResume()
        super.onResume();
        Log.d(LESSONONE_ACTIVITY, "onResume");

        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause()
    {
        // the activity must call the GL surface view's onPause() on activity onPause()
        super.onPause();
        Log.d(LESSONONE_ACTIVITY, "onPause");

        mGLSurfaceView.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lesson_one, menu);
        Log.d(LESSONONE_ACTIVITY, "onCreateOptionsMenu");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.d(LESSONONE_ACTIVITY, "onOptionsItemSelected");


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
