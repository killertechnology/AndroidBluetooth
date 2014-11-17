package com.example.compliancekiosk;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import com.example.compliancekiosk.util.SystemUiHider;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class Activity2 extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
   
	TextView myLabel;
	WebView wv;
    EditText myTextbox;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
    String temp = "";
    int _finalCount = 0;
    byte[] encodedBytes = new byte[0];
    byte[] packetBytes = new byte[0];
   
    private Handler handler = new Handler();
    
	// declare a string array with initial size
	public String[] schoolbag = new String[3];
	public int _thisIteration =-1;
	public int _isVidPlaying = 0;
	
    private Runnable R1 = new Runnable() {
    	   
    	
    	@Override
    	   public void run() {
    	   
    			_thisIteration++;
        		String _showInterstitial = schoolbag[_thisIteration].toString();
        			
    			if (_thisIteration>1){ _thisIteration = -1;  }
    		  	
    	        if (_isVidPlaying == 0){
    	        	Log.d("ACTIVITY 2", "return handler started- updating web interface.");
        		   	WebView webView = (WebView) findViewById(R.id.webView1);
        	        webView.loadUrl(_showInterstitial);
    	        	handler.postDelayed(this, 10000);
    	        }
    	        else{
    	        	Log.d("ACTIVITY 2", "Extending web interface delay to 35 seconds.");
    	        	_isVidPlaying =0;
    	        	handler.postDelayed(this, 35000);
    	        	
    	        }
    		
    		
    		      
	   }
	};
    public String videoURL = "http://staging.vwffweeklypicks.com/media/weeklypick/images/baileys_5sec.mp4";
    public String incomingRFID = "**";
	
	
    @SuppressLint("SetJavaScriptEnabled")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity2);
        

        	
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

        // add elements to the array
    	schoolbag[0] = "http://staging.vwffweeklypicks.com/media/weeklypick/images/knockout.png";
    	schoolbag[1] = "http://sarahjessicaparkerlookslikeahorse.com";
    	schoolbag[2] = "http://staging.vwffweeklypicks.com/media/weeklypick/images/volunteers.jpg";
    	
        myLabel = (TextView)findViewById(R.id.textView2);
        myLabel.setText("starting bluetooth interface.");
        Log.d("ACTIVITY 2", "starting bluetooth interface.");
        
        
        WebView webView = (WebView) findViewById(R.id.webView1);
        webView.setBackgroundColor(0x00000000);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
      

        
        try 
        {
        	handler.postDelayed(R1, 3000); //3000
        	Log.d("ACTIVITY 2", "handler started - waiting for messages.");
            findBT();
            openBT();  
            beginListenForData();	
            
            
        }
        catch (IOException ex) { }
        finally {
        	
        }
       
         
        
        
    }
    

    
    void beginListenForData() throws IOException
    {
    	Log.d("ACTIVITY2", "Begin listening for data.");
        final Handler handler = new Handler(); 
        
        //47 is the ASCII code for a "/" character 
        //This is the signal that message transmission is complete.
        final byte delimiter = 47; 
        
        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[256];
        
        workerThread = new Thread(new Runnable()
        {
        	String videoURL = "http://staging.vwffweeklypicks.com/media/weeklypick/images/country_life_butter.mp4"; 
    		
        	public void navigateToVideo(String videoURL) throws IOException
            {
        		_isVidPlaying = 1;
            	Intent intent = new Intent(Activity2.this, Activity3.class);                                            	
            	intent.putExtra("videoURL",videoURL);
        	 	startActivity(intent);
        	 	
            }
        	
        	
            public void run()
            {
            	myLabel.setText("Listening for data.");
            	Log.d("ACTIVITY2", "Listening for data.");
               while(!Thread.currentThread().isInterrupted() && !stopWorker)
               {
                    try 
                    {
                    	int bytesAvailable = mmInputStream.available();                        
                        if(bytesAvailable > 0)
                        {
                        	packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            //Log.d("ACTIVITY2", "Data found - " + bytesAvailable);
                            //myLabel.setText("Data found - " + bytesAvailable);
                            
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b != delimiter){
                                	readBuffer[readBufferPosition++] = b;
                                }
                                else
                                {
                                    encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    //Log.d("ACTIVITY2", "Setting Temp - " + i);
                                    temp = new String(encodedBytes, "US-ASCII"); //+ " - " + _finalCount
                                    _finalCount++;
                                    readBufferPosition = 0;
                                    
                                    handler.post(new Runnable()
                                    {
                                        @SuppressWarnings("deprecation")
										public void run()
                                        {
                                            Log.d("ACTIVITY2", "MESSAGE RECEIVED: " + temp);
                                        	
                                            if (temp.contains("plays a video")){
                                        		
                                            	try {
                                            		incomingRFID = temp.replace("\nplays a video\n", "").replace("RFID FOUND|", "");
                                            		
                                            		//videoURL = "http://staging.vwffweeklypicks.com/media/weeklypick/images/baileys_5sec.mp4";
                                            		videoURL = "http://www.popordrop.com/insert.php?rfid_id=" + incomingRFID;
                                            		
                                            		myLabel.setText(myLabel.getText()+ incomingRFID); // + "--"
                                                	navigateToVideo(videoURL);
                                            		
												} catch (IOException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
                                            }
                                        	else if (temp.contains("Pedal")){
                                    
                                        		try {
                                        			
                                        			myLabel.setText(myLabel.getText() + temp); // + "--"
                                        			videoURL = "http://staging.vwffweeklypicks.com/media/weeklypick/images/baileys_5sec.mp4";                                      	
                                            		//navigateToVideo(videoURL);
                                        			
												} finally {
													// TODO Auto-generated catch block
													//e.printStackTrace();
												}
                                        	}
                                        /*	*/
                                        }
                                    });
                                }
                            }
                            //readBufferPosition = 0;
                        }
                    } 
                    catch (IOException ex) 
                    {
                        stopWorker = true;
                    }
               }
            }
        });

        workerThread.start();
        
    }
    
   
    
    void sendData() throws IOException
    {
        String msg = myTextbox.getText().toString();
        msg += "\n";
        mmOutputStream.write(msg.getBytes());
        myLabel.setText("Data Sent");
    }
    
    void closeBT() throws IOException
    {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
        myLabel.setText("Bluetooth Closed");
    }
    

	void findBT() throws IOException
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            myLabel.setText("No bluetooth adapter available");
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
        	myLabel.setText("Bluetooth Device Found");
        	
            for(BluetoothDevice device : pairedDevices)
            {
            	//contains("Adafruit EZ-Link 353e")
                if((device.getName().contains("Adafruit")) || (device.getName().contains("HC-"))) 
                {
                	myLabel.setText("Bluetooth Device Connected!");
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
        myLabel.setText("Bluetooth Opened");
        Log.d("ACTIVITY 2", "Bluetooth Opened");
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();
        
    }
    
   
    
}













