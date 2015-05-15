package org.worldsproject.puzzle;

import java.util.ArrayList;
import java.util.List;

import org.worldsproject.puzzle.enums.Difficulty;

import com.example.puzzle.GameActivity;
import com.example.puzzle.R;
import com.example.puzzle.network.wifi.pack.ConsoleMessage;
import com.example.puzzle.network.wifi.pack.Global;
import com.example.puzzle.network.wifi.pack.MessageService;
import com.example.puzzle.network.wifi.pack.MyMessage;
import com.example.puzzle.network.wifi.pack.SocketClient.ClientMsgListener;
import com.example.puzzle.network.wifi.pack.SocketServer.ServerMsgListener;
import com.google.gson.Gson;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class PuzzleView extends View implements OnGestureListener,
		OnDoubleTapListener, OnScaleGestureListener, AnimationListener, ConsoleMessage {
	private static final String TAG = "PuzzleView";
	
	private Puzzle puzzle;
	private GestureDetector gesture;
	private ScaleGestureDetector scaleGesture;
	private Piece tapped;
	private boolean firstDraw = true;
	private float scale = 1.0f;
	private Difficulty difficulty;
	private Context context;
	private int[] x_array;
	private int[] y_array;
	private boolean[] mask_array;
	
	private MessageService msgService = new MessageService(Global.APP, Global.DEVICENAME, Global.IP);
	private Gson gson;
	private Handler serverHandler;
	private Handler clientHandler;
	public List<MyMessage> messages = new ArrayList<MyMessage>();
	
	public PuzzleView(Context context) {
		super(context);
	}

	public PuzzleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public PuzzleView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public Difficulty getDifficulty() {
		return difficulty;
	}
	
	public void loadPuzzle(Bitmap image, Difficulty difficulty, String location) {
		Log.i(TAG, "into load puzzle");
		
		gesture = new GestureDetector(this.getContext(), this);
		scaleGesture = new ScaleGestureDetector(this.getContext(), this);		
		
		PuzzleGenerator temp = new PuzzleGenerator(this.getContext());
		temp.setMask(mask_array);
		puzzle = temp.generatePuzzle(this.getContext(), image, difficulty, location);
		puzzle.savePuzzle(getContext(), location, true);
		
		Log.i(TAG, "out load puzzle");
		
		Piece.resetSerial();
		
		this.difficulty = difficulty;
	}


	public void loadPuzzle(Bitmap image, Difficulty difficulty, String location, int[] x_array, int[] y_array) {
		Log.i(TAG, "into load puzzle");
		
		gesture = new GestureDetector(this.getContext(), this);
		scaleGesture = new ScaleGestureDetector(this.getContext(), this);		
		
		puzzle = new PuzzleGenerator(this.getContext()).generatePuzzle(
				this.getContext(), image, difficulty, location);
		puzzle.savePuzzle(getContext(), location, true);
		
		Log.i(TAG, "out load puzzle");
		
		Piece.resetSerial();
		
		this.difficulty = difficulty;
		
	}
	
	public void loadPuzzle(String location) {
		gesture = new GestureDetector(this.getContext(), this);
		scaleGesture = new ScaleGestureDetector(this.getContext(), this);

		puzzle = new Puzzle(this.getContext(), location);
		firstDraw = false;
		
		Piece.resetSerial();
	}

	public void savePuzzle(String location) {
		puzzle.savePuzzle(this.getContext(), location, false);
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (firstDraw) {
			firstDraw = false;
			//puzzle.shuffle(this.getWidth(), this.getHeight());
			puzzle.shuffle(x_array,y_array);
		}
		canvas.scale(scale, scale);
		puzzle.draw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.i(TAG,"**********onTouchEvent");
		scaleGesture.onTouchEvent(event);
		gesture.onTouchEvent(event);
		return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent arg0) {
		Log.i(TAG,"**********onDoubleTap");
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		Log.i(TAG,"**********onDoubleTapEvent");
		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		Log.i(TAG,"**********onSingleTapConfirmed");
		if (checkSurroundings(tapped))
			this.invalidate();

		if (isFinished(tapped))
			openFinishDialog();
		
		if (tapped != null)
			tapped.setIsOnDown(false);
		
		return true;
	}
    
	@Override
	public boolean onDown(MotionEvent e1) {
		Log.i(TAG,"**********onDown");
		// Get the piece that is under this tap.
		Piece possibleNewTapped = null;
		boolean shouldPan = true;

		for (int i = this.puzzle.getPieces().size()-1; i >= 0; i--) {
			Piece p = this.puzzle.getPieces().get(i);
			
			if (p.inMe((int) (e1.getX() / scale), (int) (e1.getY() / scale))) {
				if (p == tapped) {
					Log.i(TAG," p == tapped");
					//possibleNewTapped = null;
				} else {
					possibleNewTapped = p;
				}
				shouldPan = false;
				break;
			}
		}

		//Log.i(TAG,"shouldpan "+shouldPan);
		
		if (possibleNewTapped != null) {
			if (tapped != null) //reset the previous tapped's isOnDown
				tapped.setIsOnDown(false);
			tapped = possibleNewTapped;
		}

		if (shouldPan) {
			if (tapped != null)
				tapped.setIsOnDown(false);
			tapped = null;
		}
		
		//Log.i(TAG,"tapped"+tapped);
		if (tapped != null) { 
			tapped.setIsOnDown(true);
			Log.i(TAG, ""+tapped.getSerial()+" is onDown");
			/*get the state of tapped on others'*/
			boolean isOtherOnDown = isOtherOnDown(readPhase(tapped));
			removeMsg(tapped); //remove the ack messages for next receive process 
			if (isOtherOnDown) {
				warnDialog();
				tapped = null;
			}
		}

		return true;
	}

	private boolean isOtherOnDown(ArrayList<String> array) {
		int number = 0;
		Log.i(TAG,"array "+array.size());
		for (String s: array) {
			Log.i(TAG, "array s "+s);
			if (s.equals("true"))
				number++;
		}
		Log.i(TAG,"number "+number);
		if (number >= 1) 
			return true;
		else 
			return false;
	}
	
	private ArrayList<String> readPhase(Piece p) {
		msgService.sendMsg(msgService.structMessage("readpiece", p.getSerial()));
		ArrayList<String> result = new ArrayList<String>();
		for (MyMessage m: messages) {
			if (m.getType().equals("ack_readpiece"+"_"+p.getSerial())) {
				result.add(m.getMsg());
			}
		}
		Log.i(TAG,"readphase "+result.size());
		
		return result;
	}
	
	private void removeMsg(Piece p) {
		if (messages.size() == 0) return;
		
		for (int i = 0; i < messages.size(); i++) {
			if (messages.get(i).getType().equals("ack_readpiece"+"_"+p.getSerial())) {
				messages.remove(i--);
			}
		}
	}

	private void warnDialog(){	
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(R.drawable.alert_dialog_icon);
		builder.setTitle("Warning");
		builder.setMessage("The piece has been select by your companion, please reselect");
		builder.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//GameActivity.this.finish();
					}
				});
		builder.create().show();
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		Log.i(TAG,"**********onFling");
		if (checkSurroundings(tapped))
			this.invalidate(); //redraw the view

		if (isFinished(tapped)) 
			openFinishDialog();
		
		if (tapped != null)
			tapped.setIsOnDown(false);
		
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		Log.i(TAG,"**********onLongPress");
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		Log.i(TAG,"**********onScroll");
		if (tapped == null) {// We aren't hitting a piece
			//puzzle.translate(distanceX / scale, distanceY / scale);
		} else {
			//tapped.setIsOnDown(true);
			tapped.getGroup().translate((int) (distanceX / scale),
					(int) (distanceY / scale));
		}
		
		this.invalidate();
		
		//if (tapped != null)
			//tapped.setIsOnDown(false);
		return true;
	}

	private boolean checkSurroundings(Piece tapped) {
		Log.i(TAG,"***********checkSurroundings");
		
		if (tapped == null || tapped.getOrientation() != 0) {
			return false;
		}

		boolean rv = false;

		if (tapped.inLeft()) {
			tapped.snap(tapped.getLeft());
			rv = true;
		}

		if (tapped.inRight()) {
			tapped.snap(tapped.getRight());
			rv = true;
		}

		if (tapped.inBottom()) {
			tapped.snap(tapped.getBottom());
			rv = true;
		}

		if (tapped.inTop()) {
			tapped.snap(tapped.getTop());
			rv = true;
		}

		return rv;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		if (checkSurroundings(tapped))
			this.invalidate();

		if (isFinished(tapped)) 
			openFinishDialog();	
		
		//if (tapped != null)
			//tapped.setIsOnDown(false);
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		Log.i(TAG,"**********onSingleTapUp");
		if (checkSurroundings(tapped))
			this.invalidate();

		if (isFinished(tapped)) 
			openFinishDialog();
		
		if (tapped != null)
			tapped.setIsOnDown(false);
		
		return true;
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		Log.i(TAG,"**********onScale");
		scale *= detector.getScaleFactor();
		this.invalidate();
		return true;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		Log.i(TAG,"**********onScaleBegin");
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
		Log.i(TAG,"**********onScaleEnd");
	}

	public void shuffle() {
		puzzle.shuffle(this.getWidth(), this.getHeight());
		this.invalidate();
	}

	public void solve() {
		Animation a = new AlphaAnimation(1, 0);
		a.setDuration(2000);
		a.setAnimationListener(this);
		this.startAnimation(a);
		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		this.puzzle.solve();
		Piece zero = this.puzzle.getPieces().get(0);
		zero.getGroup().translate(zero.getX()+5, zero.getY()+5);
		this.invalidate();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}
	

	public boolean isFinished(Piece p) {
		// TODO Auto-generated method stub
		if (p == null || p.getGroup() == null) 
			return false; 
		if (p.getGroup().getGroup().size()==difficulty.getPieceNum())
			return true;
		else 
			return false;
	}
	
	public void finishedAction() {
		msgService.sendMsg(msgService.structMessage("finished", true));
		openFinishDialog();
	}
	
    private void openFinishDialog(){
    	Builder builder = new AlertDialog.Builder(context);
    	builder.setIcon(R.drawable.success);
		builder.setTitle("Congratulations£¡");
		builder.setMessage("Return to game");
		builder.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						((GameActivity)context).finish();
					}
				});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.create().show();
    }

    
	public void setContext(GameActivity activity) {
		// TODO Auto-generated method stub
		context = activity;
	}
	
	public Puzzle getPuzzle() {
		return puzzle;
	}
	
	public void setCoordinate(int[] x_array, int[] y_array) {
		this.x_array = new int[x_array.length];
		for (int i = 0; i < x_array.length; i++) 
			this.x_array[i] = x_array[i];
		
		this.y_array = new int[y_array.length];
		for (int i = 0; i < y_array.length; i++) 
			this.y_array[i] = y_array[i];
	}
	

	public void setMask(int[] mask_array) {
		this.mask_array = new boolean[mask_array.length];
		for (int i = 0; i < mask_array.length; i++) {
			if (mask_array[i] == 1)
				this.mask_array[i] = true;
			else
				this.mask_array[i] = false;
		}
	}
	
	private void initServerListener() {

		if (Global.APP.server == null) {
			return;
		}
		Log.i(TAG, "into initServerListener() app server =" + Global.APP.server);
		Global.APP.server.setMsgListener(new ServerMsgListener() {
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
		if (Global.APP.client == null) {
			return;
		}
		//Log.i(TAG, "into initClientListener() app client =" + Global.APP.client);
		Global.APP.client.setMsgListener(new ClientMsgListener() {

			Message msg = null;

			@Override
			public void handlerHotMsg(String hotMsg) {
				//Log.i(TAG, "into initClientListener() handlerHotMsg(String hotMsg) hotMsg = " + hotMsg);
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
		if (Global.APP.server == null) {
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
				messages.add(Msg);
				console(Msg);
				Log.i(TAG, "into initServerHandler() handleMessage(Message msg) chatMessage = " + Msg);
			}
		};
	}

	private void initClientHandler() {
		if (Global.APP.client == null) {
			Log.i(TAG,"app.client is null");
			return;
		}
		clientHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				//Log.i(TAG, "into initClientHandler() handleMessage(Message msg)");
				String text = (String) msg.obj;
				gson = new Gson();
				MyMessage Msg = gson.fromJson(text, MyMessage.class);
				messages.add(Msg);
				
				console(Msg);
				
				//Log.i(TAG, "into initClientHandler() handleMessage(Message msg) chatMessage =" + Msg);
			}
		};
	}


	@Override
	public void console(MyMessage msg) {
		if (msg.getType().equals("readpiece") && !msg.getNetAddress().equals(Global.IP)) {
			int serial = Integer.parseInt(msg.getMsg());
			PuzzleGroup temp = puzzle.getPieces().get(serial-1).getGroup();
			Log.i(TAG,"group "+temp.getSerial());
			boolean ack = false;
			for (Piece p: temp.getGroup()) {
				Log.i(TAG,"p ondown "+p.getIsOnDown());
				if (p.getIsOnDown()) {
					Log.i(TAG,"p is ondown "+p.getSerial());
					ack = true;
					break;
				}
			}
			Log.i(TAG,"ack "+ack);
			msgService.sendMsg(msgService.structMessage("ack_readpiece"+"_"+serial, ack));
			messages.remove(msg); //remove the "readpiece" message;
		}
		if (msg.getType().equals("writepiece") && !msg.getNetAddress().equals(Global.IP)) {
			Log.i(TAG,"writepiece msg");
			String[] pxy = new String[3];
			pxy = msg.getMsg().split("_");
			
			String[] p = new String[Difficulty.HARD.getRow()*Difficulty.HARD.getCol()];
			p = pxy[0].split(" ");
			String[] x = new String[p.length];
			x = pxy[1].split(" ");
			String[] y = new String[p.length];
			y = pxy[2].split(" ");
			for (int i = 0; i < p.length; i++) {
				puzzle.getPieces().get(Integer.parseInt(p[i])-1).setX(Integer.parseInt(x[i]));
				puzzle.getPieces().get(Integer.parseInt(p[i])-1).setY(Integer.parseInt(y[i]));
			}
			this.invalidate();
			for (int i = 0; i < p.length; i++) {
				Log.i(TAG,"Piece "+puzzle.getPieces().get(Integer.parseInt(p[i])-1).getSerial());
				Log.i(TAG,"x "+puzzle.getPieces().get(Integer.parseInt(p[i])-1).getX());
				Log.i(TAG,"y "+puzzle.getPieces().get(Integer.parseInt(p[i])-1).getY());
			}
		}
		if (msg.getType().equals("updategroup") && !msg.getNetAddress().equals(Global.IP)) {
			Log.i(TAG,"updategroup");
			String[] group_pieces  = new String[Difficulty.HARD.getRow()*Difficulty.HARD.getCol()];
			group_pieces = msg.getMsg().split("_");
			for (String p: group_pieces) {
				for (String piece: group_pieces) {
					int group = puzzle.getPieces().get(Integer.parseInt(p)-1).getGroup().getSerial();
					int pieceIndex = puzzle.getPieces().get(Integer.parseInt(piece)-1).getSerial();
					Log.i(TAG, "group "+group+" piece "+pieceIndex);
					puzzle.getPieces().get(Integer.parseInt(p)-1).getGroup().addPiece(puzzle.getPieces().get(Integer.parseInt(piece)-1));
				}
			}
		}
		
		if (msg.getType().equals("group") && !msg.getNetAddress().equals(Global.IP)) {
			PuzzleGroup g = (PuzzleGroup)msg.getBigMsg();
			for (Piece p: g.getGroup()) {
				puzzle.getPieces().get(p.getSerial()-1).setGroup(g);
			}
		}
		
		if (msg.getType().equals("finished") && !msg.getNetAddress().equals(Global.IP)) {
			this.invalidate();
			openFinishDialog();
		}
	}
	
	public void initServerClient() {
		initServerHandler();
		initClientHandler();
		initServerListener();
		initClientListener();
	}

}
