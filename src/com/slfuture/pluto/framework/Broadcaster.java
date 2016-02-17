package com.slfuture.pluto.framework;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.slfuture.pluto.framework.core.IListener;

/**
 * 消息广播器
 */
public class Broadcaster {
	/**
	 * 获取回调对象
	 */
	@SuppressWarnings("unchecked")
	public static <T extends IListener> T broadcast(Class<T> clazz) {
		return (T) Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getClass().getInterfaces(), 
	        new InvocationHandler() {
				@Override
				public Object invoke(Object proxy, Method method,
						Object[] args) throws Throwable {
					// TODO Auto-generated method stub
					return null;
				}
			});
	}
}
