package com.example.puzzle;

import java.util.ArrayList;
import java.util.List;

import org.worldsproject.puzzle.PuzzleView;
import org.worldsproject.puzzle.enums.Difficulty;

import com.example.puzzle.network.wifi.pack.MyMessage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;


public class GameActivity extends Activity{
	private static final String TAG = "GameActivity";
	private PuzzleView pv;

	private Difficulty x;
	private int puzzle;
	private Bitmap image;
	
	public static SoundPool sp;
	public static int music;
	
	public static Resources resource;
	public List<MyMessage> Messages = new ArrayList<MyMessage>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//ActivityManager.getInstance().addActivity(GameActivity.this);
		setContentView(R.layout.puzzle);
        
        /***
        msgService = new MessageService(app, deviceName, deviceIp);      
        initServerHandler();
    	initClientHandler();
    	initServerListener();
    	initClientListener();
    	****/
        
		initControls();
	}
	
	private void initControls(){
		Bundle extras=this.getIntent().getExtras();
		int level=extras.getInt("level");
		puzzle = extras.getInt("puzzle");
		int[] x_array = extras.getIntArray("xcoordinate");		
		int[] y_array = extras.getIntArray("ycoordinate");		
		int[] mask_array = extras.getIntArray("mask");
				
				
    	if(0==puzzle){
   			image=BitmapFactory.decodeResource(getResources(),R.drawable.img10);
   	 	}else if(1==puzzle){
   			image=BitmapFactory.decodeResource(getResources(),R.drawable.img11);
   	    }else if(2==puzzle){
   			image=BitmapFactory.decodeResource(getResources(),R.drawable.img12);
   	    }else if(3==puzzle){
   			image=BitmapFactory.decodeResource(getResources(),R.drawable.img13);
   	    }else if(4==puzzle){
   			image=BitmapFactory.decodeResource(getResources(),R.drawable.img14);
   	    }else if(5==puzzle){
   			image=BitmapFactory.decodeResource(getResources(),R.drawable.img15);
   	    }else if(6==puzzle){
   			image=BitmapFactory.decodeResource(getResources(),R.drawable.img16);
   	    }else if(7==puzzle){
   			image=BitmapFactory.decodeResource(getResources(),R.drawable.img17);
   	    }else if(8==puzzle){
   	 		image=BitmapFactory.decodeResource(getResources(),R.drawable.img18);
   	    }

    	resource = getResources();
    	if (level == 0) { 
    		x = Difficulty.EASY;
    	} else if (level == 1) {
    		x = Difficulty.MEDIUM;
    	} else {
    		x = Difficulty.HARD;		 
    	}

    	sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
    	music = sp.load(this, R.raw.error1, 1);

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
    	
    	pv.setCoordinate(x_array, y_array);
    	pv.setMask(mask_array);
    	pv.loadPuzzle(image, x, path(puzzle, x.toString()));
    	pv.initServerClient();
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
						MainActivity.isReceivedMsg = false;
						Intent intent = new Intent(GameActivity.this, MainActivity.class);
						startActivity(intent);
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
}
