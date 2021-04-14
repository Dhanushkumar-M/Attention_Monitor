package com.example.attentionmonitor;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.neurosky.connection.TgStreamReader;

/**
 * This activity is the man entry of this app. It demonstrates the usage of 
 * (1) TgStreamReader.redirectConsoleLogToDocumentFolder()
 * (2) TgStreamReader.stopConsoleLog()
 * (3) demo of getVersion
 */
public class DemoActivity extends Activity {
	private static final String TAG = com.example.attentionmonitor.DemoActivity.class.getSimpleName();

	private final Button btn_adapter = null;

	private TextView tv_filedemo = null;
	private TextView tv_adapter = null;
	private TextView tv_device = null;
	//private TextView  tv_uart = null;
	
	private Button btn_filedemo = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.main_view);

		BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
		bottomNavigationView.setSelectedItemId(R.id.navigation_home);

		bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@SuppressLint("NonConstantResourceId")
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				switch(item.getItemId()){
					case R.id.navigation_profile:
						startActivity(new Intent(getApplicationContext(),AccountPage.class));
						overridePendingTransition(0,0);
						return true;
					case R.id.navigation_home:
						return true;
					case R.id.navigation_About:
						startActivity(new Intent(getApplicationContext(), AboutPage.class));
						overridePendingTransition(0,0);
						return true;
				}
				return false;
			}
		});

		initView();
		// (1) Example of redirectConsoleLogToDocumentFolder()
		// Call redirectConsoleLogToDocumentFolder at the beginning of the app, it will record all the log.
		// Don't forget to call stopConsoleLog() in onDestroy() if it is the end point of this app.
		// If you can't find the end point of the app , you don't have to call stopConsoleLog()
		TgStreamReader.redirectConsoleLogToDocumentFolder();
		// (3) demo of getVersion
		Log.d(TAG,"lib version: " + TgStreamReader.getVersion());
	}
	private Button btn_device = null;
	//private Button btn_uart = null;

	private void initView() {
		tv_filedemo = findViewById(R.id.tv_filedemo);
		tv_adapter = findViewById(R.id.tv_adapter);
		tv_device = findViewById(R.id.tv_device);
		//tv_uart = (TextView) findViewById(R.id.tv_uart);

		btn_filedemo = findViewById(R.id.btn_filedemo);
		//btn_adapter = (Button) findViewById(R.id.btn_adapter);
		btn_device = findViewById(R.id.btn_device);
		//btn_uart = (Button) findViewById(R.id.btn_uart);
		
		btn_filedemo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(DemoActivity.this,FileDemoActivity.class);
				Log.d(TAG,"Start the FileDemoActivity");
				startActivity(intent);
			}
		});

		
	/*	btn_adapter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(DemoActivity.this,BluetoothAdapterDemoActivity.class);
				Log.d(TAG,"Start the BluetoothAdapterDemoActivity");
				startActivity(intent);
			}
		}); */
		btn_device.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(DemoActivity.this,BluetoothDeviceDemoActivity.class);
				Log.d(TAG,"Start the BluetoothDeviceDemoActivity");
				startActivity(intent);
			}
		});
//		btn_uart.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				Intent intent = new Intent(DemoActivity.this,UARTDemoActivity.class);
//				startActivity(intent);
//			}
//		});
	}

	@Override
	protected void onDestroy() {
		
		// (2) Example of stopConsoleLog()
		TgStreamReader.stopConsoleLog();
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}


}
