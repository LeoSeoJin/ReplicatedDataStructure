package com.example.puzzle;

import java.util.ArrayList;
import java.util.List;

import org.worldsproject.puzzle.PuzzleView;
import org.worldsproject.puzzle.enums.Difficulty;

import com.example.puzzle.network.wifi.WifiApplication;
import com.example.puzzle.network.wifi.pack.MyMessage;
import com.example.puzzle.network.wifi.pack.SocketClient.ClientMsgListener;
import com.example.puzzle.network.wifi.pack.SocketServer.ServerMsgListener;
import com.google.gson.Gson;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;


public class GameActivity extends Activity {
	private static final String TAG = "GameActivity";
	private PuzzleView pv;

	private Difficulty x;
	private int puzzle;
	private Bitmap image;
	
	private WifiApplication app;
	private String deviceName;
	private String deviceIp;
	private Gson gson;
	private Handler serverHandler;
	private Handler clientHandler;
	public List<MyMessage> Messages = new ArrayList<MyMessage>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//ActivityManager.getInstance().addActivity(GameActivity.this);
		setContentView(R.layout.puzzle);
		
        deviceName = new Build().MODEL;
        deviceIp = "192.168.43.1";
        app = (WifiApplication) this.getApplication();
        
    	initServerHandler();
    	initClientHandler();
    	initServerListener();
    	initClientListener();
    	
		initControls();
	}
	
	private void initControls(){
		Bundle extras=this.getIntent().getExtras();
		int level=extras.getInt("level");
		puzzle = extras.getInt("puzzle");
		int[] x_array = extras.getIntArray("xcoordinate");
		for (int p: x_array)
			Log.i(TAG, "x_array "+p);
		
		int[] y_array = extras.getIntArray("ycoordinate");
		for (int p: x_array)
			Log.i(TAG, "x_array "+p);
		
		System.out.println("picture_index "+puzzle);
		
    	if(0==puzzle){
   			image=BitmapFactory.decodeResource(getResources(),R.drawable.img0);
   	 	}else if(1==puzzle){
   			image=BitmapFactory.decodeResource(getResources(),R.drawable.img1);
   	    }else if(2==puzzle){
   			image=BitmapFactory.decodeResource(getResources(),R.drawable.img2);
   	    }else if(3==puzzle){
   			image=BitmapFactory.decodeResource(getResources(),R.drawable.img3);
   	    }else if(4==puzzle){
   			image=BitmapFactory.decodeResource(getResources(),R.drawable.img4);
   	    }else if(5==puzzle){
   			image=BitmapFactory.decodeResource(getResources(),R.drawable.img5);
   	    }else if(6==puzzle){
   			image=BitmapFactory.decodeResource(getResources(),R.drawable.img6);
   	    }else if(7==puzzle){
   			image=BitmapFactory.decodeResource(getResources(),R.drawable.img7);
   	    }else if(8==puzzle){
   	 		image=BitmapFactory.decodeResource(getResources(),R.drawable.img8);
   	    }
    	
    	if (level == 0) { 
    		x = Difficulty.EASY;
    	} else if (level == 1) {
    		x = Difficulty.MEDIUM;
    	} else {
    		x = Difficulty.HARD;		 
    	}
    	 
    	pv = (PuzzleView) this.findViewById(R.id.puzzleView);
 		//File testExistance = new File(path(puzzle, x.toString()));

 		/****
 		if (testExistance != null && testExistance.exists()) {
 			System.out.println("*****testExistence is null");
 			pv.loadPuzzle(path(puzzle, x.toString()));
 		} else {
 			System.out.println("*****testExistence not null");
 			pv.loadPuzzle(image, x, path(puzzle, x.toString()));
 		}
 		****/
    	Log.i(TAG,"level "+level+" p "+puzzle);
    	
    	pv.setXarray(x_array);
    	pv.setYarray(y_array); 		
    	pv.loadPuzzle(image, x, path(puzzle, x.toString()));

 		pv.setContext(this);
 		
    }
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
			 exitThisActivity();
			 return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void exitThisActivity(){	
		AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
		builder.setIcon(R.drawable.question_dialog_icon);
		builder.setTitle("Back");
		builder.setMessage("Are you sure to back to reselect level and picture£¿");
		builder.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						GameActivity.this.finish();
					}
				});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.create().show();
	}
	
	private String path(int puzzle, String difficulty) {
		String rv = null;
		if (Environment.getExternalStorageState().equals("mounted")) {
			rv = getExternalCacheDir().getAbsolutePath() + "/" + puzzle + "/"
					+ difficulty + "/";
		} else {
			rv = getCacheDir().getAbsolutePath() + "/" + puzzle + "/"
					+ difficulty + "/";
		}
		return rv;
	}
	
	private void sendMsg(String msg) {
		Log.i(TAG, "into sendMsg(Message msg) msg =" + msg);
		if (app.server != null) {
			app.server.sendMsgToAllCLients(msg);
		} else if (app.client != null) {
			app.client.sendMsg(msg);
		}
		Log.i(TAG, "out sendMsg(Message msg) msg =" + msg);
	}
	
	private String structMessage(String text) {
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
				
				Log.i(TAG, "into initClientHandler() handleMessage(Message msg) chatMessage =" + Msg);
			}
		};
	}
}
