package com.slfuture.pluto.framework;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

/**
 * 消息广播器
 */
public class Broadcaster {
	/**
	 * 获取回调对象
	 * 
	 * @param context 上下文
	 * @param clazz 接口类
	 */
	@SuppressWarnings("unchecked")
	public static <T> T broadcast(final Context context, final Class<T> clazz) {
		return (T) Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getClass().getInterfaces(), 
	        new InvocationHandler() {
				@Override
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					Intent intent = new Intent();
					intent.setAction(clazz.getName());
					intent.putExtra("method", new com.slfuture.carrie.base.model.Method(method.getName(), method.getParameterTypes()).toString());
					int i = -1;
					for(Object item : args) {
						i++;
						if(null == item) {
							continue;
						}
						if(item.getClass().equals(int.class) || item.getClass().equals(Integer.class)) {
							intent.putExtra("parameter" + i, (Integer) item);
						}
						else if(item.getClass().equals(float.class) || item.getClass().equals(Float.class)) {
							intent.putExtra("parameter" + i, (Float) item);
						}
						else if(item.getClass().equals(double.class) || item.getClass().equals(Double.class)) {
							intent.putExtra("parameter" + i, (Double) item);
						}
						else if(item.getClass().equals(String.class)) {
							intent.putExtra("parameter" + i, (String) item);
						}
						else if(Parcelable.class.isAssignableFrom(item.getClass())) {
							intent.putExtra("parameter" + i, (Parcelable) item);
						}
						else if(Serializable.class.isAssignableFrom(item.getClass())) {
							intent.putExtra("parameter" + i, (Serializable) item);
						}
						else {
							throw new RuntimeException("unsupport parameter type:" + item.getClass());
						}
					}
					context.sendBroadcast(intent);
					return null;
				}
			}
		);
	}
}
