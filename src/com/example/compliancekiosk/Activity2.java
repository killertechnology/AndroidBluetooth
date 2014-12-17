package com.example.compliancekiosk;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;



@SuppressLint("NewApi")
public class Activity2 extends Activity {
	
	protected TextView txtTextView1;
    BluetoothAdapter mBluetoothAdapter;
    public BluetoothSocket mmSocket;
    public BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    Thread secondWorkerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker1=false;
    volatile boolean stopWorker2=true;
    protected String serialReader;
    int _finalCount = 0;
    byte[] encodedBytes = new byte[0];
    byte[] packetBytes = new byte[0];
   
    protected Handler handler = new Handler();
    protected String[] schoolbag = new String[10];
    protected int _thisIteration = 0;
    protected static int _isVidPlaying = 0;
    protected static String _sessionRFID;
    protected static String _sessionID;
    protected String videoURL;
    public String incomingRFID;
    public String _thisRFID;
    public TimerTask timerTask;
	public int _waterDuration=0;
	private WebView webView;
	private VideoView videoViewVid1;
	private MediaController mc1;
	public Uri video;
	protected Button button1;
	protected RelativeLayout.LayoutParams videoViewLayoutParams = new RelativeLayout.LayoutParams(1080,880);
	protected RelativeLayout.LayoutParams webViewLayoutParams = new RelativeLayout.LayoutParams(1250,800);
	protected boolean resetInitialized = false;

