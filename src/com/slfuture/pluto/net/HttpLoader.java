package com.slfuture.pluto.net;

import android.util.Log;

import com.slfuture.carrie.base.async.PipeLine;
import com.slfuture.carrie.base.async.core.IOperation;
import com.slfuture.carrie.base.type.core.ITable;
import com.slfuture.pluto.net.future.Future;

/**
 * HTTP加载类
 */
public class HttpLoader {
	/**
	 * HTTP操作
	 */
	public class HttpOperation implements IOperation<Void> {
		/**
		 * 地址
		 */
		public String url;
		/**
		 * 参数
		 */
		public ITable<String, Object> parameters = null;
		/**
		 * 选项对象
		 */
		public Option option;
		/**
		 * 回调对象
		 */
		public Future future = null;
		
		
		@Override
		public Void onExecute() {
			HttpUtil.send(url, parameters, option, future);
			return null;
		}
	}
	
	
	/**
	 * 流水线
	 */
	private PipeLine pipeLine = null;
	
	
	/**
	 * 初始化
	 * 
	 * @param operatorMaxCount 线程数
	 */
	public boolean initialize(int operatorMaxCount) {
		pipeLine = new PipeLine();
		return pipeLine.start(operatorMaxCount, Integer.MAX_VALUE);
	}

	/**
	 * 析构
	 */
	public void terminate() {
		try {
			pipeLine.stop();
		}
		catch (InterruptedException e) {
			Log.e("pluto", "call PipeLine.stop() failed", e);
		}
		pipeLine = null;
	}

	/**
	 * 投递请求
	 * 
	 * @param url 地址
	 * @param parameters POST参数
	 * @param option 选项
	 * @param future 回调对象，null表示阻塞调用
	 * @return 投递结果
	 */
	public String send(String url, ITable<String, Object> parameters, Option option, Future future) {
		if(null == future) {
			return HttpUtil.send(url, parameters, option, future);
		}
		HttpOperation operation = new HttpOperation();
		operation.url = url;
		operation.parameters = parameters;
		operation.option = option;
		operation.future = future;
		try {
			pipeLine.supply(operation);
		}
		catch (InterruptedException e) {
			Log.e("pluto", "call PipeLine.supply(?) failed", e);
		}
		return null;
	}
}
