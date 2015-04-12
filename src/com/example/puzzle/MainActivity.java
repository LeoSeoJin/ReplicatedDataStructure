package com.example.puzzle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.puzzle.R;
import com.example.puzzle.network.wifi.WifiApplication;
import com.example.puzzle.network.wifi.pack.MyMessage;
import com.example.puzzle.network.wifi.pack.SocketClient.ClientMsgListener;
import com.example.puzzle.network.wifi.pack.SocketServer.ServerMsgListener;
import com.google.gson.Gson;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	
	private int screenWidth=0;
    private int screenHeight=0;
    private Spinner levelSp;
	private GridView pictureGridView;
	private Button confBtn;
	private Button returnBtn;
	
	private WifiApplication app;
	private String deviceName;
	private String deviceIp;
	private Gson gson;
	private Handler serverHandler;
	private Handler clientHandler;
	private boolean isReceivedMsg = false;
	public List<MyMessage> Messages = new ArrayList<MyMessage>();
	
	private static final int[] pictureArray={R.drawable.model0,R.drawable.model1,R.drawable.model2,
											R.drawable.model3,R.drawable.model4,R.drawable.model5,
											R.drawable.model6,R.drawable.model7,R.drawable.model8};

	private int pictureIndex = 0;
	private int level = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ActivityManager.getInstance().addActivity(MainActivity.this);
        setContentView(R.layout.activity_main);
        deviceName = new Build().MODEL;
        deviceIp = "192.168.43.1";
        app = (WifiApplication) this.getApplication();
        initControls();
    	
    	initServerHandler();
    	initClientHandler();
    	initServerListener();
    	initClientListener();
    	
        showGridView();
    }
    
    private void initControls(){
    	levelSp=(Spinner)findViewById(R.id.main_level_spinner);
    	pictureGridView=(GridView)findViewById(R.id.main_picture_gridView);
    	pictureGridView.setOnItemClickListener(new ItemClickListener());//监听
    	pictureGridView.setOnItemSelectedListener(new ItemSelectedListener());//选择监听

		String[] level_list =this.getResources().getStringArray(R.array.levelArray);				
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,level_list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		levelSp.setAdapter(adapter);
		    	
    	confBtn=(Button)findViewById(R.id.main_confirm_btn);
    	//System.out.println("confbtn"+confBtn.getCompoundPaddingLeft()+" "+confBtn.getCompoundPaddingRight());
    	confBtn.setOnClickListener(btnOnClickListener);
    	returnBtn=(Button)findViewById(R.id.main_return_btn);
    	returnBtn.setOnClickListener(btnOnClickListener);
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
			 exitApp();
			 return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void showGridView(){
		 //获取屏幕大小
		DisplayMetrics dm = new DisplayMetrics();
        dm = this.getApplicationContext().getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        
        System.out.println("width"+screenWidth);
        System.out.println("height"+screenHeight);
        
        pictureGridView.setColumnWidth((screenWidth-20*4)/3);

		SimpleAdapter saImageItems = new SimpleAdapter(this, getAllItemsForListView(),	R.layout.grid_item,
				new String[] { "ImageView"},
				new int[] { R.id.gridItem_imgView});
		pictureGridView.setAdapter(saImageItems);
		pictureGridView.setSelector(R.drawable.menuitemshape);

		
	}
	
	public List<Map<String, Object>> getAllItemsForListView() {
		// TODO Auto-generated method stub
		// 生成动态数组，并且传入数据
		List<Map<String, Object>> imageItem = new ArrayList<Map<String, Object>>();
		for (int i=0;i<pictureArray.length;i++) {
			HashMap<String, Object> tempMap = new HashMap<String, Object>();
//			Bitmap bitamp=BitmapFactory.decodeResource(this.getResources(), pictureArray[i]);
			tempMap.put("ImageView",pictureArray[i]);//
			imageItem.add(tempMap);
		}
		return imageItem;
	}

	private class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> adapterView,	View view,int position,	long rowid){
			// 在本例中position=rowid
			pictureIndex=position;
		}

	}

	private class ItemSelectedListener implements OnItemSelectedListener{

		public void onItemSelected(AdapterView<?> adapterView, View view, int position,
				long rowid) {
			// TODO Auto-generated method stub
//			HashMap<String, Object> item = (HashMap<String, Object>) adapterView.getItemAtPosition(position);
			pictureIndex=position;
		}
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}

	private void exitApp(){	
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setIcon(R.drawable.question_dialog_icon);
		builder.setTitle("Exit");
		builder.setMessage("Are you sure to exit");
		builder.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						MainActivity.this.finish();
						ActivityManager.getInstance().exit();
					}
				});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.create().show();
	}
	
	private View.OnClickListener btnOnClickListener=new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.main_confirm_btn:
				/*send the level and picture_index to the companion*/
				sendMsg(structChatMessage(levelSp.getSelectedItemPosition()+" "+pictureIndex));
						
				Bundle extras=new Bundle();
				
				if(!isReceivedMsg) 
					extras.putInt("level", levelSp.getSelectedItemPosition());
				else 
					extras.putInt("level", level);
				
				extras.putInt("puzzle", pictureIndex);
				
				Intent intent=new Intent(MainActivity.this, GameActivity.class);
				intent.putExtras(extras);
				startActivityForResult(intent, 1);

				break;
				
			case R.id.main_return_btn:
				Intent intent1 = new Intent(MainActivity.this,StartInterface.class);
				startActivity(intent1);
				MainActivity.this.finish();	
			}
		}
	};	
	
	
	private void sendMsg(String msg) {
		Log.i(TAG, "into sendMsg(Message msg) msg =" + msg);
		if (app.server != null) {
			app.server.sendMsgToAllCLients(msg);
		} else if (app.client != null) {
			app.client.sendMsg(msg);
		}
		Log.i(TAG, "out sendMsg(Message msg) msg =" + msg);
	}
	
	private String structChatMessage(String text) {
		MyMessage msg = new MyMessage();
		msg.setDeviceName(deviceName);
		msg.setNetAddress(deviceIp);
		msg.setMsg(text);
		gson = new Gson();
		return gson.toJson(msg);
	}

	private void initServerListener() {
		if (app.server == null) {
			return;
		}
		Log.i(TAG, "into initServerListener() app server =" + app.server);
		app.server.setMsgListener(new ServerMsgListener() {
			Message msg = null;

			@Override
			public void handlerHotMsg(String hotMsg) {
				Log.i(TAG, "into initServerListener() handlerHotMsg(String hotMsg) hotMsg = " + hotMsg);
				msg = serverHandler.obtainMessage();
				msg.obj = hotMsg;
				serverHandler.sendMessage(msg);
			}

			@Override
			public void handlerErorMsg(String errorMsg) {
				// TODO Auto-generated method stub

			}
		});
		Log.i(TAG, "out initServerListener() ");
	}

	private void initClientListener() {
		if (app.client == null) {
			return;
		}
		Log.i(TAG, "into initClientListener() app client =" + app.client);
		app.client.setMsgListener(new ClientMsgListener() {

			Message msg = null;

			@Override
			public void handlerHotMsg(String hotMsg) {
				Log.i(TAG, "into initClientListener() handlerHotMsg(String hotMsg) hotMsg = " + hotMsg);
				msg = clientHandler.obtainMessage();
				msg.obj = hotMsg;
				clientHandler.sendMessage(msg);
			}

			@Override
			public void handlerErorMsg(String errorMsg) {
				// TODO Auto-generated method stub
			}
		});
		Log.i(TAG, "out initClientListener()");
	}

	private void initServerHandler() {
		if (app.server == null) {
			Log.i(TAG,"app.server is null");
			return;
		}
		serverHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Log.i(TAG, "into initServerHandler() handleMessage(Message msg)");
				String text = (String) msg.obj;
				sendMsg(text);
				gson = new Gson();
				MyMessage Msg = gson.fromJson(text, MyMessage.class);
				Messages.add(Msg);
				Log.i(TAG, "into initServerHandler() handleMessage(Message msg) chatMessage = " + Msg);
			}
		};
	}

	private void initClientHandler() {
		if (app.client == null) {
			Log.i(TAG,"app.client is null");
			return;
		}
		clientHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Log.i(TAG, "into initClientHandler() handleMessage(Message msg)");
				String text = (String) msg.obj;
				gson = new Gson();
				MyMessage Msg = gson.fromJson(text, MyMessage.class);
				Messages.add(Msg);
				
				/*get the information of level and picture_index*/
				String[] level_picture = new String[2];
				level_picture = Msg.getMsg().split(" ");
				level = Integer.parseInt(level_picture[0]);
				pictureIndex = Integer.parseInt(level_picture[1]);
				isReceivedMsg = true;
				
				Log.i(TAG, "**********************level "+level+" index "+pictureIndex);	
				
				Log.i(TAG, "into initClientHandler() handleMessage(Message msg) chatMessage =" + Msg);
			}
		};
	}
}