		@Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_activity2);
	        
	        //reset the Flag view state
	        resetFlagState();
	        
	        //Initialize the shit
	        initializeApp();
	        
	        //Turn on the ad switcher
	        handler.postDelayed(AdSwitcher, 2000); //3000
	        handler.postDelayed(AutomaticRestart, 2000); //3000
        	
	        //c'mon
			GoGetBluetooth();
			
	    }
	 
	 
	private void initializeApp() {
		
		txtTextView1 = (TextView)findViewById(R.id.textView2);
        webView = (WebView) findViewById(R.id.webView1);
        videoViewVid1 = (VideoView) findViewById(R.id.videoView1);
        button1 = (Button) findViewById(R.id.button1);
  
        //reset the video display and media controller for video - set off stage by default
        videoViewVid1.setLayoutParams(videoViewLayoutParams);
        webView.setLayoutParams(webViewLayoutParams);
        toggleVideoMode(false);
    
        // add elements to the array
    	schoolbag[0] = "http://www.popordrop.com/media/images/knockout.png";
    	schoolbag[1] = "http://www.popordrop.com/media/images/good_hygiene_poster.jpg";
    	schoolbag[2] = "http://www.popordrop.com/media/images/handwashingsteps.jpg";
    	schoolbag[3] = "http://www.popordrop.com/media/images/handwashingday.png";
    	
        //Setup the web view for showing ads
        //webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        //webView.setBackgroundColor(0x00000000);
        
        mc1 = new MediaController(this);
        mc1.setVisibility(View.GONE);
		mc1.setAnchorView(videoViewVid1);
        
		button1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.d("ACTIVITY 3", "BUTTON CLICKED!");
				doRestart(Activity2.this);
			}
		});
		
		videoViewVid1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { 
			public  void  onCompletion(MediaPlayer mc1) { 
	    		//Reset the session status and stage elements to ad mode
	    		Log.d("ACTIVITY 3", "Video finished! - returning to activity 2");
	    		Activity2._sessionRFID=null;
	    		Activity2._isVidPlaying = 0;
	    		toggleVideoMode(false);
	    		videoViewVid1.clearAnimation();
	    		videoViewVid1.clearFocus();
//	    		videoViewVid1 = (VideoView) findViewById(R.id.videoView1);
	          }
	    });
	}

	private Runnable AdSwitcher = new Runnable() {
    	
    	   public void run() {
    			
        		String _showInterstitial = schoolbag[_thisIteration].toString();
        		if (_thisIteration>=3){ _thisIteration = -1;  }
        		_thisIteration++;
        		
    	        if (_isVidPlaying == 0){
    	        	Log.d("ACTIVITY 2", "handler refreshed- updating web interface.");
    	        	//webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        	        webView.loadUrl(_showInterstitial);
        	        
    	        }
    	        
    	        handler.postDelayed(this, 10000);
    	        
    	        /*
    	        else{
    	        	Log.d("ACTIVITY 2", "Extending web interface delay to 35 seconds.");
    	        	handler.postDelayed(this, 35000);
    	        }
    	        */
	   }
	};
	
	private Runnable AutomaticRestart = new Runnable() {
		//reset the program every 60 minutes in case android fails/restarts
		@SuppressWarnings("static-access")
		public void run() {
			if (resetInitialized ){
				doRestart(Activity2.this);
				Log.d("ACTIVITY 3", "RESTARTING NOW!");
			}
			else{
				resetInitialized = true;
				//handler.postDelayed(this, 3600000);
				handler.postDelayed(this, 900000);
				Date cDate = new Date(System.currentTimeMillis());
				android.text.format.DateFormat df = new android.text.format.DateFormat();
				
				Log.d("ACTIVITY 3", "RESTART SCHEDULED! \n15 min. from " + cDate.toString());
				txtTextView1.setText(txtTextView1.getText()+ "\nRESTART SCHEDULED! \n15 min. from\n" + df.format("yyyy-MM-dd kk:mm", cDate).toString()); // + "--"
			}
		}
	};
	
    private void GoGetBluetooth() {
    	try 
        {
        	//Setup the text view that will display status
            txtTextView1.setText("starting bluetooth interface.");
            Log.d("ACTIVITY 2", "starting bluetooth interface.");
            
        	findBT();
            openBT();  
            beginListenForData();	
            
        }
        catch (IOException ex) { }
        finally {
        	
        }
	}

    public void startWaterTimer(View view) {
  	  final Handler handler = new Handler();
  	  Timer ourtimer = new Timer();
  	  timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                  	  txtTextView1 = (TextView)findViewById(R.id.textView2);
                  	  //myLabel.setText(n + "Seconds");
                  	  _waterDuration++;
                    }
               });
            }};

  	      ourtimer.schedule(timerTask, 0, 1000);

  	 }
	public void resetFlagState() {
    	this.getWindow().setFlags(
        		//WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
        		WindowManager.LayoutParams.FLAG_FULLSCREEN | 
        		//WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
        		WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
        		WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
        		//WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
        		WindowManager.LayoutParams.FLAG_FULLSCREEN |
        		//WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | 
        		WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
        		WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    	
	}

	void beginListenForData() throws IOException
    {
    	Log.d("ACTIVITY 2", "Begin listening for data.");
        
        //47 is the ASCII code for "/" character- The signal that message transmission is complete.
        final byte delimiter = 47; 
        final Handler handler = new Handler();
        readBufferPosition = 0;
        readBuffer = new byte[256];
        
        workerThread = new Thread(new Runnable()
        {
			public void navigateToVideo() throws IOException
            {
        		Activity2._isVidPlaying = 1;
        		toggleVideoMode(true);
        		Uri video = Uri.parse("android.resource://com.example.compliancekiosk/raw/handwashingpart3");
				
				//comment out to turn off controls
				videoViewVid1.setMediaController(mc1);
				videoViewVid1.setVideoURI(video);
				videoViewVid1.start();
            }
        	
        	//videoURL = "http://www.popordrop.com/insert.php?rfid_id=" + incomingRFID;
            public void run()
            {
            	txtTextView1.setText("Listening for data.");
            	Log.d("ACTIVITY 2", "Listening for data.");
            	ReadIncomingBytestream();
            }

			protected void ReadIncomingBytestream() {
				try 
                {
					while(!Thread.currentThread().isInterrupted() && !stopWorker1)
					{
	                    	//Check for incoming data stream
	                    	int bytesAvailable = mmInputStream.available();                        
	                        if(bytesAvailable > 0)
	                        {
	                        	packetBytes = new byte[bytesAvailable];
	                            mmInputStream.read(packetBytes);
	                            //Log.d("ACTIVITY 2", "STILL GOING!!!!!!!");
	                            byte b = 47;
	                            for(int i=0;i<bytesAvailable;i++)
	                            {
	                            	if (i<packetBytes.length){
		                            	try{
			                                b = packetBytes[i];
		                            	}
		                            	finally{
		                            		//b = 0;
		                            	}
		                            	
		                                if(b != delimiter){
		                                	readBuffer[readBufferPosition++] = b;
		                                }
		                                else
		                                {
		                                	encodedBytes = new byte[readBufferPosition];
		                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
		                                    serialReader = new String(encodedBytes, "US-ASCII"); //+ " - " + _finalCount
		                                    _finalCount++;
		                                    readBufferPosition = 0;
		                                    
		                                    ReadMessage();
		                                   
		                                }
	                            	}
	                            }
	                        }
					}
                } 
                catch (IOException ex) 
                {
                	ex.printStackTrace();
                }
				
			}

			private void ReadMessage() {
				
				handler.post(new Runnable()
                {
					@SuppressLint("NewApi")
					public void run()
                    {
                        Log.d("ACTIVITY 2", "MESSAGE RECEIVED: " + serialReader);
                        try {
                        	Log.d("ACTIVITY 2", "STILL GOING!!!!!!!" + serialReader + "\n\n");
	                        if (serialReader.contains("plays a video")){
								
								if (Activity2._isVidPlaying == 0){
									
									incomingRFID = serialReader.replace("plays a video\n\r\nRFID FOUND|", "").replace("\n","");
									incomingRFID = incomingRFID.replace("\r", "");
									Log.d("ACTIVITY 2", "1 EXTRACTING NEW RFID:-->" + incomingRFID + "!");
	
									if (incomingRFID!=null){
										Activity2._sessionRFID = incomingRFID;
										Log.d("ACTIVITY 2", "2 OPENING VIDEO USING RFID:");
										txtTextView1.setText(txtTextView1.getText()+ "\n\n*****NEW SESSION ****\n->" + incomingRFID + "\n"); // + "--"
										navigateToVideo();
										serialReader=""; 
									}
								}
								else{
									txtTextView1.setText(txtTextView1.getText()+ "\nIGNORING " + incomingRFID); // + "--"
								}
								
	                        }
	                    	else if (serialReader.contains("Pedal")){
	                    		
	                    		txtTextView1.setText(txtTextView1.getText() + serialReader); // + "--"
								String waterURL = "http://www.popordrop.com/insert.php?session=" + Activity2._sessionID  + "&type=water&duration=" + _waterDuration;
								
								if (serialReader.contains("Depressed")){
									
									//Start the depression timer
									startWaterTimer(txtTextView1);
									GetWaterData obj = new GetWaterData();
	                        		obj.execute(waterURL);
	                        		
	                        		if (Activity2._isVidPlaying == 0){
	                            		
	                        			Activity2._isVidPlaying = 1;
	                        			navigateToVideo();
	                            		
	                        		}
	                        		
								}
								else{
									txtTextView1.setText(txtTextView1.getText() + "\nDURATION:" + (_waterDuration+1) + " SECONDS"); // + "--"
									timerTask.cancel();
									timerTask=null;
									_waterDuration=0;
								}
	                    	}
	                        serialReader = "";
                        
                        } catch (IOException e) {
							e.printStackTrace();
						} 
                    }

                });
				
			}
        });

        workerThread.start();
       
    }
    
    void sendData() throws IOException
    {
    	/*
        String msg = myTextbox.getText().toString();
        msg += "\n";
        mmOutputStream.write(msg.getBytes());
        txtTextView1.setText("Data Sent");
        */
    }
    
    void closeBT() throws IOException
    {
    	readBufferPosition = 0;
    	readBuffer = new byte[256];
    	packetBytes = null;
    	encodedBytes = null;
        mBluetoothAdapter = null;
        serialReader="";
        initializeApp();
        txtTextView1.setText("Bluetooth Closed");
    }

	void findBT() throws IOException
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            txtTextView1.setText("No bluetooth adapter available");
            Log.d("ACTIVITY 2", "No bluetooth adapter available");
        }
        
        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }
        
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
        	txtTextView1.setText("Bluetooth Device Found");
        	
            for(BluetoothDevice device : pairedDevices)
            {
            	//contains("Adafruit EZ-Link 353e")
                if((device.getName().contains("Adafruit")) || (device.getName().contains("HC-"))) 
                {
                	txtTextView1.setText("Bluetooth Device Connected!");
                    mmDevice = device;
                    break;
                }
            }
        }
    }
    
    void openBT() throws IOException
    {
    	UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);        
        mmSocket.connect();
        txtTextView1.setText("Bluetooth Opened");
        Log.d("ACTIVITY 2", "Bluetooth Opened");
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();
        
        
        
    }
    
   
    public class GetWaterData extends AsyncTask<String, Void, String>
	{
	    public GetWaterData()
	    {
	        //Constructor may be parametric 
	    }
	   

	    @Override
	    protected String doInBackground(String... params) 
	    {
	        
	        Log.d("ACTIVITY 2", "!!!SENDING WATER TRACKING SIGNAL!!!");
	        /*
	        try {
	        	BufferedReader reader = null;
	        	String data = null;
	    		
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
	         catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	        finally{
	        	
	        }
			*/
	        return null;
	    }
	    
	    @Override
	    protected void onPostExecute(String result)
	    {
	    	super.onPostExecute(result);
	    	Log.d("ACTIVITY 2", "SENDING WATER TRACKING SIGNAL MESSAGE");
	    }
	}

    public static void doRestart(Context c) {
        try {
            //check if the context is given
            if (c != null) {
                //fetch the packagemanager so we can get the default launch activity 
                // (you can replace this intent with any other activity if you want
                PackageManager pm = c.getPackageManager();
                //check if we got the PackageManager
                if (pm != null) {
                    //create the intent with the default start activity for your application
                    Intent mStartActivity = pm.getLaunchIntentForPackage(c.getPackageName());
                    if (mStartActivity != null) {
                        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //create a pending intent so the application is restarted after System.exit(0) was called. 
                        // We use an AlarmManager to call this intent in 100ms
                        int mPendingIntentId = 223344;
                        PendingIntent mPendingIntent = PendingIntent.getActivity(c, mPendingIntentId, mStartActivity,PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        //kill the application
                        System.exit(0);
                    } else {
                        Log.e("TAG", "Was not able to restart application, mStartActivity null");
                    }
                } else {
                    Log.e("TAG", "Was not able to restart application, PM null");
                }
            } else {
                Log.e("TAG", "Was not able to restart application, Context null");
            }
        } catch (Exception ex) {
            Log.e("TAG", "Was not able to restart application");
        }
    }

	protected void toggleVideoMode(boolean videoOn) {
		if (videoOn){
	        webView.setX(5000);
	        videoViewVid1.setX(200);
		}
		else
		{
			//Reposition the video back on stage
			webView.setX(200);
			videoViewVid1.setX(5000);
		}
	}
}













