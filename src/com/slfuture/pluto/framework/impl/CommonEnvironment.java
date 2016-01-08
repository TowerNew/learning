package com.slfuture.pluto.framework.impl;

import java.io.File;

import com.slfuture.pluto.framework.core.IEnvironment;

/**
 * 常规环境
 */
public abstract class CommonEnvironment implements IEnvironment {
	/**
	 * 图片缓存目录名
	 */
	public final static String IMAGE_ROOT = "image";
	/**
	 * 动态配置目录名
	 */
	public final static String CONFIG_ROOT = "config";
	/**
	 * 本地运行时数据目录名
	 */
	public final static String DATA_ROOT = "data";


	/**
	 * 获取图片缓存根目录
	 * 
	 * @return 图片缓存根目录
	 */
	@Override
	public String imageRoot() {
		return root() + File.pathSeparator + IMAGE_ROOT + File.pathSeparator;
	}

	/**
	 * 获取动态配置根目录
	 * 
	 * @return 动态配置根目录
	 */
	@Override
	public String configRoot() {
		return root() + File.pathSeparator + CONFIG_ROOT + File.pathSeparator;
	}

	/**
	 * 获取本地运行时数据根目录
	 * 
	 * @return 本地运行时数据根目录
	 */
	@Override
	public String dataRoot() {
		return root() + File.pathSeparator + DATA_ROOT + File.pathSeparator;
	}
}
