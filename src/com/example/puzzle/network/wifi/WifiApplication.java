package com.example.puzzle.network.wifi;

import com.example.puzzle.network.wifi.pack.SocketClient;
import com.example.puzzle.network.wifi.pack.SocketServer;
import com.example.puzzle.network.wifi.pack.WifiHotManager;

import android.app.Application;

public class WifiApplication extends Application {

	public SocketServer server;

	public SocketClient client;

	public WifiHotManager wifiHotM;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

}
