package com.slfuture.pluto.sensor;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;

/**
 * 提醒器
 */
public class Reminder {
	/**
	 * 
	 */
	private Reminder() { }

	/**
	 * 震动
	 * 
	 * @param context 上下文
	 */
	public static void vibrate(Context context) {
		Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(new long[] {0, 180, 80, 120}, -1);
	}

	/**
	 * 单次响铃
	 * 
	 * @param context 上下文
	 */
	public static void ringtone(Context context) {
        Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final Ringtone ringtone = RingtoneManager.getRingtone(context, notificationUri);
        if(null == ringtone) {
            return;
        }
        if(!ringtone.isPlaying()) {
            String vendor = Build.MANUFACTURER;
            ringtone.play();
            if(vendor != null && vendor.toLowerCase().contains("samsung")) {
                Thread thread = new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(3000);
                            if(ringtone.isPlaying()) {
                            	ringtone.stop();
                            }
                        }
                        catch (Exception e) {
                        	Log.e("pluto", "stop ringtone on samsung failed", e);
                        }
                    }
                };
                thread.run();
            }
        }
	}
}
