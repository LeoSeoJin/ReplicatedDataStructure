package com.example.puzzle;

import org.worldsproject.puzzle.PuzzleView;
import org.worldsproject.puzzle.enums.Difficulty;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;


public class GameActivity extends Activity {
	private PuzzleView pv;

	private Difficulty x;
	private int puzzle;
	private Bitmap image;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//ActivityManager.getInstance().addActivity(GameActivity.this);
		setContentView(R.layout.puzzle);
		initControls();
	}
	
	private void initControls(){
		Bundle extras=this.getIntent().getExtras();
		int level=extras.getInt("level");
		puzzle = extras.getInt("puzzle");
		
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
 		pv.loadPuzzle(image, x, path(puzzle, x.toString()));
 		pv.setContext(this);
 		System.out.println("**********difficulty "+pv.getDifficulty());
 		
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
}
