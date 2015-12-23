package com.slfuture.pluto.sensor;

import java.io.Serializable;

/**
 * 位置
 */
public class Location implements Serializable {
	private static final long serialVersionUID = -3073019358918297267L;

	/**
	 * 纬度
	 */
	public double latitude;
	/**
	 * 经度
	 */
	public double longitude;
}
