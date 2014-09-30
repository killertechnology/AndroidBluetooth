package com.example.compliancekiosk;

import com.example.compliancekiosk.util.SystemUiHider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;



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
   
    //private SystemUiHider mSystemUiHider;  
   

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_activity2);
        
		
		
		
        Button button1 = (Button)findViewById(R.id.button1);
        button1.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Activity2.this.finish();
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				android.os.Process.killProcess(android.os.Process.myPid());
				
			}
        	
        	
        });
        
    }
}
