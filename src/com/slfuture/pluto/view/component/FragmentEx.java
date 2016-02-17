package com.slfuture.pluto.view.component;

import java.lang.reflect.Field;

import com.slfuture.pluto.view.annotation.ResourceView;

import android.app.Fragment;
import android.os.Bundle;
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
	 * 是否附着到根视图
	 * 
	 * @return 是否附着到根视图
	 */
	protected boolean attachToRoot() {
		return true;
	}
}
