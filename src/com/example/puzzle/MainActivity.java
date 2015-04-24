package com.example.puzzle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.worldsproject.puzzle.enums.Difficulty;

import com.example.puzzle.R;
import com.example.puzzle.network.wifi.WifiApplication;
import com.example.puzzle.network.wifi.pack.ConsoleMessage;
import com.example.puzzle.network.wifi.pack.Global;
import com.example.puzzle.network.wifi.pack.MessageService;
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

public class MainActivity extends Activity implements ConsoleMessage{
	private static final String TAG = "MainActivity";
	private static final Random RAN = new Random();
	
	private final String deviceName = Global.DEVICENAME;
	private final String deviceIp = Global.IP;
	
	private int screenWidth=0;
    private int screenHeight=0;
    private Spinner levelSp;
	private GridView pictureGridView;
	private Button confBtn;
	private Button returnBtn;
	
	private Difficulty x;
	private WifiApplication app;
	private Gson gson;
	private Handler serverHandler;
	private Handler clientHandler;
	private boolean isReceivedMsg = false;
	private MessageService msgService;
	public List<MyMessage> Messages = new ArrayList<MyMessage>();
	
	private static final int[] pictureArray={R.drawable.model0,R.drawable.model1,R.drawable.model2,
											R.drawable.model3,R.drawable.model4,R.drawable.model5,
											R.drawable.model6,R.drawable.model7,R.drawable.model8};

	private int pictureIndex = 0;
	private int level = 0;
	private int[] x_array;
	private int[] y_array;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ActivityManager.getInstance().addActivity(MainActivity.this);
        Log.i(TAG,"onCreate");
        setContentView(R.layout.activity_main);
        app = (WifiApplication) this.getApplication();
        msgService = new MessageService(app, deviceName, deviceIp);
        initControls();
    	
    	initServerHandler();
    	initClientHandler();
    	initServerListener();
    	initClientListener();
    	
