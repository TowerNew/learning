package com.slfuture.pluto.view.component;

import java.lang.reflect.Field;

import com.slfuture.pluto.view.annotation.ResourceView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * 界面拓展
 */
public class ActivityEx extends Activity {
	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Activity
		ResourceView activityView = this.getClass().getAnnotation(ResourceView.class);
		if(null != activityView) {
			this.setContentView(activityView.id());
		}
		// Control
		for(Field field : this.getClass().getFields()) {
			ResourceView controlView = field.getAnnotation(ResourceView.class);
			if(null == controlView) {
				continue;
			}
			try {
				field.set(this, this.findViewById(controlView.id()));
			}
			catch (IllegalAccessException e) {
				Log.e("pluto", "ActivityEx.onCreate() failed", e);
			}
			catch (IllegalArgumentException e) {
				Log.e("pluto", "ActivityEx.onCreate() failed", e);
			}
		}
	}
}
