package com.slfuture.pluto.framework.core;

import com.slfuture.pluto.etc.Version;

/**
 * 程序环境参数接口
 */
public interface IEnvironment {
	/**
	 * 获取程序版本
	 * 
	 * @return 程序版本号
	 */
	public Version version();

	/**
	 * 获取根目录
	 * 
	 * @return 根目录
	 */
	public String root();

	/**
	 * 获取图片缓存根目录
	 * 
	 * @return 图片缓存根目录
	 */
	public String imageRoot();

	/**
	 * 获取动态配置根目录
	 * 
	 * @return 动态配置根目录
	 */
	public String configRoot();

	/**
	 * 获取本地运行时数据根目录
	 * 
	 * @return 本地运行时数据根目录
	 */
	public String dataRoot();
}
