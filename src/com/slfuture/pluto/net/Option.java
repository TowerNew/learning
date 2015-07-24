package com.slfuture.pluto.net;

/**
 * 选项类
 */
public class Option {
	/**
	 * 等待超时时间：永远
	 */
	public final static int TIMEOUT_FOREVER = 0;


	/**
	 * 超时时长
	 */
	public int timeout;
	
	
	/**
	 * 构造函数
	 */
	public Option() { }	
	
	/**
	 * 构造函数
	 * 
	 * @param timeout 超时时长
	 */
	public Option(int timeout) {
		this.timeout = timeout;
	}
}
