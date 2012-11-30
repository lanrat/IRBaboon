package com.vorsk.irbaboon;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

//import com.hoho.android.usbserial.R;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;


public class Home extends Activity {
    
    SerialInputOutputManager mSerialIoManager;
    UsbManager mUsbManager;
    UsbSerialDriver mSerialDevice;
    TextView globalTextView;
    
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    
    private final String TAG = Home.class.getSimpleName();
    
    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

        @Override
        public void onRunError(Exception e) {
            Log.d(TAG, "Runner stopped.");
        }

        @Override
        public void onNewData(final byte[] data) {
            Home.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Home.this.updateReceivedData(data);
                }
            });
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        // Create USB Serial Manager
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

     }
    
    public void onResume(){
    	super.onResume();
    	byte array[] = new byte[512];
    	// connecting to usb device
    	Toast.makeText(this, "On Resume", Toast.LENGTH_SHORT).show();
        mSerialDevice = UsbSerialProber.acquire(mUsbManager);
        if (mSerialDevice == null) {
        	Toast.makeText(this, "On Resume null", Toast.LENGTH_SHORT).show();
        } else {
        	Toast.makeText(this, "On Resume not null before read", Toast.LENGTH_SHORT).show();
        	try {
        		mSerialDevice.open();
    			mSerialDevice.read(array, 10000);
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        	Toast.makeText(this, "On Resume not null after read", Toast.LENGTH_SHORT).show();
            String byteString = "";
            for (int i = 0; i < array.length; i++)
            {
            	byteString += Integer.toBinaryString((int)array[i]);
            }
            Toast.makeText(this, "On Resume before write", Toast.LENGTH_SHORT).show();
            globalTextView = (TextView)findViewById(R.id.textview);
            globalTextView.append(byteString);
            try {
    			mSerialDevice.write(array, 10000);
    			Toast.makeText(this, "On Resume after write", Toast.LENGTH_SHORT).show();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			
    		}

          
        }
        
        //onDeviceStateChange();
    	
    }
    
    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.i(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (mSerialDevice != null) {
            mSerialIoManager = new SerialInputOutputManager(mSerialDevice, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }
    
    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }
    
    private void updateReceivedData(byte[] data) {
    	
    	String message = "Read " + data.length + " bytes: \n";
    	    	
    	for (int i = 0; i < data.length; i++)
    	{
    		message += Integer.toHexString((int) data[i]) + " ";
    	}
    	message +="\n";
    	
    	Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    	globalTextView = (TextView)findViewById(R.id.textview);
    	globalTextView.append(message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_home, menu);
        return true;
    }

    
}