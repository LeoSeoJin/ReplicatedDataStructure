package com.example.puzzle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import java.util.List;
import java.util.Random;

import com.example.puzzle.network.wifi.WifiApplication;
import com.example.puzzle.network.wifi.WifiHotAdapter;
import com.example.puzzle.network.wifi.pack.Global;
import com.example.puzzle.network.wifi.pack.SocketClient.ClientMsgListener;
import com.example.puzzle.network.wifi.pack.SocketServer.ServerMsgListener;
import com.example.puzzle.network.wifi.pack.WifiHotManager.OpretionsType;
import com.example.puzzle.network.wifi.pack.WifiHotManager.WifiBroadCastOperations;
import com.example.puzzle.network.wifi.pack.WifiHotManager;
import com.example.puzzle.network.wifi.pack.SocketClient;
import com.example.puzzle.network.wifi.pack.SocketServer;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class WifiHotActivity extends Activity implements WifiBroadCastOperations {
	private static String TAG = "WifiHotActivity";
	private static final Random RAN = new Random();
	
	private Button wifiHotBtn;		
	private Button scanHotsBtn;
	private Button wifiHome;
	
	private TextView statu;
	private WifiHotManager wifiHotM;
	private List<ScanResult> wifiList;
	private SocketClient client;
	private SocketServer server;
	private ListView listView;
	private WifiHotAdapter adapter;
	private boolean connected;
	private String mSSID;
	private Handler serverHandler;
	private Handler clientHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "into onCreate()");
		super.onCreate(savedInstanceState);
		ActivityManager.getInstance().addActivity(WifiHotActivity.this);
		setContentView(R.layout.actiivty_wifihotlist);
		initClientHandler();
		initServerHandler();

		wifiHotBtn = (Button) findViewById(R.id.createHot);
		wifiHotBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				wifiHotM.startAWifiHot("Puzzle");
				initServer();
			}
		});
			
		scanHotsBtn = (Button) findViewById(R.id.scanHots);
		scanHotsBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				wifiHotM.scanWifiHot();
			}
		});

		wifiHome = (Button) findViewById(R.id.wifi_home);
		wifiHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveData();
				Intent intent = new Intent(WifiHotActivity.this, StartInterface.class);
				startActivity(intent);
			}
		});
		
		listView = (ListView) findViewById(R.id.listHots);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ScanResult result = wifiList.get(position);
				mSSID = result.SSID;
				statu.setText("search list");
				Log.i(TAG, "into  onItemClick() SSID= " + result.SSID);
				wifiHotM.connectToHotpot(result.SSID, wifiList, Global.PASSWORD);
				Log.i(TAG, "out  onItemClick() SSID= " + result.SSID);
			}
		});
		statu = (TextView) findViewById(R.id.hotTitleName);
		Log.i(TAG, "out onCreate()");
	}

	private String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
	}

	public WifiHotManager getWifiHotManager() {
		return wifiHotM;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onResume() {
		wifiHotM = WifiHotManager.getInstance(WifiHotActivity.this, WifiHotActivity.this);
		wifiHotM.scanWifiHot();
		super.onResume();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Log.i(TAG, "into onBackPressed()");
			//wifiHotM.unRegisterWifiScanBroadCast();
			//wifiHotM.unRegisterWifiStateBroadCast();
			//wifiHotM.disableWifiHot();
			//this.finish();
			
			AlertDialog.Builder builder = new AlertDialog.Builder(WifiHotActivity.this);
			builder.setIcon(R.drawable.question_dialog_icon);
			builder.setTitle("Exit");
			builder.setMessage("Are you sure to exit£¿");
			builder.setPositiveButton(R.string.confirm,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							WifiHotActivity.this.finish();
							ActivityManager.getInstance().exit();
						}
					});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			builder.create().show();			
			Log.i(TAG, "out onBackPressed()");
			return true;
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "into onDestroy() ");
		if (adapter != null) {
			adapter.clearData();
			adapter = null;
		}
		if (server != null) {
			server.clearServer();
			server.stopListner();
			server = null;
			wifiHotM.disableWifiHot();
			}
		if (client != null) {
			client.clearClient();
			client.stopAcceptMessage();
			client = null;
			wifiHotM.deleteMoreCon(mSSID);
		}
		System.exit(0);
		Log.i(TAG, "out onDestroy() ");
		super.onDestroy();
	}

	@Override
	public void disPlayWifiScanResult(List<ScanResult> wifiList) {
		Log.i(TAG, "into wifi scan");
		this.wifiList = wifiList;
		wifiHotM.unRegisterWifiScanBroadCast();
		refreshWifiList(wifiList);
		Log.i(TAG, "out wifi scan " + wifiList);
	}

	@Override
	public boolean disPlayWifiConResult(boolean result, WifiInfo wifiInfo) {
		Log.i(TAG, "into connect wifi");
		String ip = "";
		wifiHotM.setConnectStatu(false);
		wifiHotM.unRegisterWifiStateBroadCast();
		wifiHotM.unRegisterWifiConnectBroadCast();
		initClient(ip);
		Log.i(TAG, "out connect wifi");
		return false;
	}

	@Override
	public void operationByType(OpretionsType type, String SSID) {
		Log.i(TAG, "into operationByType = " + type);
		if (type == OpretionsType.CONNECT) {
			wifiHotM.connectToHotpot(SSID, wifiList, Global.PASSWORD);
		} else if (type == OpretionsType.SCAN) {
			wifiHotM.scanWifiHot();
		}
		Log.i(TAG, "out operationByType");
	}

	private void initServer() {
		server = SocketServer.newInstance(Global.PORT, new ServerMsgListener() {
			Message msg = null;

			@Override
			public void handlerHotMsg(String hotMsg) {
				connected = true;
				Log.i(TAG, "server initial");
				msg = clientHandler.obtainMessage();
				msg.obj = hotMsg;
				msg.what = 1;
				serverHandler.sendMessage(msg);
			}

			@Override
			public void handlerErorMsg(String errorMsg) {
				connected = false;
				Log.d(TAG, "server error initial");
				msg = clientHandler.obtainMessage();
				msg.obj = errorMsg;
				msg.what = 0;
				serverHandler.sendMessage(msg);
			}
		});
		server.beginListen();
	}

	// client 
	private void initClient(String IP) {
		client = SocketClient.newInstance("192.168.43.1", Global.PORT, new ClientMsgListener() {

			Message msg = null;

			@Override
			public void handlerErorMsg(String errorMsg) {
				connected = false;
				Log.d(TAG, "client error initial");
				msg = clientHandler.obtainMessage();
				msg.obj = errorMsg;
				msg.what = 0;
				clientHandler.sendMessage(msg);

			}

			@Override
			public void handlerHotMsg(String hotMsg) {
				connected = true;
				Log.i(TAG, "client handle msg");
				msg = clientHandler.obtainMessage();
				msg.obj = hotMsg;
				msg.what = 1;
				clientHandler.sendMessage(msg);

			}
		});
		client.connectServer();
	}

	private void refreshWifiList(List<ScanResult> results) {
		Log.i(TAG, "into refresh wifi list");
		if (null == adapter) {
			Log.i(TAG, "into refresh, adapter is null");
			adapter = new WifiHotAdapter(results, this);
			listView.setAdapter(adapter);
		} else {
			Log.i(TAG, "into refresh, adapter is not null");
			
			adapter.refreshData(results);
		}
		Log.i(TAG, "out refresh");
	}

	private void saveData() {
		Log.i(TAG, "into saveData()");
		WifiApplication app = (WifiApplication) this.getApplication();
		app.client = this.client;
		app.server = this.server;
		app.wifiHotM = this.wifiHotM;
		Log.i(TAG, "out saveData() app client ="+app.client);
		Log.i(TAG, "out saveData() app server ="+app.server);
		Log.i(TAG, "out saveData() app wifiHotM ="+app.wifiHotM);
	}

	private void initServerHandler() {
		serverHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Log.i(TAG, "into initServerHandler() handleMessage(Message msg)");
				if (msg.what == 0) {
					statu.setText("initial server handle");
				} else {
					String text = (String) msg.obj;
					statu.setText("initial server handle");
					Log.i(TAG, "into initServerHandler() handleMessage(Message msg) text = " + text);
				}
			}
		};
	}

	private void initClientHandler() {
		clientHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Log.i(TAG, "into initClientHandler() handleMessage(Message msg)");
				if (msg.what == 0) {
					statu.setText("unsuccessfully initial Client");
				} else {
					statu.setText("successfully initial Client");
					String text = (String) msg.obj;
					Log.i(TAG, "into initClientHandler() handleMessage(Message msg) text =" + text);
				}
			}
		};
	}
}