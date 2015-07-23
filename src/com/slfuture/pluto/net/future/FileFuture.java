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
		this.setStatus(STATUS_DOWNLOADING);
		//
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		OutputStream output = null;
        try {
        	output = new FileOutputStream(file);
        	byte buffer[] = new byte[SIZE_DOWNLOAD];
            int size = 0;
            while((size = stream.read(buffer)) != -1) {
            	output.write(buffer, 0, size);
				this.progress(size);
            }
		}
        catch (Exception ex) {
			Log.e("pluto", "FileFuture.download(?) execute failed", ex);
			this.setStatus(STATUS_ERROR);
			return;
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
			try {
				if(null != output) {
					output.close();
				}
			}
			catch (Exception e) {
				Log.e("pluto", "call OutputStream.close() failed", e);
			}
        }
        this.setStatus(STATUS_COMPLETED);
	}

	/**
	 * 存储输入流
	 *
	 * @param stream 输入流
	 * @param file 文件对象
	 */
	public static void save(InputStream stream, File file) throws IOException {
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
			try {
				if(null != output) {
					output.close();
				}
			}
			catch (Exception e) {
				Log.e("pluto", "call OutputStream.close() failed", e);
			}
		}
	}
}
