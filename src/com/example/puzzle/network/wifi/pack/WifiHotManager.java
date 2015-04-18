package com.example.puzzle.network.wifi.pack;

import java.util.List;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

public class WifiHotManager {
	public static String TAG = "WifiHotManager";

	private WifiHotAdmin wifiApadmin;

	private WifiManager mWifimanager;

	private static WifiHotManager instance = null;

	private Context context;

	private WifiBroadCastOperations operations;

	private WifiScanRsultBroadCast wifiScanReceiver;

	private WifiStateBroadCast wifiStateReceiver;

	private WifiConnectBroadCast wifiConnectReceiver;

	public boolean isConnecting;

	private String mSSID;

	public enum OpretionsType {

		CONNECT,

		SCAN;
	}

	public static interface WifiBroadCastOperations {

		public void disPlayWifiScanResult(List<ScanResult> wifiList);

		/**
		 * @param result wifi
		 * @param wifiInfo wifi
		 * @return wifi
		 */
		public boolean disPlayWifiConResult(boolean result, WifiInfo wifiInfo);

		/**
		 * @param type conntect wifi or scan wifi
		 * @param SSID wifi
		 */
		public void operationByType(OpretionsType type, String SSID);

	}

	public static WifiHotManager getInstance(Context context, WifiBroadCastOperations operations) {

		if (instance == null) {
			instance = new WifiHotManager(context, operations);

		}
		return instance;
	}

	private WifiHotManager(Context context, WifiBroadCastOperations operations) {
		this.context = context;
		this.operations = operations;
		wifiApadmin = WifiHotAdmin.newInstance(context);
		mWifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	}

	public boolean wifiIsOpen() {
		if (mWifimanager == null) {
			mWifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		}
		return mWifimanager.isWifiEnabled();
	}

	public void scanWifiHot() {
		Log.i(TAG, "into wifiHotScan()");
		if (!wifiIsOpen()) {
			Log.i(TAG, "out wifiHotScan() wifi is not open!");
			registerWifiStateBroadcast("");
			wifiStateReceiver.setOpType(OpretionsType.SCAN);
			openWifi();
		} else {
			Log.i(TAG, "out wifiHotScan() wifi is  open!");
			scanNearWifiHots();
		}
		Log.i(TAG, "out wifiHotScan()");
	}

	public void connectToHotpot(final String SSID, List<ScanResult> wifiList, final String password) {
		if (SSID == null || SSID.equals("")) {
			Log.d(TAG, "WIFI ssid is null or ");
			return;
		}
		if (SSID.equalsIgnoreCase(mSSID) && isConnecting) {
			Log.d(TAG, "same ssid is  connecting!");
			operations.disPlayWifiConResult(false, null);
			return;
		}
		if (!checkConnectHotIsEnable(SSID, wifiList)) {
			Log.d(TAG, "ssid is not in the wifiList!");
			operations.disPlayWifiConResult(false, null);
			return;
		}
		if (!wifiIsOpen()) {
			Log.d(TAG, "!wifi is not open");
			registerWifiStateBroadcast(SSID);
			wifiStateReceiver.setOpType(OpretionsType.CONNECT);
			openWifi();
		} else {
			Log.d(TAG, "connectToHotPot else");//
			enableNetwork(SSID, password);
		}
	}

	public void setConnectStatu(boolean connecting) {
		this.isConnecting = connecting;
	}

	public boolean checkConnectHotIsEnable(String wifiName, List<ScanResult> wifiList) {

		for (ScanResult result : wifiList) {
			if (result.SSID.contains(wifiName)) {
				return true;
			}
		}
		return false;
	}

	public void enableNetwork(final String SSID, final String password) {
		deleteMoreCon(SSID);
		Log.i(TAG, "into enableNetwork(WifiConfiguration wifiConfig)");
		new Thread(new Runnable() {
			@Override
			public void run() {
				WifiConfiguration config = WifiHotConfigAdmin.createWifiNoPassInfo(SSID, password);
				//WifiConfiguration config = WifiHotConfigAdmin.createWifiWpaInfo(SSID, password);
				isConnecting = connectHotSpot(config);
				registerWifiConnectBroadCast();
				mSSID = SSID;
				//while (!isConnecting) {
				if (!isConnecting) {
					Log.i(TAG, "into enableNetwork(WifiConfiguration wifiConfig) isConnecting =" + isConnecting);
					operations.disPlayWifiConResult(false, null);
					return;
				}
			}
		}).start();
		Log.i(TAG, "out enableNetwork(WifiConfiguration wifiConfig)");
	}

