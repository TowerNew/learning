package com.slfuture.pluto.communication.response;

import android.graphics.Bitmap;

/**
 * 图片反馈
 */
public abstract class ImageResponse extends CommonResponse<Bitmap> {
	/**
	 * 图片地址
	 */
	protected String url = null;
	/**
	 * 附属信息
	 */
	public Object tag = null;

	/**
	 * 构造函数
	 * 
	 * @param url 图片码
	 */
	public ImageResponse(String url) {
		this.url = url;
	}

	/**
	 * 构造函数
	 * 
	 * @param url 图片码
	 * @param tag 附属信息
	 */
	public ImageResponse(String url, Object tag) {
		this.url = url;
		this.tag = tag;
	}
	
	/**
	 * 获取文件名称
	 * 
	 * @return 文件名称
	 */
	public String fileName() {
		if(null == url) {
			return null;
		}
		return url.substring(1 + url.lastIndexOf("/"));
	}
}