        showGridView();
    }
    
    private void initControls(){
    	Log.i(TAG,"initialControls");
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
       	
    	/*when back from other activity, initial it to false*/
    	isReceivedMsg = false;
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
				Log.i(TAG,"isReceivedMsg "+isReceivedMsg);
				if (!isReceivedMsg) {
					level = levelSp.getSelectedItemPosition();
					randomCoordinate(getRowCol(level));
					msgService.sendMsg(msgService.structMessage("level_picture", levelSp.getSelectedItemPosition(), pictureIndex));
					msgService.sendMsg(msgService.structMessage("coordinate", x_array, y_array));
					//sendMsg(structMessage("levle", levelSp.getSelectedItemPosition()));
					//sendMsg(structMessage("picture", pictureIndex));
					//sendMsg(structMessage("xcoordinate", x_array));
					//sendMsg(structMessage("ycoordinate", y_array));
				}
										
				Bundle extras=new Bundle();
				
				extras.putInt("level", level);
				extras.putInt("puzzle", pictureIndex);
				extras.putIntArray("xcoordinate", x_array);
				extras.putIntArray("ycoordinate", y_array);
				
				Intent intent=new Intent(MainActivity.this, GameActivity.class);
				intent.putExtras(extras);
				startActivity(intent);
				//startActivityForResult(intent, 1);

				break;
				
			case R.id.main_return_btn:
				Intent intent1 = new Intent(MainActivity.this,StartInterface.class);
				startActivity(intent1);
				MainActivity.this.finish();	
			}
		}
	};	
	
	private int getRowCol(int l) {
		if (l == 0) 
			return 2;
		else if (l == 4)
			return 3;
		else 
			return 4;
	}
	
	private void setDifficulty(int l) {
		if (l == 0)
			x = Difficulty.EASY;
		if (l == 1)
			x = Difficulty.MEDIUM;
		if (l == 2)
			x = Difficulty.HARD;
			
	}
	
	protected void randomCoordinate(int rowCol) {
		x_array = new int[rowCol*rowCol];
		y_array = new int[rowCol*rowCol];
		setDifficulty(level);
		int maxX = screenWidth - x.pieceSize();
		int maxY = (int) (screenHeight - x.pieceSize()*1.6);

		for (int i = 0; i < x_array.length; i++) {
			x_array[i] = RAN.nextInt(maxX);
			y_array[i] = RAN.nextInt(maxY);
		}
	}

	/*************
	private void sendMsg(String msg) {
		Log.i(TAG, "into sendMsg(Message msg) msg =" + msg);
		if (app.server != null) {
			app.server.sendMsgToAllCLients(msg);
		} else if (app.client != null) {
			app.client.sendMsg(msg);
		}
		Log.i(TAG, "out sendMsg(Message msg) msg =" + msg);
	}
	
	private String structMessage(String type, int t1, int t2) {
		MyMessage msg = new MyMessage();
		msg.setType(type);
		msg.setDeviceName(deviceName);
		msg.setNetAddress(deviceIp);
		msg.setMsg(Integer.toString(t1)+" "+Integer.toString(t2));
		gson = new Gson();
		return gson.toJson(msg);
	}
	
	private String structMessage(String type, int text) {
		MyMessage msg = new MyMessage();
		msg.setType(type);
		msg.setDeviceName(deviceName);
		msg.setNetAddress(deviceIp);
		msg.setMsg(Integer.toString(text));
		gson = new Gson();
		return gson.toJson(msg);
	}

	private String structMessage(String type, int[] array) {
		MyMessage msg = new MyMessage();
		msg.setType(type);
		msg.setDeviceName(deviceName);
		msg.setNetAddress(deviceIp);

		StringBuffer text = new StringBuffer("");
		for (int p: array) {
			text.append(p);
			text.append(" ");
		}
		msg.setMsg(text.toString());
		
		gson = new Gson();
		return gson.toJson(msg);
	}
	*****************/
	
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
				msgService.sendMsg(text);
				gson = new Gson();
				MyMessage Msg = gson.fromJson(text, MyMessage.class);
				Messages.add(Msg);
				console(Msg);
				Log.i(TAG, "into initServerHandler() handleMessage(Message msg) chatMessage = " + Msg);
			}
		};
	}

	public void console(MyMessage msg) {
		/*get the information*/
		isReceivedMsg = true;
		if (msg.getType().equals("level_picture")) {
			Log.i(TAG,"receiving level_picture");
			String[] temp = new String[2];
			temp = msg.getMsg().split("_");
			level = Integer.parseInt(temp[0]);
			pictureIndex = Integer.parseInt(temp[1]);
		}
		if (msg.getType().equals("coordinate")) {
			Log.i(TAG, "receiving coordinate");
			String[] coordinate = new String[2];
			coordinate = msg.getMsg().split("_");
			
			String[] xCoordinate = new String[getRowCol(level)*getRowCol(level)];
			xCoordinate = coordinate[0].split(" ");
			x_array = new int[xCoordinate.length];
			for (int i = 0; i < x_array.length; i++) {
				x_array[i] = Integer.parseInt(xCoordinate[i]);
			}	
			String[] yCoordinate = new String[getRowCol(level)*getRowCol(level)];
			yCoordinate = coordinate[1].split(" ");
			y_array = new int[yCoordinate.length];
			for (int i = 0; i < y_array.length; i++) {
				y_array[i] = Integer.parseInt(yCoordinate[i]);
			}

		}		
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
				Log.i(TAG,"hadle message "+Msg);
				Messages.add(Msg);
				
				/*get the information*/
				console(Msg);
				/***
				isReceivedMsg = true;
				if (Msg.getType().equals("level_picture")) {
					Log.i(TAG,"receiving level_picture");
					String[] temp = new String[2];
					temp = Msg.getMsg().split("_");
					level = Integer.parseInt(temp[0]);
					pictureIndex = Integer.parseInt(temp[1]);
				}
				if (Msg.getType().equals("coordinate")) {
					Log.i(TAG, "receiving coordinate");
					String[] coordinate = new String[2];
					coordinate = Msg.getMsg().split("_");
					
					String[] xCoordinate = new String[getRowCol(level)*getRowCol(level)];
					xCoordinate = coordinate[0].split(" ");
					x_array = new int[xCoordinate.length];
					for (int i = 0; i < x_array.length; i++) {
						x_array[i] = Integer.parseInt(xCoordinate[i]);
					}	
					String[] yCoordinate = new String[getRowCol(level)*getRowCol(level)];
					yCoordinate = coordinate[1].split(" ");
					y_array = new int[yCoordinate.length];
					for (int i = 0; i < y_array.length; i++) {
						y_array[i] = Integer.parseInt(yCoordinate[i]);
					}

				}
				****/
		
				/****
				if (Msg.getType().equals("level")) 
					level = Integer.parseInt(Msg.getMsg());
				if (Msg.getType().equals("picture"))
					pictureIndex = Integer.parseInt(Msg.getMsg());
				if (Msg.getType().equals("xcoordinate")) {
					Log.i(TAG, "receiving x coordinate");
					
					String[] temp = new String[getRowCol(level)*getRowCol(level)];
					temp = Msg.getMsg().split(" ");
					x_array = new int[temp.length];
					for (int i = 0; i < x_array.length; i++) {
						x_array[i] = Integer.parseInt(temp[i]);
					}
				}
				if (Msg.getType().equals("ycoordinate")){
					Log.i(TAG, "receiving y coordinate");
					
					String[] temp = new String[getRowCol(level)*getRowCol(level)];
					temp = Msg.getMsg().split(" ");
					y_array = new int[temp.length];
					for (int i = 0; i < y_array.length; i++) {
						y_array[i] = Integer.parseInt(temp[i]);
					}
				}
				*****/
				Log.i(TAG, "into initClientHandler() handleMessage(Message msg) chatMessage =" + Msg);
			}
		};
	}
}