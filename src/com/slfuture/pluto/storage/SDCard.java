package com.slfuture.pluto.storage;

import android.os.Environment;

/**
 * 存储卡管理类
 */
public class SDCard {
	/**
	 * 隐藏构造函数
	 */
	private SDCard() { }

	/**
	 * 获取存储卡根路径
	 * 
	 * @return 存储卡根路径
	 */
	public static String root() {
		return Environment.getExternalStorageDirectory().getPath() + "/";
	}
}
