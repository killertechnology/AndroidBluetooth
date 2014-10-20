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
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
    
    private Runnable R1 = new Runnable() {
    	   @Override
    	   public void run() {
    	      /* do what you need to do */
    	     
    	      /* and here comes the "trick" */
    		   	handler.postDelayed(this, 6000);
    	      	String _thisURL = "http://staging.vwffweeklypicks.com/media/weeklypick/images/knockout.png";
    	      	WebView webView = (WebView) findViewById(R.id.webView1);
		        webView.setBackgroundColor(0x00000000);
		        webView.setWebViewClient(new WebViewClient());
		        webView.loadUrl(_thisURL);
    	      
    	        
    	   }
    	};
    
    @SuppressLint("SetJavaScriptEnabled")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity2);
        
        handler.postDelayed(R1, 6000);
        
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

		
        myLabel = (TextView)findViewById(R.id.textView1);
        myLabel.setText("starting bluetooth interface.");
        Log.d("ACTIVITY 2", "starting bluetooth interface.");
        
        WebView webView = (WebView) findViewById(R.id.webView1);
        webView.setBackgroundColor(0x00000000);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://staging.vwffweeklypicks.com/media/weeklypick/images/volunteers.jpg");
        
        
        
        
        try 
        {
        	
            findBT();
            openBT();  
            beginListenForData();	
            
        }
        catch (IOException ex) { }
        
       
        
        
    }
    

	void findBT() throws IOException
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            myLabel.setText("No bluetooth adapter available");
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
                if(device.getName().equals("Adafruit EZ-Link 353e")) 
                {
                	myLabel.setText("Adafruit Bluetooth Device Found");
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
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();
        
    }
    
   
    void beginListenForData() throws IOException
    {
    	
        final Handler handler = new Handler(); 
        final byte delimiter = 47; //This is the ASCII code for a "/" character
        
        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[256];
        workerThread = new Thread(new Runnable()
        {
        	
            public void run()
            {
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
                                    temp = new String(encodedBytes, "US-ASCII") + " - " + _finalCount;
                                    _finalCount++;
                                    readBufferPosition = 0;
                                    
                                    handler.post(new Runnable()
                                    {
                                        @SuppressWarnings("deprecation")
										public void run()
                                        {
                                            myLabel.setText(temp);
                                        	Log.d("ACTIVITY2",   "serial input: " + temp);
                                    
                                            if (temp.contains("plays a video")){
                                        		
                                            	Intent intent = new Intent(Activity2.this, Activity3.class);                                            	
                                            	intent.putExtra("videoURL","http://staging.vwffweeklypicks.com/media/weeklypick/images/country_life_butter.mp4");
                                        		//String urlpath = "android.resource://"+ getPackageName() + "/" + R.raw.country_life_butter;
                                                //intent.putExtra("EXTRA_URL_PATH", urlpath);
                                        	 	startActivity(intent);
                                            }
                                        	else if (temp.contains("a message")){
                                        		
                                        		Intent intent = new Intent(Activity2.this, Activity3.class);                                            	
                                            	intent.putExtra("videoURL","http://staging.vwffweeklypicks.com/media/weeklypick/images/baileys_5sec.mp4");
                                        	 	startActivity(intent);
                                        	}
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
    
   
    
}