	private boolean connectHotSpot(WifiConfiguration wifiConfig) {
		Log.i(TAG, "into enableNetwork(WifiConfiguration wifiConfig)");
		int wcgID = mWifimanager.addNetwork(wifiConfig);
		Log.i(TAG, "into enableNetwork(WifiConfiguration wifiConfig) wcID = " + wcgID);
		if (wcgID < 0) {
			Log.i(TAG, "into enableNetwork(WifiConfiguration wifiConfig) addNetWork fail!");
			operations.disPlayWifiConResult(false, null);
			return false;
		}
		boolean flag = mWifimanager.enableNetwork(wcgID, true);
		Log.i(TAG, "out enableNetwork(WifiConfiguration wifiConfig)");
		return flag;
	}

	public void startAWifiHot(String wifiName) {
		Log.i(TAG, "into startAWifiHot(String wifiName) wifiName =" + wifiName);
		if (mWifimanager.isWifiEnabled()) {
			mWifimanager.setWifiEnabled(false);
		}
		if (wifiApadmin != null) {
			wifiApadmin.startWifiAp(wifiName);
		}
		Log.i(TAG, "out startAWifiHot(String wifiName)");
	}

	public void closeAWifiHot() {
		Log.i(TAG, "into closeAWifiHot()");
		if (wifiApadmin != null) {
			wifiApadmin.closeWifiAp();
		}
		Log.i(TAG, "out closeAWifiHot()");
	}

	private void scanNearWifiHots() {
		Log.i(TAG, "into scanNearWifiHots()");
		registerWifiScanBroadCast();
		mWifimanager.startScan();
		Log.i(TAG, "out scanNearWifiHots()");
	}

	public void openWifi() {
		Log.i(TAG, "into OpenWifi()");
		if (mWifimanager == null) {
			mWifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		}
		if (!mWifimanager.isWifiEnabled()) {
			mWifimanager.setWifiEnabled(true);
		}
		Log.i(TAG, "out OpenWifi()");
	}

	private void registerWifiStateBroadcast(String SSID) {
		IntentFilter filter = new IntentFilter();
		if (wifiStateReceiver == null) {
			wifiStateReceiver = new WifiStateBroadCast(operations, SSID);
		}
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		context.registerReceiver(wifiStateReceiver, filter);
	}

	private void registerWifiScanBroadCast() {
		IntentFilter filter = new IntentFilter();
		if (wifiScanReceiver == null) {
			wifiScanReceiver = new WifiScanRsultBroadCast(operations);

		}
		filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		context.registerReceiver(wifiScanReceiver, filter);
	}

	private void registerWifiConnectBroadCast() {
		if (wifiConnectReceiver == null) {
			Log.i(TAG, "wifiConnectReceiver is null");
			wifiConnectReceiver = new WifiConnectBroadCast(operations);
		}
		Log.i(TAG, "wifiConnectReceiver is not null");
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		context.registerReceiver(wifiConnectReceiver, filter);
	}

	public void unRegisterWifiStateBroadCast() {
		if (wifiStateReceiver != null) {
			context.unregisterReceiver(wifiStateReceiver);
			wifiStateReceiver = null;
		}
	}

	public void unRegisterWifiScanBroadCast() {
		if (wifiScanReceiver != null) {
			context.unregisterReceiver(wifiScanReceiver);
			wifiScanReceiver = null;
		}
	}

	public void unRegisterWifiConnectBroadCast() {
		if (wifiConnectReceiver != null) {
			context.unregisterReceiver(wifiConnectReceiver);
			wifiConnectReceiver = null;
		}
	}

	public void deleteMoreCon(String SSID) {
		Log.i(TAG, "into deleteMoreCon(String SSID) SSID= " + SSID);
		String destStr = "\"" + SSID + "\"";
		Log.i(TAG, "connectConfig  SSID= " + destStr);
		List<WifiConfiguration> existingConfigs = mWifimanager.getConfiguredNetworks();
		if (existingConfigs == null) {
			return;
		}
		for (WifiConfiguration existingConfig : existingConfigs) {
			Log.i(TAG, "existingConfig SSID = " + existingConfig.SSID);
			if (existingConfig.SSID.equalsIgnoreCase(destStr)) {
				Log.i(TAG, "existingConfig contain SSID = " + existingConfig.SSID);
				mWifimanager.disableNetwork(existingConfig.networkId);
				mWifimanager.removeNetwork(existingConfig.networkId);
			}
		}
		mWifimanager.saveConfiguration();
		Log.i(TAG, "out deleteMoreCon(String SSID) SSID= " + SSID);

	}

	public void disableWifiHot() {
		wifiApadmin.closeWifiAp();
	}

	public void disconnectWifi(String SSID) {
		// mWifimanager.disconnect();
	}
}
