package org.worldsproject.puzzle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;


public class MyImageView extends ImageView {
	private String namespace="http://shadow.com";
	private int color;
	
	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		//color=;
	}


	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub 

		super.onDraw(canvas); 
		//»­±ß¿ò
		Rect rec=canvas.getClipBounds();
		rec.bottom--;
		rec.right--;
		Paint paint=new Paint();
		paint.setColor(color);
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(rec, paint);
	}
}
