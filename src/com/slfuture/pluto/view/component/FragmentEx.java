package com.slfuture.pluto.view.component;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.slfuture.pluto.framework.annotation.ListenerInterface;
import com.slfuture.pluto.view.annotation.ResourceView;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 界面拓展
 */
public class FragmentEx extends Fragment {
	/**
	 * 监听器
	 */
	private BroadcastReceiver listener = null;
	

	/**
	 * 界面创建
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Class<?> clazz = this.getClass();
		while(clazz.getName().contains("$")) {
			clazz = clazz.getSuperclass();
			if(null == clazz) {
				return super.onCreateView(inflater, container, savedInstanceState);
			}
		}
		com.slfuture.pluto.view.annotation.ResourceView activityView = clazz.getAnnotation(com.slfuture.pluto.view.annotation.ResourceView.class);
		if(null == activityView) {
			return super.onCreateView(inflater, container, savedInstanceState);
		}
		else {
			View result = null;
			int id = 0;
			if(0 != activityView.id()) {
				id = activityView.id();
			}
			else if(!"".equals(activityView.field())) {
				try {
					id = (Integer) activityView.clazz().getField(activityView.field()).get(null);
				}
				catch (IllegalAccessException e) {
					throw new RuntimeException("set " + activityView.clazz() + "." + activityView.field() + " failed", e);
				}
				catch (IllegalArgumentException e) {
					throw new RuntimeException("set " + activityView.clazz() + "." + activityView.field() + " failed", e);
				}
				catch (NoSuchFieldException e) {
					throw new RuntimeException("set " + activityView.clazz() + "." + activityView.field() + " failed", e);
				}
			}
			else {
				return super.onCreateView(inflater, container, savedInstanceState);
			}
			result = inflater.inflate(id, container, attachToRoot());
			for(Field field : clazz.getFields()) {
				ResourceView controlView = field.getAnnotation(ResourceView.class);
				if(null == controlView) {
					continue;
				}
				try {
					if(0 != controlView.id()) {
						id = controlView.id();
					}
					else if(!"".equals(controlView.field())) {
						id = (Integer) controlView.clazz().getField(controlView.field()).get(null);
					}
					else {
						continue;
					}
					field.set(this, result.findViewById(id));
				}
				catch (IllegalAccessException e) {
					throw new RuntimeException("set " + controlView.clazz() + "." + controlView.field() + " failed", e);
				}
				catch (IllegalArgumentException e) {
					throw new RuntimeException("set " + controlView.clazz() + "." + controlView.field() + " failed", e);
				}
				catch (NoSuchFieldException e) {
					throw new RuntimeException("set " + controlView.clazz() + "." + controlView.field() + " failed", e);
				}
			}
			return result;
		}
	}

	/**
	 * 界面创建
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		for(Class<?> clazz : this.getClass().getInterfaces()) {
			if(null == clazz.getAnnotation(ListenerInterface.class)) {
				continue;
			}
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(clazz.getName());
			listener = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					try {
						com.slfuture.carrie.base.model.Method m = com.slfuture.carrie.base.model.Method.build(intent.getStringExtra("method"));
						Method method = this.getClass().getMethod(m.name, m.parameters);
						Object[] parameters = new Object[m.parameters.length];
						for(int i = 0; i < m.parameters.length; i++) {
							if(!intent.hasExtra("parameter" + i)) {
								parameters[i] = null;
								continue;
							}
							if(m.parameters[i].equals(int.class) || m.parameters[i].equals(Integer.class)) {
								parameters[i] = intent.getIntExtra("parameter" + i, 0);
							}
							else if(m.parameters[i].equals(float.class) || m.parameters[i].equals(Float.class)) {
								parameters[i] = intent.getFloatExtra("parameter" + i, 0);
							}
							else if(m.parameters[i].equals(double.class) || m.parameters[i].equals(Double.class)) {
								parameters[i] = intent.getDoubleExtra("parameter" + i, 0);
							}
							else if(m.parameters[i].equals(String.class)) {
								parameters[i] = intent.getStringExtra("parameter" + i);
							}
							else if(Parcelable.class.isAssignableFrom(m.parameters[i])) {
								parameters[i] = intent.getParcelableExtra("parameter" + i);
							}
							else if(Serializable.class.isAssignableFrom(m.parameters[i])) {
								parameters[i] = intent.getSerializableExtra("parameter" + i);
							}
							else {
								throw new RuntimeException("unsupport parameter type:" + m.parameters[i]);
							}
						}
						method.invoke(this, parameters);
					}
					catch(Exception ex) {
						Log.e("pluto", "Broadcast onReceive() failed", ex);
					}
				}
			};
			//注册监听
			this.getActivity().registerReceiver(listener, intentFilter);
		}
    }

	/**
	 * 是否附着到根视图
	 * 
	 * @return 是否附着到根视图
	 */
	protected boolean attachToRoot() {
		return true;
	}
	
	/**
	 * 界面销毁
	 */
	@Override
    public void onDestroy() {
		if(null != listener) {
			this.getActivity().unregisterReceiver(listener);
			listener = null;
		}
		super.onDestroy();
    }
}
