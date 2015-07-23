package com.slfuture.pluto.net;

import com.slfuture.carrie.base.async.PipeLine;

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
	 * 工作流
	 */
	public PipeLine pipeLine = null;
}
