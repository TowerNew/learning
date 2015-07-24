package com.slfuture.pluto.net.future;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;

/**
 * 文本回调类
 */
public class TextFuture extends Future {
	/**
	 * 下载的文本
	 */
	public String text = null;
	
	
	/**
	 * 执行下载
	 * 
	 * @param stream 输入流
	 */
	@Override
	public void download(InputStream stream) {
		this.setStatus(STATUS_DOWNLOADING);
		//
		try {
			text = convert(stream);
		}
		catch (IOException ex) {
			Log.e("pluto", "call TextFuture.convert(?) failed", ex);
			this.setStatus(STATUS_ERROR);
			return;
		}
        this.setStatus(STATUS_COMPLETED);
	}

	/**
	 * 输入流转字符串
	 *
	 * @param stream 输入流
	 * @return 字符串
	 */
	public static String convert(InputStream stream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		StringBuilder builder = new StringBuilder();
		try {
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
		}
		catch (IOException ex) {
			throw ex;
		}
		finally {
			try {
				if(null != reader) {
					reader.close();
				}
			}
			catch (Exception e) {
				Log.e("pluto", "call BufferedReader.close() failed", e);
			}
		}
		return builder.toString();
	}
}
