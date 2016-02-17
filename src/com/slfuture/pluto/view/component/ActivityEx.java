package com.slfuture.pluto.view.component;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.slfuture.pluto.framework.annotation.ListenerInterface;
import com.slfuture.pluto.view.annotation.ResourceView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

/**
 * 界面拓展
 */
public class ActivityEx extends Activity {
	/**
	 * 监听器
	 */
	private BroadcastReceiver listener = null;
	

	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ResourceView activityView = this.getClass().getAnnotation(ResourceView.class);
		if(null != activityView) {
			if(0 != activityView.id()) {
				this.setContentView(activityView.id());
			}
			else if(!"".equals(activityView.field())) {
				try {
					this.setContentView((Integer) activityView.clazz().getField(activityView.field()).get(null));
				}
				catch (NoSuchFieldException e) {
					throw new RuntimeException("set " + activityView.clazz() + "." + activityView.field() + " failed", e);
				}
				catch (IllegalAccessException e) {
					throw new RuntimeException("set " + activityView.clazz() + "." + activityView.field() + " failed", e);
				}
				catch (IllegalArgumentException e) {
					throw new RuntimeException("set " + activityView.clazz() + "." + activityView.field() + " failed", e);
				}
			}
		}
		for(Field field : this.getClass().getFields()) {
			ResourceView controlView = field.getAnnotation(ResourceView.class);
			if(null == controlView) {
				continue;
			}
			try {
				if(0 != controlView.id()) {
					field.set(this, this.findViewById(controlView.id()));
				}
				else if(!"".equals(controlView.field())) {
					field.set(this, this.findViewById((Integer) controlView.clazz().getField(controlView.field()).get(null)));
				}
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
			registerReceiver(listener, intentFilter); 
		}
	}

	/**
	 * 界面销毁
	 */
	@Override
    protected void onDestroy() {
		if(null != listener) {
			this.unregisterReceiver(listener);
			listener = null;
		}
		super.onDestroy();
    }
}
