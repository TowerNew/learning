package com.slfuture.pluto.net.future;

import java.io.InputStream;

/**
 * 回调类
 */
public abstract class Future {
	/**
	 * 状态：就绪
	 */
	public final static int STATUS_READY = 0;
	/**
	 * 状态：连接进行
	 */
	public final static int STATUS_CONNECTING = 1;
	/**
	 * 状态：下载进行
	 */
	public final static int STATUS_DOWNLOADING = 2;
	/**
	 * 状态：超时终止
	 */
	public final static int STATUS_TIMEOUT = 3;
	/**
	 * 状态：完成终止
	 */
	public final static int STATUS_COMPLETED = 4;
	/**
	 * 状态：中断终止
	 */
	public final static int STATUS_INTERRUPTED = 5;
	/**
	 * 状态：错误终止
	 */
	public final static int STATUS_ERROR = 6;


	/**
	 * 总数
	 */
	public int total = 0;
	/**
	 * 当前进度
	 */
	public int progress = 0;
	/**
	 * 当前状态
	 */
	protected int status = STATUS_READY;


	/**
	 * 获取状态
	 * 
	 * @return 状态
	 */
	public int status() {
		return status;
	}

	/**
	 * 设置状态
	 * 
	 * @param status 新状态
	 */
	public void setStatus(int status) {
		this.status = status;
		if(STATUS_TIMEOUT == status || STATUS_COMPLETED == status || STATUS_INTERRUPTED == status || STATUS_ERROR == status) {
			synchronized(this) {
				this.notify();
			}
		}
	}

	/**
	 * 执行下载
	 * 
	 * @param stream 输入流
	 */
	public abstract void download(InputStream stream);

	/**
	 * 等待结束
	 * 
	 * @param millis 等待毫秒数
	 */
	public void await(long millis) throws InterruptedException {
		synchronized(this) {
			if(STATUS_TIMEOUT == status || STATUS_COMPLETED == status || STATUS_INTERRUPTED == status || STATUS_ERROR == status) {
				return;
			}
			this.wait(millis);
		}
	}
}
