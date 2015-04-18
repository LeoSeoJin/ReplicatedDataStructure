package com.example.puzzle.network.wifi.pack;

public class Global {

	public static int WIFI_CONNECTING = 0;

	public static int WIFI_CONNECTED = 1;

	public static int WIFI_CONNECT_FAILED = 2;

	public static String SSID = "YRCCONNECTION";

	public static String PASSWORD = "2012110312123";

	public static int PORT = 8080;
	
	public static int WIFICIPHER_NOPASS = 1;

	public static int WIFICIPHER_WEP = 2;

	public static int WIFICIPHER_WPA = 3;

	public static String INT_SERVER_FAIL = "INTSERVER_FAIL";

	public static String INT_SERVER_SUCCESS = "INTSERVER_SUCCESS";

	public static String INT_CLIENT_FAIL = "INTCLIENT_FAIL";

	public static String INT_CLIENT_SUCCESS = "INTCLIENT_SUCCESS";

	public static String CONNECT_SUCESS = "connect_success";

	public static String CONNECT_FAIL = "connect_fail";

	public static final int IPMSG_SNEDCLIENTDATA = 0x00000050; 

	public static final int IPMSG_SENDALLCLIENTS = 0x00000051; 
																

	public static final int IPMSG_SENDROTARYDATA = 0x00000060; 

	public static final int IPMSG_SENDROTARYRESULT = 0x00000061; 

	public static final int IPMSG_SENDCHANGECONTROLLER = 0x00000062;

	public static final int IPMSG_REQUESTCHANGECONTROLLER = 0x00000062;

}
