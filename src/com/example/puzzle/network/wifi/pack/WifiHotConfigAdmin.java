package com.example.puzzle.network.wifi.pack;

import android.net.wifi.WifiConfiguration;
import android.util.Log;

public class WifiHotConfigAdmin {

	private static String TAG = "WifiConfigurationAdmin";

	public static WifiConfiguration createWifiNoPassInfo(String SSID, String password) {

		Log.v(TAG, "into nopass  SSID = " + SSID + "  Password = " + password + " Type = ");
		WifiConfiguration config = new WifiConfiguration();
		config = createWifiInfo(config, SSID, password);
		config.wepKeys[0] = "\"" + "" + "\"";
		config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		config.wepTxKeyIndex = 0;
		config.status = WifiConfiguration.Status.ENABLED;
		Log.v(TAG, "out nopass  SSID = " + SSID + "  Password = " + password + " Type = ");
		return config;
	}

	// Wep
	public static WifiConfiguration createWifiWepInfo(String SSID, String password) {

		Log.v(TAG, "into WIFICIPHER_WEP   SSID = " + SSID + "  Password = " + password);
		WifiConfiguration config = new WifiConfiguration();
		config = createWifiInfo(config, SSID, password);
		config.hiddenSSID = true;
		config.wepKeys[0] = "\"" + password + "\"";
		config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
		config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
		config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
		config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
		config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		config.wepTxKeyIndex = 0;
		Log.v(TAG, "out WIFICIPHER_WEP   SSID = " + SSID + "  Password = " + password);
		return config;

	}

	// WPA
	public static WifiConfiguration createWifiWpaInfo(String SSID, String password) {

		Log.v(TAG, "into WIFICIPHER_WPA   SSID = " + SSID + "  Password = " + password);
		WifiConfiguration config = new WifiConfiguration();
		config = createWifiInfo(config, SSID, password);
		config.preSharedKey = "\"" + password + "\"";
		config.hiddenSSID = true;
		config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
		config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
		config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
		// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
		config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
		config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
		config.status = WifiConfiguration.Status.ENABLED;
		Log.v(TAG, "out WIFICIPHER_WPA   SSID = " + SSID + "  Password = " + password);
		return config;

	}

	private static WifiConfiguration createWifiInfo(WifiConfiguration config, String SSID,
			String password) {

		Log.v(TAG, "into wifi SSID = " + SSID + "  Password = " + password);
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";
		config.priority = 0;
		Log.v(TAG, "into wifi config.SSID = " + config.SSID + "  Password = " + password);
		return config;
	}
}
