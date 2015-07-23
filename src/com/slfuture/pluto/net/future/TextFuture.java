package com.slfuture.pluto.net.future;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

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
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        try {
        	String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
		}
        catch (Exception ex) {
	        this.setStatus(STATUS_ERROR);
		}
        finally {
        	try {
            	reader.close();
			}
			catch (Exception e) { }
        }
        text = builder.toString();
        this.setStatus(STATUS_COMPLETED);
	}
}
