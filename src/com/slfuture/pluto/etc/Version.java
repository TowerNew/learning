package com.slfuture.pluto.etc;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * 版本号
 */
public class Version implements Comparable<Version> {
	/**
	 * 主版本号
	 */
	private int major;
	/**
	 * 次版本号
	 */
	private int minor;
	/**
	 * 修订号
	 */
	private int revision;


	/**
	 * 属性
	 */
	public int major() {
		return this.major;
	}
	public int minor() {
		return this.minor;
	}
	public int revision() {
		return this.revision;
	}

	/**
	 * 构建版本号
	 * 
	 * @param version 版本号
	 * @return 版本对象
	 */
	public static Version build(String version) {
		Version result = new Version();
		int i = version.indexOf(".");
		if(-1 == i) {
			return result;
		}
		result.major = Integer.parseInt(version.substring(0, i));
		i++;
		int j = version.indexOf(".", i);
		if(-1 == j) {
			result.minor = Integer.parseInt(version.substring(i));
			return result;
		}
		result.minor = Integer.parseInt(version.substring(i, j));
		result.revision = Integer.parseInt(version.substring(j + 1));
		return result;
	}

	/**
	 * 比较
	 * 
	 * @param another 被对比对象
	 * @return 比较结果
	 */
	@Override
	public int compareTo(Version another) {
		if(major > another.major()) {
			return 1;
		}
		else if(major < another.major()) {
			return -1;
		}
		if(minor > another.minor()) {
			return 1;
		}
		else if(minor < another.minor()) {
			return -1;
		}
		if(revision > another.revision()) {
			return 1;
		}
		else if(revision < another.revision()) {
			return -1;
		}
		return 0;
	}

	/**
	 * 转换为字符串
	 * 
	 * @return 字符串
	 */
	@Override
	public String toString() {
		return major + "." + minor + "." + revision;
	}
	
	/**
	 * 获取系统版本号
	 * 
	 * @return 获取系统版本号
	 */
	public static Version fetchVersion(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			String version = info.versionName;
			return build(version);
		}
		catch (Exception e) {
			Log.e("pluto", "fetchVersion(?) failed", e);
			return null;
		}
	}
}
