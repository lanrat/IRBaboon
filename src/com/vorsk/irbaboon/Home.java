package com.vorsk.irbaboon;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.facebook.samples.profilepicture.R;
import com.facebook.widget.ProfilePictureView;
//import com.hoho.android.usbserial.R;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;


public class Home extends Activity {
    
    SerialInputOutputManager mSerialIoManager;
    UsbManager mUsbManager;
    UsbSerialDriver mSerialDevice;
    TextView globalTextView;
    DictionaryOpenHelper db = new DictionaryOpenHelper(this);
    ProfilePictureView profilePic;
    
    byte array[] = new byte[512];
    int count = 0;
    
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
//        profilePic = new ProfilePictureView(this);
//        profilePic = (ProfilePictureView)findViewById(R.id.profilepic);
//        profilePic.setProfileId(null);
//        GridLayout layout = (GridLayout) findViewById(R.id.view);
//        layout.addView(profilePic);
        
        // Create USB Serial Manager
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        Button button1 = (Button)findViewById(R.id.button1);
        Button button2 = (Button)findViewById(R.id.button2);
        button1.setText("Clear Buffer");
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                array = new byte[512];
                count = 0;
            }
        });
        button2.setText("Write Signal");
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                writeSignal();
            }
        });
        Button button3 = (Button)findViewById(R.id.button3);
        button3.setText("Party Mode");
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ImageView img = (ImageView)findViewById(R.id.lightImage);
                Random rand = new Random();
                switch(rand.nextInt(7)){
	                case 0:
	                {
	                	img.setImageResource(R.drawable.moustache);
	                	break;
	                }
	                case 1:
	                {
	                	img.setImageResource(R.drawable.sombrero);
	                	break;
	                }
	                case 2:
	                {
	                	img.setImageResource(R.drawable.redsolo);
	                	break;
	                }
	                case 3:
	                {
	                	img.setImageResource(R.drawable.discoball);
	                	break;
	                }
	                case 4:
	                {
	                	img.setImageResource(R.drawable.politicalparty);
	                	break;
	                }
	                case 5:
	                {
	                	img.setImageResource(R.drawable.lanparty);
	                	break;
	                }
	                case 6:
	                {
	                	img.setImageResource(R.drawable.pantsparty);
	                	break;
	                }
	                default:
	                {
	                	
	                }
	             
                }
                	
            }
        });
        
     // connecting to usb device
        mSerialDevice = UsbSerialProber.acquire(mUsbManager);
        if (mSerialDevice == null) {
        	//Toast.makeText(this, "On Resume null", Toast.LENGTH_SHORT).show();
        } else {
        	//Toast.makeText(this, "On Resume not null before read", Toast.LENGTH_SHORT).show();
        	try {
        		mSerialDevice.open();
        		mSerialDevice.setBaudRate(57600);
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        	
        }
        String byteString = "";
        for(int i = 0; i < array.length; i++)
        {
        	byteString += Integer.toBinaryString((int)array[i]);
        }
        TextView view = (TextView) findViewById(R.id.textview);
        view.setMovementMethod(new ScrollingMovementMethod());
        view.setText(byteString);
        
        
     }
    
    public void onResume(){
    	super.onResume();
        onDeviceStateChange();
    }

	public void writeSignal(){
    	
//		SQLiteDatabase read = db.getReadableDatabase();
//		Cursor cursor = read.query("dictionary", new String[]{"byteArray"}, null, null, null, null, null);
//		array = cursor.getBlob(1);
		String byteString = "Writing:\n";
        for(int i = 0; i < array.length; i++)
        {
        	byteString += Integer.toBinaryString((int)array[i]);
        }
        TextView view = (TextView) findViewById(R.id.textview);
        //view.setText(byteString);
            try {
    			mSerialDevice.write(array, 1000);
    			//Toast.makeText(this, "On Resume after write", Toast.LENGTH_SHORT).show();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			
    		}
            	
            
        
	}
	
//	public void wait2seconds(){
//		
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Toast.makeText(this, "Timer done!", Toast.LENGTH_SHORT).show();
//	}
    
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
    	
    	String message = "";
    	
    	for (int i = 0; i < data.length; i++)
    	{
    		if(count < 512)
    		{
    			array[count] = data[i];
    			
    		}
    		else if(count == 512)
    		{
//    			SQLiteDatabase write = db.getWritableDatabase();
    			for (int j = 0; j < array.length; j++)
    				message += Integer.toBinaryString((int)array[j]);
//    			ContentValues value = new ContentValues(1);
//    			value.put("1", array);
//    			write.insert("dictionary", "byteArray", value);
//    			globalTextView = (TextView)findViewById(R.id.textview);
//    	    	globalTextView.append(message);
    		}
    		count++;
    		
    	}
    	
		globalTextView = (TextView)findViewById(R.id.textview);
    	globalTextView.setText(message);
//    	if(startTheTimer) {
//    		wait2seconds();
//    		startTheTimer = false;
//    	}
//    	message +="\n";
//    	
    	
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_home, menu);
        return true;
    }
    
    
    public class DictionaryOpenHelper extends SQLiteOpenHelper {

        private static final int DATABASE_VERSION = 2;
        private static final String DICTIONARY_TABLE_NAME = "dictionary";
        private static final String DICTIONARY_TABLE_CREATE =
                    "CREATE TABLE " + DICTIONARY_TABLE_NAME + " (" +
                    "byteArray" + " varchar(512)" + ");";

        DictionaryOpenHelper(Context context) {
            super(context, "irbaboon", null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DICTIONARY_TABLE_CREATE);
        }

		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}
    }
}




