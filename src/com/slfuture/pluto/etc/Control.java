package com.slfuture.pluto.etc;

import android.os.Handler;

/**
 * 工具类
 */
public class Control {
	/**
	 * 主机句柄
	 */
	private static final ThreadLocal<Handler> hostHandlers = new ThreadLocal<Handler>();
	
	
	/**
	 * 执行延时命令
	 * 
	 * @param runable 可执行代码
	 * @param delay 延时毫秒数
	 */
	public static void doDelay(Runnable runnable, long delay) {
		Handler handler = hostHandlers.get();
		if(null == handler) {
			handler = new Handler();
			hostHandlers.set(handler);
		}
		handler.postDelayed(runnable, delay);
	}
}
