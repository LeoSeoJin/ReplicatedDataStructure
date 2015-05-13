package com.example.puzzle.network.wifi.pack;

import org.worldsproject.puzzle.PuzzleGroup;

import android.util.Log;

import com.example.puzzle.network.wifi.WifiApplication;
import com.example.puzzle.network.wifi.pack.MyMessage;
import com.google.gson.Gson;

public class MessageService {
	private static final String TAG = "MessageService";
	private WifiApplication app;
	private String deviceName;
	private String deviceIp;
	private Gson gson;
	
	public MessageService(WifiApplication app, String name, String ip) {
		this.app = app;
		this.deviceName = name;
		this.deviceIp = ip;
	}
	
	public void sendMsg(String msg) {
		//Log.i(TAG, "into sendMsg(Message msg) msg =" + msg);
		if (app.server != null) {
			app.server.sendMsgToAllCLients(msg);
		} else if (app.client != null) {
			app.client.sendMsg(msg);
		}
		//Log.i(TAG, "out sendMsg(Message msg) msg =" + msg);
	}
	
	public String structMessage(String type, int t1, int t2) {
		MyMessage msg = new MyMessage();
		msg.setType(type);
		msg.setDeviceName(deviceName);
		msg.setNetAddress(deviceIp);
		msg.setMsg(Integer.toString(t1)+"_"+Integer.toString(t2));
		gson = new Gson();
		return gson.toJson(msg);
	}
	
	public String structMessage(String type, int[] x, int[] y) {
		MyMessage msg = new MyMessage();
		msg.setType(type);
		msg.setDeviceName(deviceName);
		msg.setNetAddress(deviceIp);	
		StringBuffer text = new StringBuffer("");
		for (int p: x) {
			text.append(p);
			text.append(" ");
		}
		text.append("_");
		for (int p: y) {
			text.append(p);
			text.append(" ");
		}
		msg.setMsg(text.toString());
		gson = new Gson();
		return gson.toJson(msg);
	}
	
	public String structMessage(String type, int text) {
		MyMessage msg = new MyMessage();
		msg.setType(type);
		msg.setDeviceName(deviceName);
		msg.setNetAddress(deviceIp);
		msg.setMsg(Integer.toString(text));
		gson = new Gson();
		return gson.toJson(msg);
	}

	public String structMessage(String type, int[] array) {
		MyMessage msg = new MyMessage();
		msg.setType(type);
		msg.setDeviceName(deviceName);
		msg.setNetAddress(deviceIp);

		StringBuffer text = new StringBuffer("");
		for (int p: array) {
			text.append(p);
			text.append("_");
		}
		msg.setMsg(text.toString());
		
		gson = new Gson();
		return gson.toJson(msg);
	}

	public String structMessage(String type, boolean isOnDown) {
		MyMessage msg = new MyMessage();
		msg.setType(type);
		msg.setDeviceName(deviceName);
		msg.setNetAddress(deviceIp);

		String text;
		if (isOnDown) 
			text = "true";
		else
			text = "false";
		msg.setMsg(text);
		
		gson = new Gson();
		return gson.toJson(msg);
	}

	public String structMessage(String type, int[] p, int[] x, int[] y) {
		MyMessage msg = new MyMessage();
		msg.setType(type);
		msg.setDeviceName(deviceName);
		msg.setNetAddress(deviceIp);	
		StringBuffer text = new StringBuffer("");
		for (int i: p) {
			text.append(i);
			text.append(" ");
		}
		text.append("_");
		for (int i: x) {
			text.append(i);
			text.append(" ");
		}
		text.append("_");
		for (int i: y) {
			text.append(i);
			text.append(" ");
		}
		msg.setMsg(text.toString());
		gson = new Gson();
		return gson.toJson(msg);
	}
}
