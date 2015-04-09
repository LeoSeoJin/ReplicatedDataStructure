package com.example.puzzle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class StartInterface extends Activity{
	private Button btstart;
	private Button btexit;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_interface);
        
        btstart = (Button) findViewById(R.id.start_button);
        btstart.setOnClickListener(bt_listener);
        btexit = (Button)findViewById(R.id.exit_button);
        btexit.setOnClickListener(bt_listener);
        
    }
    
    private View.OnClickListener bt_listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.start_button:
				Intent new_intent = new Intent(StartInterface.this,MainActivity.class);
				startActivity(new_intent);
			
			case R.id.exit_button:
				StartInterface.this.finish();
			}
		}
    };
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
			 exitApp();
			 return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void exitApp(){	
		AlertDialog.Builder builder = new AlertDialog.Builder(StartInterface.this);
		builder.setIcon(R.drawable.question_dialog_icon);
		builder.setTitle("Exit");
		builder.setMessage("Are you sure to exit£¿");
		builder.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						StartInterface.this.finish();
					}
				});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.create().show();
	}
}
