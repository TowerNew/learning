package com.slfuture.pluto.etc;

import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.carrie.base.type.safe.Table;

import android.os.Handler;

/**
 * 工具类
 */
public class Controller {
	/**
	 * 回调类
	 */
	private static class CallBack {
		/**
		 * 句柄
		 */
		public Handler handler = null;
		/**
		 * 回调
		 */
		public IEventable<?> eventable = null;
	}


	/**
	 * 主机句柄
	 */
	private static final ThreadLocal<Handler> hostHandlers = new ThreadLocal<Handler>();
	/**
	 * 回调映射
	 */
	private static Table<Integer, CallBack> callbacks = new Table<Integer, CallBack>();


	/**
	 * 执行延时命令
	 * 
	 * @param runable 可执行代码
	 * @param delay 延时毫秒数
	 */
	public static void doDelay(Runnable runnable, long delay) {
		Handler handler = fetchHandler();
		handler.postDelayed(runnable, delay);
	}

	/**
	 * 执行延时命令
	 * 
	 * @param runable 可执行代码
	 * @param delay 延时毫秒数
	 */
	public static void doDelay(ParameterRunnable runnable, long delay) {
		Handler handler = fetchHandler();
		handler.postDelayed(runnable, delay);
	}

	/**
	 * 执行协同命令
	 * 
	 * @param commandId 命令ID
	 * @param callback 回调函数
	 */
	public static <T>void doJoin(int commandId, IEventable<T> callback) {
		CallBack value = new CallBack();
		value.handler = fetchHandler();
		value.eventable = callback;
		callbacks.put(commandId, value);
	}

	/**
	 * 执行协同命令
	 * 
	 * @param commandId 命令ID
	 * @param callback 执行结果
	 */
	@SuppressWarnings("unchecked")
	public static <T>void doMerge(int commandId, T result) {
		CallBack value = callbacks.delete(commandId);
		if(null == value) {
			return;
		}
		final T fResult = result;
		final IEventable<T> fEventable = (IEventable<T>) value.eventable;
		value.handler.post(new Runnable() {
			@Override
			public void run() {
				fEventable.on((T)fResult);
			}
		});
	}
	
	/**
	 * 执行协同命令
	 * 
	 * @param commandId 命令ID
	 * @param callback 执行结果
	 */
	@SuppressWarnings("unchecked")
	public static <T>void doFork(int commandId, T result) {
		CallBack value = callbacks.get(commandId);
		if(null == value) {
			return;
		}
		final T fResult = result;
		final IEventable<T> fEventable = (IEventable<T>) value.eventable;
		value.handler.post(new Runnable() {
			@Override
			public void run() {
				fEventable.on((T)fResult);
			}
		});
	}

	/**
	 * 抽取句柄
	 * 
	 * @return 句柄对象
	 */
	public static Handler fetchHandler() {
		Handler handler = hostHandlers.get();
		if(null == handler) {
			handler = new Handler();
			hostHandlers.set(handler);
		}
		return handler;
	}
}
