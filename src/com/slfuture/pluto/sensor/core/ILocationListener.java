package com.slfuture.pluto.sensor.core;

import com.slfuture.pluto.sensor.Location;

/**
 * 位置监听器
 */
public interface ILocationListener {
	/**
	 * 监听结果回调
	 * 
	 * @param location 最新位置
	 */
	public void onListen(Location location);
}
