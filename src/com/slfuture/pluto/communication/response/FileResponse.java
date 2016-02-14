package com.slfuture.pluto.communication.response;

import java.io.File;

import com.slfuture.carrie.base.etc.Serial;

/**
 * 文件反馈
 */
public abstract class FileResponse extends CommonResponse<File> {
	/**
	 * 图片地址
	 */
	protected String url = null;
	/**
	 * 存储路径
	 */
	protected String path = null;
	/**
	 * 附属信息
	 */
	public Object tag = null;


	/**
	 * 构造函数
	 * 
	 * @param url 文件地址
	 * @param tag 附属信息
	 */
	public FileResponse(String url, Object tag) {
		this.url = url;
		this.tag = tag;
	}
	
	/**
	 * 构造函数
	 * 
	 * @param url 文件地址
	 * @param path 本地缓存路径
	 * @param tag 附属信息
	 */
	public FileResponse(String url, String path, Object tag) {
		this.url = url;
		this.path = path;
		this.tag = tag;
	}

	/**
	 * 获取文件名称
	 * 
	 * @return 文件名称
	 */
	/**
	 * 获取文件名称
	 * 
	 * @return 文件名称
	 */
	public String fileName() {
		if(null != path) {
			int i = path.lastIndexOf(File.separator);
			if(-1 == i) {
				return path;
			}
			return path.substring(i + 1);
		}
		if(null != url) {
			String md5 = Serial.getMD5String(url).toLowerCase();
			int i = url.lastIndexOf(".");
			if(-1 == i) {
				return md5;
			}
			String suffix = url.substring(i).toLowerCase();
			if(suffix.length() > 5) {
				return md5;
			}
			return md5 + suffix;
		}
		return null;
	}

	/**
	 * 获取文件对象
	 * 
	 * @return 文件对象
	 */
	public File file() {
		if(null == path) {
			return null;
		}
		return new File(path);
	}
}
