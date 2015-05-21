package com.example.compliancekiosk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;


public class Activity3 extends Activity {

	//TextView myLabel;
	String _myVideoURL;
	String _mySessionID;
	VideoView videoViewVid1;
	MediaController mc1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity3);
		
		//Activity2._isVidPlaying = 1;
		Log.d("ACTIVITY 3", "STARTING VIDEO USING RFID:" + Activity2._sessionRFID);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
      
	}
	
	protected void closeActivity() {
		// TODO Auto-generated method stub
		Activity2._isVidPlaying = 0;
		Activity2._sessionRFID=null;
		finish();
	}


	public class GetData extends AsyncTask<String, Void, String>
	{
	    public GetData()
	    {
	        //Constructor may be parametric 
	    }
	   
	    @Override
	    protected String doInBackground(String... params) 
	    {
	        BufferedReader reader = null;
	        String data = null;
	        Log.d("ACTIVITY 3", "SENDING TRACKING SIGNAL");
	        
	        try {
	        	if (Activity2._sessionRFID!=null){
		        	HttpClient client = new DefaultHttpClient();
					URI uri = new URI(params[0]);
					HttpGet get = new HttpGet(uri);
					HttpResponse response = client.execute(get);
					
					InputStream stream = response.getEntity().getContent();
					reader = new BufferedReader(new InputStreamReader(stream));
					StringBuffer buffer = new StringBuffer("");
					String line = "";
					String newLine = System.getProperty("line.separator");
					while((line = reader.readLine())!=null){
						buffer.append(line + newLine);
					}
					reader.close();
					data = buffer.toString();
		        	
					return data;
	        	}
	        	else{
	        		Log.d("ACTIVITY 3", "CANNOT TRACK - NO RFID FOUND!");
	        	}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("ACTIVITY 3", "SESSION KILLED IOException!");
				closeActivity();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("ACTIVITY 3", "SESSION KILLED URISyntaxException!\nRFID#: " + params[0] + "!");
				closeActivity();
			}
			
	         
	        return null;
	    }
	   

		@Override
	    protected void onPostExecute(String result)
	    {
	    	super.onPostExecute(result);
	    	if (result!=null){
	    	
		    	_myVideoURL = result.replace("\n", "");
		    	_myVideoURL = _myVideoURL.substring(0,(_myVideoURL.indexOf("|")));
		    	
		    	_mySessionID = result.replace(_myVideoURL, "");
		    	_mySessionID = _mySessionID.replace("|", "").replace("\n", "");
		    	if (_mySessionID!=null){
		    		Activity2._sessionID = _mySessionID;
		    		Log.d("ACTIVITY 3", "SESSION IDENTIFIED - " + _mySessionID + "!");
		    		Uri video = Uri.parse("android.resource://com.example.compliancekiosk/raw/handwashingpart2");
					
					
					//Uri video = Uri.parse(_myVideoURL);
					
					//comment out to turn off controls
					videoViewVid1.setMediaController(mc1);
					videoViewVid1.setVideoURI(video);
					videoViewVid1.start();
		    	}
		  	
	    	}
	    }
		
	}

}


