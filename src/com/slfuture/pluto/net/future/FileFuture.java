package com.slfuture.pluto.net.future;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * 文件回调类
 */
public class FileFuture extends Future {
	/**
	 *  下载块大小
	 */
	private static final int SIZE_DOWNLOAD = 1024 * 10;
	
	/**
	 * 下载的文本
	 */
	public File file = null;


	/**
	 * 执行下载
	 * 
	 * @param stream 输入流
	 */
	@Override
	public void download(InputStream stream) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		OutputStream output = null;
        try {
        	output = new FileOutputStream(file);
        	byte buffer[] = new byte[SIZE_DOWNLOAD];
            int size = 0;
            while((size = stream.read(buffer)) != -1) {
            	output.write(buffer, 0, size);
            }
		}
        catch (Exception ex) {
	        this.setStatus(STATUS_ERROR);
		}
        finally {
        	try {
            	reader.close();
            	output.close();
			}
			catch (Exception e) { }
        }
        this.setStatus(STATUS_COMPLETED);
	}
}
