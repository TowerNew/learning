package com.slfuture.pluto.etc;

/**
 * 带参数的可执行
 */
public abstract class ParameterRunnable implements Runnable {
	/**
	 * 参数
	 */
	protected Object parameter = null;


	/**
	 * 构造函数
	 * 
	 * @param parameter 参数
	 */
	public ParameterRunnable(Object parameter) {
		this.parameter = parameter;
	}
}
