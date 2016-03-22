package com.slfuture.pluto.communication.response;

import java.io.File;

import com.slfuture.pluto.communication.Networking;

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
	 * 下载文件路径
	 */
	protected File file = null;
	/**
	 * 附属信息
	 */
	public Object tag = null;
	/**
	 * 图片压缩
	 */
	public int width = 0;
	public int height = 0;


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
	 * @param width 宽度
	 * @param height 高度
	 */
	public ImageResponse(String url, int width, int height) {
		this.url = url;
		this.width = width;
		this.height = height;
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
	 * 构造函数
	 * 
	 * @param url 图片码
	 * @param tag 附属信息
	 */
	public ImageResponse(String url, Object tag, int width, int height) {
		this.url = url;
		this.tag = tag;
		this.width = width;
		this.height = height;
	}

	/**
	 * 构造函数
	 * 
	 * @param file 下载文件
	 */
	public ImageResponse(File file) {
		this.file = file;
	}

	/**
	 * 构造函数
	 * 
	 * @param file 下载文件
	 * @param tag 附属信息
	 */
	public ImageResponse(File file, Object tag) {
		this.file = file;
		this.tag = tag;
	}

	/**
	 * 获取文件名称
	 * 
	 * @return 文件名称
	 */
	public String fileName() {
		if(null != file) {
			return file.getName();
		}
		if(null != url) {
			return Networking.parseFileNameWithURL(url);
		}
		return null;
	}
}
