package com.example.compliancekiosk;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.example.compliancekiosk.util.SystemUiHider;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
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
	
	TextView txtTextView1;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    Thread secondWorkerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker1=false;
    volatile boolean stopWorker2=true;
    protected String serialReader = "";
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
    
    private Runnable R1 = new Runnable() {
    	
    	@SuppressLint("NewApi")
		@Override
    	   public void run() {
    			
        		String _showInterstitial = schoolbag[_thisIteration].toString();
        		if (_thisIteration>=3){ _thisIteration = -1;  }
        		_thisIteration++;
        		
    	        if (_isVidPlaying == 0){
    	        	Log.d("ACTIVITY 2", "handler refreshed- updating web interface.");
        		   	webView = (WebView) findViewById(R.id.webView1);
    	        	webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        	        webView.loadUrl(_showInterstitial);
    	        	handler.postDelayed(this, 10000);
    	        }
    	        /*
    	        else{
    	        	Log.d("ACTIVITY 2", "Extending web interface delay to 35 seconds.");
    	        	handler.postDelayed(this, 35000);
    	        }
    	        */
    		      
	   }
	};
	private WebView webView;
    
	public void startTimer(View view) {
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

	
 
    @SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity2);
        
        this.getWindow().setFlags(
        		WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
        		WindowManager.LayoutParams.FLAG_FULLSCREEN | 
        		WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
        		WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
        		WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
        		WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
        		WindowManager.LayoutParams.FLAG_FULLSCREEN |
        		WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | 
        		WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
        		WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        // add elements to the array
    	schoolbag[0] = "http://www.popordrop.com/media/images/knockout.png";
    	schoolbag[1] = "http://www.popordrop.com/media/images/good_hygiene_poster.jpg";
    	schoolbag[2] = "http://www.popordrop.com/media/images/handwashingsteps.jpg";
    	schoolbag[3] = "http://www.popordrop.com/media/images/handwashingday.png";
    	
        txtTextView1 = (TextView)findViewById(R.id.textView2);
        txtTextView1.setText("starting bluetooth interface.");
        Log.d("ACTIVITY 2", "starting bluetooth interface.");
        
        //mHandler = new Handler();
        //trackRFIDActivation.start();
        
        webView = (WebView) findViewById(R.id.webView1);
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webView.setBackgroundColor(0x00000000);
        //webView.getSettings().setJavaScriptEnabled(true);
        //webView.setWebViewClient(new WebViewClient());
        
        try 
        {
        	handler.postDelayed(R1, 2000); //3000
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
            	Intent intent = new Intent(Activity2.this, Activity3.class);                                            	
            	//intent.putExtra("videoURL",videoURL);
        	 	startActivity(intent);
        	 	
            }
        	
        	
            public void run()
            {
            	txtTextView1.setText("Listening for data.");
            	Log.d("ACTIVITY 2", "Listening for data.");
               while(!Thread.currentThread().isInterrupted() && !stopWorker1)
               {
                    try 
                    {
                    	
                    	int bytesAvailable = mmInputStream.available();                        
                        if(bytesAvailable > 0)
                        {
                        	packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            //Log.d("ACTIVITY 2", "Data found - " + bytesAvailable);
                            
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
                                    serialReader = new String(encodedBytes, "US-ASCII"); //+ " - " + _finalCount
                                    _finalCount++;
                                    readBufferPosition = 0;
                                    
                                    
                                    handler.post(new Runnable()
                                    {
										public void run()
                                        {
                                            Log.d("ACTIVITY 2", "MESSAGE RECEIVED: " + serialReader);
                                        	
                                            if (serialReader.contains("plays a video")){
                                        		
												try {
														if (Activity2._isVidPlaying == 0){
															
															incomingRFID = serialReader.replace("plays a video\n\r\nRFID FOUND|", "").replace("\n","");
															incomingRFID = incomingRFID.replace("\r", "");
															
															//incomingRFID = incomingRFID.substring(0,(incomingRFID.indexOf("\n")));
															Log.d("ACTIVITY 2", "1 EXTRACTING NEW RFID:-->" + incomingRFID + "!");

															if (incomingRFID!=null){
																Activity2._sessionRFID = incomingRFID;
																//videoURL = "http://www.popordrop.com/insert.php?rfid_id=" + incomingRFID;
																Log.d("ACTIVITY 2", "2 OPENING VIDEO USING RFID:-->" + videoURL + "->EOF");
																txtTextView1.setText(txtTextView1.getText()+ "\n*****NEW SESSION ****\n->" + incomingRFID + "\n"); // + "--"
																navigateToVideo();
															}
															else{
																txtTextView1.setText(txtTextView1.getText()+ "\nRFID REQUEST IGNORED ->" + incomingRFID); // + "--"
															}
														}
														else{
															txtTextView1.setText(txtTextView1.getText()+ "\nIGNORING " + incomingRFID); // + "--"
														}
												} catch (IOException e) {
													e.printStackTrace();
												} 
                                            }
                                        	else if (serialReader.contains("Pedal")){
                                        		
                                        		txtTextView1.setText(txtTextView1.getText() + serialReader); // + "--"
												String waterURL = "http://www.popordrop.com/insert.php?session=" + Activity2._sessionID  + "&type=water&duration=" + _waterDuration;
												
												if (serialReader.contains("Depressed")){
													startTimer(txtTextView1);
													GetWaterData obj = new GetWaterData();
	                                        		obj.execute(waterURL);
												}
												else{
													txtTextView1.setText(txtTextView1.getText() + "\nDURATION:" + (_waterDuration+1) + " SECONDS"); // + "--"
													timerTask.cancel();
													timerTask=null;
													_waterDuration=0;
												}
                                        	}
                                            serialReader = "";
                                        }
										
                                    });
                                }
                            }
                            //readBufferPosition = 0;
                        }
                    } 
                    catch (IOException ex) 
                    {
                    	ex.printStackTrace();
                        //stopWorker1 = true;
                    }
               }
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
        stopWorker1 = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
    
}













