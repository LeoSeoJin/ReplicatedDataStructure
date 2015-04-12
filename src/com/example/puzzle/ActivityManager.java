package com.example.puzzle;

import java.util.LinkedList; 
import java.util.List; 
import android.app.Activity; 
import android.app.Application; 

public class ActivityManager extends Application {
    private List<Activity> mList = new LinkedList<Activity>();
    private static ActivityManager instance; 

    private ActivityManager(){}

    public synchronized static ActivityManager getInstance(){ 
        if (null == instance) { 
            instance = new ActivityManager(); 
        } 
        return instance; 
    } 
 
    public void addActivity(Activity activity) { 
        mList.add(activity); 
    } 

    public void exit() { 
        try { 
            for (Activity activity:mList) { 
            	System.out.println("********************activity num "+mList.size());
                if (activity != null) {
                	if (activity.getClass().equals(WifiHotActivity.class)) {
                		System.out.println("***********activity is wifihotactivity");
                		((WifiHotActivity)activity).getWifiHotManager().unRegisterWifiScanBroadCast();
                		((WifiHotActivity)activity).getWifiHotManager().unRegisterWifiStateBroadCast();
                		((WifiHotActivity)activity).getWifiHotManager().disableWifiHot();
                	}
                	activity.finish(); 
                }
                   
            } 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally { 
            System.exit(0); 
        } 
    } 

    public void onLowMemory() { 
        super.onLowMemory();     
        System.gc(); 
    }  
}
 
