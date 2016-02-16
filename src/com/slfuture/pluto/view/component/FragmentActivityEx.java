package com.slfuture.pluto.view.component;

import java.lang.reflect.Field;

import com.slfuture.pluto.view.annotation.ResourceView;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * 界面拓展
 */
public class FragmentActivityEx extends FragmentActivity {
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
	}
}
