package com.slfuture.pluto.framework.core;

/**
 * 程序接口
 */
public interface IProgram {
	/**
	 * 获取环境对象
	 * 
	 * @return 环境对象
	 */
	public IEnvironment environment();

	/**
	 * 获取指定服务
	 * 
	 * @param clazz 服务类
	 * @return 服务对象
	 */
	public <T>T service(Class<T> clazz);
}
