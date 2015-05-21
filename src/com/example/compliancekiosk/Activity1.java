package com.example.compliancekiosk;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import com.example.compliancekiosk.util.SystemUiHider;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class Activity1 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	Intent intent = new Intent(Activity1.this, Activity2.class);
  	 	startActivity(intent);
        
    	/*
    	

        //setContentView(R.layout.activity_activity1);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	        
        
        
        
       // final View controlsView = findViewById(R.id.fullscreen_content_controls);
        //final View contentView = findViewById(R.id.fullscreen_content);
 
        TextView txtTextView1 = (TextView)findViewById(R.id.fullscreen_content);
        txtTextView1.setText("TESTING");
        //DownloadFile("http://www.popordrop.com/media/images/knockout.png","testknockout.png");
       
        File file = new File(this.getFilesDir(), "myfiletest");
    	
        String filename = "myfile";
        String string = "Hello world!";
        FileOutputStream outputStream;

        try {
          outputStream = openFileOutput("myfiletest", Context.MODE_PRIVATE);
          outputStream.write(string.getBytes());
          outputStream.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
     */
    }

    
   
}
