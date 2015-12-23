package com.slfuture.pluto.view.component;

import java.lang.reflect.Field;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 界面拓展
 */
public class FragmentEx extends Fragment {
	/**
	 * 界面创建
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		com.slfuture.pluto.view.annotation.ResourceView activityView = this.getClass().getAnnotation(com.slfuture.pluto.view.annotation.ResourceView.class);
		if(null == activityView) {
			return super.onCreateView(inflater, container, savedInstanceState);
		}
		else {
			// Activity
			View result = inflater.inflate(activityView.id(), container, true);
			// Control
			for(Field field : this.getClass().getFields()) {
				com.slfuture.pluto.view.annotation.ResourceView controlView = field.getAnnotation(com.slfuture.pluto.view.annotation.ResourceView.class);
				if(null == controlView) {
					continue;
				}
				try {
					field.set(this, result.findViewById(controlView.id()));
				}
				catch (IllegalAccessException e) {
					Log.e("pluto", "FragmentEx.onCreate() failed", e);
				}
				catch (IllegalArgumentException e) {
					Log.e("pluto", "FragmentEx.onCreate() failed", e);
				}
			}
			return result;
		}
	}
}
