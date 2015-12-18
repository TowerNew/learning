package com.slfuture.pluto.sensor;

import java.util.List;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * 位置处理类
 */
public class Position {
	/**
	 * 位置监听
	 */
	private static class LocationListenerX implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
		    if(null == location) {
		    	if(null != listener) {
			    	manager.removeUpdates(this);
			    	listener = null;
			    }
		    	return;
		    }
			currentLatitude = location.getLatitude();
			currentLongitude = location.getLongitude();
		    if(null != listener) {
		    	manager.removeUpdates(this);
		    	listener = null;
		    }
		}
		
		@Override
		public void onProviderDisabled(String provider) {}
		
		@Override
		public void onProviderEnabled(String provider) {}
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	}
	
	
	/**
	 */
	private static final double EARTH_RADIUS = 6378137.0;
	/**
	 * 位置监听器
	 */
	private static LocationListenerX listener = null;
	/**
	 * 位置管理器
	 */
	private static LocationManager manager = null;
	/**
	 * 当前经纬度
	 */
	private static double currentLatitude = 0f, currentLongitude = 0f;
	
	
	/**
	 * 获取距离的描述字符串
	 * 
	 * @param distance 距离
	 * @return 字符串
	 */
	public static String makeDistanceDescription(double distance) {
		if(distance > 1000) {
			return String.valueOf((long)(distance / 1000)) + "千米";
		}
		else {
			return String.valueOf((long)(distance)) + "米";
		}
	}

	/**
	 * 获取距离的描述字符串
	 * 
	 * @param latitude 纬度
	 * @param longitude 经度
	 * @return 字符串
	 */
	public static String makeDistanceDescription(double latitude, double longitude) {
		double distance = getDistance(latitude, longitude, currentLatitude, currentLongitude);
		return makeDistanceDescription(distance);
	}
	
	/**
	 * 返回两点之间的距离
	 * 
	 * @param latitude 纬度
	 * @param longitude 经度
	 * @return 距离（米）
	 */
	public static double getDistance(double latitude, double longitude) {
		return getDistance(latitude, longitude, currentLatitude, currentLongitude);
	}

	/**
	 * 初始化
	 * 
	 * @param context 上下文
	 * @return 执行结果
	 */
	public static boolean initialize(Context context) {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(false);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		String provider = manager.getBestProvider(criteria, true);
		List<String> accessibleProviders = manager.getProviders(true);
		if(provider != null || accessibleProviders.size() > 0) {
		    if(null != listener) {
		    	listener = new LocationListenerX();
				manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 100 ,listener);
		    }
		    else {
		    	listener = new LocationListenerX();
				manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 100 ,listener);
		    }
		    return true;
		}
		Location lnet = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if(null == lnet) {
			return false;
		}
		currentLongitude = lnet.getLatitude();
		currentLatitude = lnet.getLongitude();
		return true;
	}

	/**
	 * 返回两点之间的距离
	 * 
	 * @param latitude1
	 * @param longitude1
	 * @param latitude2
	 * @param longitude2
	 * @return 距离（米）
	 */
	public static double getDistance(double latitude1, double longitude1, double latitude2, double longitude2) {  
		double Lat1 = rad(latitude1);
		double Lat2 = rad(latitude2);
		double a = Lat1 - Lat2;
		double b = rad(longitude1) - rad(longitude2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(Lat1) * Math.cos(Lat2) * Math.pow(Math.sin(b / 2), 2)));  
		s = s * EARTH_RADIUS;  
		s = Math.round(s * 10000) / 10000;  
		return s;  
	}

	/**
	 * 获取曲率
	 */
	private static double rad(double d) {  
		return d * Math.PI / 180.0;  
	}
}
