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
    	// connecting to usb device
        mSerialDevice = UsbSerialProber.acquire(mUsbManager);
        if (mSerialDevice == null) {
        } else {
            try {
                mSerialDevice.open();
            } catch (IOException e) {
                Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
                try {
                    mSerialDevice.close();
                } catch (IOException e2) {
                    // Ignore.
                }
                mSerialDevice = null;
                return;
            }
        }
        onDeviceStateChange();
    	
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