package com.example.puzzle.network.wifi.pack;

public class MyMessage {
	private String netAddress;
	private String msg;
	private String deviceName;
	private String msgTime;
	private String type;

	private Object bigMsg;
	
	public Object getBigMsg() {
		return bigMsg;
	}
	
	public void setBigMsg(Object o) {
		bigMsg = o;
	}
	
	public String getMsgTime() {
		return msgTime;
	}

	public void setMsgTime(String msgTime) {
		this.msgTime = msgTime;
	}

	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getNetAddress() {

		return netAddress;
	}

	public void setNetAddress(String netAddress) {

		this.netAddress = netAddress;
	}

	public String getMsg() {

		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;

	}
	
	@Override
	public String toString() {
		return "deviceName " + deviceName + " netAddress =" + netAddress + "  msg =" + msg + " msgTime=" + msgTime;
	}
}
