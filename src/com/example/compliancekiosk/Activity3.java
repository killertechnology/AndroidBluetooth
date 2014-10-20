package com.example.compliancekiosk;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;




public class Activity3 extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().setFlags(
				 WindowManager.LayoutParams.FLAG_FULLSCREEN | 
				 WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | 
				 WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | 
				 WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
	             WindowManager.LayoutParams.FLAG_FULLSCREEN | 
	             WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | 
	             WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | 
	             WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		
		 
		setContentView(R.layout.activity_activity3);
		 
		String strVideoURL = getIntent().getExtras().getString("videoURL");

		String urlpath = "";//"android.resource://"+ getPackageName() + "/" + R.raw.baileys_5sec;
		//String urlpath = "http://staging.vwffweeklypicks.com/media/weeklypick/images/baileys_5sec.mp4";
		urlpath = strVideoURL;
		final VideoView vid = (VideoView) findViewById(R.id.videoView1);
		MediaController mc = new MediaController(this);
		mc.setVisibility(View.GONE);
		mc.setAnchorView(vid);
		Uri video = Uri.parse(urlpath);
		//vid.setMediaController(mc);
		vid.setVideoURI(video);
      
 
	      
	    vid.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { 
	    	public  void  onCompletion(MediaPlayer mc) { 
	      	 	/*
	      		vid.setVisibility(0);
	      	 	Intent intent = new Intent(Activity3.this, Activity2.class);
	      	 	startActivity(intent);
	      	 	*/
	      		finish();
	          }
	      }); 
	      
	      vid.start();
		
	}

	

}
