package com.example.puzzle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
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
	
	private int screenWidth=0;//屏幕的宽度
    private int screenHeight=0;//屏幕的高度
    private Spinner levelSp;//等级
	private GridView pictureGridView;//图片
	private Button confBtn;
	private Button returnBtn;
	
	private static final int[] pictureArray={R.drawable.model0,R.drawable.model1,R.drawable.model2,
											R.drawable.model3,R.drawable.model4,R.drawable.model5,
											R.drawable.model6,R.drawable.model7,R.drawable.model8};

	private int pictureIndex=0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initControls();
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
				Bundle extras=new Bundle();
				extras.putInt("level", levelSp.getSelectedItemPosition());
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
}