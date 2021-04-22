package com.example.attentionmonitor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.neurosky.connection.ConnectionStates;
import com.neurosky.connection.DataType.MindDataType;
import com.neurosky.connection.TgStreamHandler;
import com.neurosky.connection.TgStreamReader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * This activity demonstrates how to use the constructor:
 * TgStreamReader(InputStream is, TgStreamHandler tgStreamHandler)
 * and related functions:
 * (1) setReadFileBlockSize
 * (2) setReadFileDelay
 * (3) How to destroy a TgStreamReader object
 * (4) ConnectionStates.STATE_COMPLETE is state that indicates read file to the end
 *
 */
public class FileDemoActivity extends Activity {
	private static final String TAG = com.example.attentionmonitor.FileDemoActivity.class.getSimpleName();
	
	private TextView tv_ps = null;
	private TextView tv_attention = null;
	private TextView tv_meditation = null;

	private Button btn_start = null;
	private Button btn_stop = null;


	private int badPacketCount = 0;
	FirebaseFirestore db = FirebaseFirestore.getInstance();
	private TgStreamReader tgStreamReader;


	private final TgStreamHandler callback = new TgStreamHandler() {

		@Override
		public void onStatesChanged(int connectionStates) {
			// TODO Auto-generated method stub
			Log.d(TAG, "connectionStates change to: " + connectionStates);
			switch (connectionStates) {
				case ConnectionStates.STATE_CONNECTED:
					//sensor.start();
					showToast("Connected", Toast.LENGTH_SHORT);
					break;
				case ConnectionStates.STATE_WORKING:

					break;
				case ConnectionStates.STATE_GET_DATA_TIME_OUT:
					//  get data time out
					break;
				case ConnectionStates.STATE_COMPLETE:
					//read file complete
					showToast("STATE_COMPLETE", Toast.LENGTH_SHORT);
					break;
			case ConnectionStates.STATE_STOPPED:
				break;
			case ConnectionStates.STATE_DISCONNECTED:
				break;
			case ConnectionStates.STATE_ERROR:
				break;
			}
			Message msg = LinkDetectedHandler.obtainMessage();
			msg.what = MSG_UPDATE_STATE;
			msg.arg1 = connectionStates;
			LinkDetectedHandler.sendMessage(msg);

		}

		@Override
		public void onRecordFail(int a) {
			// TODO Auto-generated method stub
			Log.e(TAG,"onRecordFail: " +a);

		}

		@Override
		public void onChecksumFail(byte[] payload, int length, int checksum) {
			// TODO Auto-generated method stub

			badPacketCount ++;
			Message msg = LinkDetectedHandler.obtainMessage();
			msg.what = MSG_UPDATE_BAD_PACKET;
			msg.arg1 = badPacketCount;
			LinkDetectedHandler.sendMessage(msg);

		}

		@Override
		public void onDataReceived(int datatype, int data, Object obj) {
			// TODO Auto-generated method stub
			Message msg = LinkDetectedHandler.obtainMessage();
			msg.what = datatype;
			msg.arg1 = data;
			msg.obj = obj;
			LinkDetectedHandler.sendMessage(msg);
			//Log.i(TAG,"onDataReceived");
		}

	};
	FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.first_view);

		initView();
	}

	private DatabaseReference databaseReference;
	private final Handler LinkDetectedHandler = new Handler() {

		@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

				case MindDataType.CODE_MEDITATION:
					Log.d(TAG, "CODE_MEDITATION " + msg.arg1);
					tv_meditation.setText("" + msg.arg1);
					break;
				case MindDataType.CODE_ATTENTION:
					Log.d(TAG, "CODE_ATTENTION " + msg.arg1);
					tv_attention.setText("" + msg.arg1);
					break;
				case MindDataType.CODE_POOR_SIGNAL://
					int poorSignal = msg.arg1;
					Log.d(TAG, "poorSignal:" + poorSignal);
					tv_ps.setText("" + msg.arg1);

					break;
				default:
					break;
			}

			super.handleMessage(msg);
			databaseReference.child(user.getDisplayName()).child("attention_Level").setValue(tv_attention.getText().toString().trim());
			databaseReference.child(user.getDisplayName()).child("meditation_Level").setValue(tv_meditation.getText().toString().trim());
		}

	};

	public void stop() {
		if (tgStreamReader != null) {
			tgStreamReader.stop();
			tgStreamReader.close();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (tgStreamReader != null) {
			tgStreamReader.close();
			tgStreamReader = null;
		}

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
		stop();
	}

	// TODO view

	private void initView() {
		tv_ps = (TextView) findViewById(R.id.tv_ps);
		tv_attention = (TextView) findViewById(R.id.tv_attention);
		tv_meditation = (TextView) findViewById(R.id.tv_meditation);


		btn_start = (Button) findViewById(R.id.btn_start);
		btn_stop = (Button) findViewById(R.id.btn_stop);


		databaseReference = FirebaseDatabase.getInstance().getReference("DataStore");


		btn_start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				badPacketCount = 0;

				// (3) How to destroy a TgStreamReader object
				if (tgStreamReader != null) {
					tgStreamReader.stop();
					tgStreamReader.close();
					tgStreamReader = null;
				}
				InputStream is = getApplicationContext().getResources().openRawResource(R.raw.tgam_capture);
				// Example of TgStreamReader(InputStream is, TgStreamHandler tgStreamHandler)
				tgStreamReader = new TgStreamReader(is, callback);

				// (1) Example of setReadFileBlockSize(int), the default block size is 8, call it before connectAndStart() or connect()
				tgStreamReader.setReadFileBlockSize(16);
				// (2) Example of setReadFileDelay(int), the default delay time is 2ms, call it before connectAndStart() or connect()
				tgStreamReader.setReadFileDelay(2);

				tgStreamReader.connectAndStart();
				addData();
				//addFirestoe();
			}
		});

		btn_stop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				stop();
			}

		});
	}

	private static final int MSG_UPDATE_BAD_PACKET = 1001;
	private static final int MSG_UPDATE_STATE = 1002;

	int raw;

	private void addData() {
		String id = user.getDisplayName();
		String name = user.getDisplayName();
		String attention = tv_attention.getText().toString().trim();
		String meditation = tv_meditation.getText().toString().trim();
		DataStore datastore = new DataStore(id, name, attention, meditation);
		db.collection("Userdata").document("User").set(datastore).addOnSuccessListener(new OnSuccessListener<Void>() {
			@Override
			public void onSuccess(Void aVoid) {
				Toast.makeText(FileDemoActivity.this, "User Registered",
						Toast.LENGTH_SHORT).show();
			}
		})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						Toast.makeText(FileDemoActivity.this, "ERROR" + e.toString(),
								Toast.LENGTH_SHORT).show();
						Log.d("TAG", e.toString());
					}
				});
		databaseReference.child(id).setValue(datastore).addOnSuccessListener(new OnSuccessListener<Void>() {
			@Override
			public void onSuccess(Void aVoid) {
				Toast.makeText(FileDemoActivity.this, "User Registered",
						Toast.LENGTH_SHORT).show();
			}
		})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						Toast.makeText(FileDemoActivity.this, "ERROR" + e.toString(),
								Toast.LENGTH_SHORT).show();
						Log.d("TAG", e.toString());
					}
				});
		//Toast.makeText(this, "Data added", Toast.LENGTH_LONG).show();
	}

	private void addFirestoe() {
		String name = user.getDisplayName();
		String attention = tv_attention.getText().toString().trim();
		String meditation = tv_meditation.getText().toString().trim();
		Map<String, Object> newData = new HashMap<>();
		newData.put("Name", name);
		newData.put("Attention", attention);
		newData.put("Meditation", meditation);
		db.collection("Monitor").document("UserData").set(newData)
				.addOnSuccessListener(new OnSuccessListener<Void>() {
					@Override
					public void onSuccess(Void aVoid) {
						Toast.makeText(FileDemoActivity.this, "User Registered",
								Toast.LENGTH_SHORT).show();
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						Toast.makeText(FileDemoActivity.this, "ERROR" + e.toString(),
								Toast.LENGTH_SHORT).show();
						Log.d("TAG", e.toString());
					}
				});
	}

	private void UpdateData() {
		String name = user.getDisplayName();
		String attention = tv_attention.getText().toString().trim();
		String meditation = tv_meditation.getText().toString().trim();
		DocumentReference userdata = db.collection("Monitor").document("UserData");
		userdata.update("Name", name);
		userdata.update("Attention", attention);
		userdata.update("Meditation", meditation)
				.addOnSuccessListener(new OnSuccessListener<Void>() {
					@Override
					public void onSuccess(Void aVoid) {
						Toast.makeText(FileDemoActivity.this, "Updated Successfully",
								Toast.LENGTH_SHORT).show();
					}
				});
	}

	public void showToast(final String msg, final int timeStyle) {
		com.example.attentionmonitor.FileDemoActivity.this.runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(getApplicationContext(), msg, timeStyle).show();
			}

		});
	}
}
