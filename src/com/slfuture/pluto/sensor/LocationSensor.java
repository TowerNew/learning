package com.slfuture.pluto.sensor;

import java.util.LinkedList;
import java.util.List;

import com.slfuture.carrie.base.async.Operator;
import com.slfuture.carrie.base.async.core.IOperation;
import com.slfuture.pluto.sensor.core.ILocationListener;

import android.content.Context;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * 位置传感器
 */
public class LocationSensor {
	/**
	 * 操作
	 */
	private static abstract class Operation<T> implements IOperation<T> {
		/**
		 * 监听器
		 */
		protected ILocationListener listener = null;
		/**
		 * 等待时长
		 */
		protected long timeout = 0;


		/**
		 * 构造函数
		 * 
		 * @param listener 监听器
		 * @param timeout 等待时长
		 */
		public Operation(ILocationListener listener, long timeout) {
			this.listener = listener;
			this.timeout = timeout;
		}
	}


	/**
	 * 位置监听
	 */
	private static class LocationListenerEx implements LocationListener {
		/**
		 * 位置变动回调
		 * 
		 * @param location 当前位置
		 */
		@Override
		public void onLocationChanged(android.location.Location location) {
			if(null != location) {
				Location currentLocation = new Location();
				currentLocation.latitude = location.getLatitude();
				currentLocation.longitude = location.getLongitude();
				//
				LinkedList<ILocationListener> template = listeners;
				listeners = new LinkedList<ILocationListener>();
				for(ILocationListener listener : template) {
					listener.onListen(currentLocation);
				}
			}
			manager.removeUpdates(this);
			LocationSensor.listener = null;
		}

		@Override
		public void onProviderDisabled(String provider) {}
		
		@Override
		public void onProviderEnabled(String provider) {}
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	}


	/**
	 * 位置管理器
	 */
	private static LocationManager manager = null;
	/**
	 * 当前监听器集合
	 */
	private static LinkedList<ILocationListener> listeners = new LinkedList<ILocationListener>();
	/**
	 * 当前有效监听器
	 */
	private static LocationListenerEx listener = null;


	/**
	 * 隐藏构造函数
	 */
	private LocationSensor() {}


	/**
	 * 初始化
	 * 
	 * @param context 上下文
	 * @return 执行结果
	 */
	public static boolean initialize(Context context) {
		manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		return true;
	}

	/**
	 * 终止
	 */
	public static void terminate() {
		manager = null;
	}
	
	/**
	 * 刷新当前位置
	 */
	public static void refresh() {
		if(null != listener) {
			return;
		}
		listener = new LocationListenerEx();
		//
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(false);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		String provider = manager.getBestProvider(criteria, true);
		List<String> accessibleProviders = manager.getProviders(true);
		if(provider != null || accessibleProviders.size() > 0) {
		    // manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0 ,listener);
		    manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0 ,listener);
		}
		else {
			listener = null;
		}
	}

	/**
	 * 获取最后一次的位置
	 * 
	 * @return 最后一次的位置信息
	 */
	public static Location fetchLastKnownLocation() {
		android.location.Location location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if(null == location) {
			return null;
		}
		Location result = new Location();
		result.latitude = location.getLatitude();
		result.longitude = location.getLongitude();
		return result;
	}

	/**
	 * 获取当前位置
	 * 
	 * @param listener 位置监听器
	 * @param timeout 最长等待时间
	 */
	public static void fetchCurrentLocation(ILocationListener listener, long timeout) {
		new Operator<Void>(new Operation<Void>(listener, timeout) {
			@Override
			public Void onExecute() {
				try {
					Thread.sleep(timeout);
				}
				catch (InterruptedException e) {
					Log.e("Pluto", "Thread.sleep(" + timeout + ") failed in fetchCurrentLocation", e);
				}
				boolean sentry = false;
				for(ILocationListener item : listeners) {
					if(item == listener) {
						sentry = true;
						listeners.remove(listener);
						break;
					}
				}
				if(!sentry) {
					return null;
				}
				listener.onListen(fetchLastKnownLocation());
				return null;
			}
		});
		listeners.add(listener);
		refresh();
	}
}
