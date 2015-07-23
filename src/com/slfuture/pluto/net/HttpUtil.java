package com.slfuture.pluto.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;

import android.util.Log;

import com.slfuture.carrie.base.async.core.IOperation;
import com.slfuture.carrie.base.type.core.ILink;
import com.slfuture.carrie.base.type.core.ITable;
import com.slfuture.pluto.net.future.Future;

/**
 * HTTP工具类
 */
public class HttpUtil {
	/**
	 * 异步任务
	 */
	public static class HttpOperation implements IOperation<Void> {
		/**
		 * 请求URL
		 */
		public String url;
		/**
		 * 参数列表
		 */
		public ITable<String, Object> parameters;
		/**
		 * 选项
		 */
		public Option option;
		/**
		 * 回调对象
		 */
		public Future future;


		@Override
		public Void onExecute() {
			Option newOption = new Option();
			newOption.timeout = option.timeout;
			send(url, parameters, newOption, future);
			return null;
		}
	}


	/**
	 * 分割符
	 */
	private static final String HTTP_BOUNDARY_STRING = "-------------------205f2a74205f2a74";
	/**
	 *  下载块大小
	 */
	private static final int SIZE_UPLOAD = 1024 * 10;


	/**
	 * 隐藏构造函数
	 */
	private HttpUtil() { }
	
	/**
	 * 投递请求
	 * 
	 * @param url 地址
	 * @param parameters POST参数
	 * @param option 选项
	 * @param future 回调对象，null表示阻塞调用
	 * @return 投递结果
	 */
	public static String send(String url, ITable<String, Object> parameters, Option option, Future future) {
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) (new URL(url)).openConnection();
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			if(null != option && option.timeout > 0) {
				connection.setConnectTimeout(option.timeout);
			}
			if(null != parameters) {
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + HTTP_BOUNDARY_STRING);
			}
			try {
	        	if(null != future) {
	        		future.setStatus(Future.STATUS_CONNECTING);
	        	}
	        	connection.connect();
	        }
	        catch (SocketTimeoutException e) {
	        	if(null != future) {
	        		future.setStatus(Future.STATUS_TIMEOUT);
	        	}
	            return null;
	        }
			if(null != parameters) {
				DataOutputStream outStream = new DataOutputStream(connection.getOutputStream());  
		        for (ILink<String, Object> link : parameters) {
		        	if(!(link.destination() instanceof File)) {
		        		continue;
		        	}
		        	File file = (File) link.destination();
		        	outStream.writeBytes("--" + HTTP_BOUNDARY_STRING + "\r\n");  
		        	outStream.writeBytes("Content-Disposition: form-data; name=\"" + link.origin() + "\"; filename=\"" + URLEncoder.encode(file.getName(), "UTF-8") + "\"\r\n");  
		        	outStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
		        	// 
		        	FileInputStream fileStream = new FileInputStream(file);
		            byte[] fileBuffer = new byte[SIZE_UPLOAD];
		            int fileIndex = 0;
		            while ((fileIndex = fileStream.read(fileBuffer)) != -1) {
		            	outStream.write(fileBuffer, 0, fileIndex);
		            }
		            fileStream.close();
		        	outStream.writeBytes("\r\n");
		        }
		        for (ILink<String, Object> link : parameters) {
		        	if(link.destination() instanceof File) {
		        		continue;
		        	}
		        	outStream.writeBytes("--" + HTTP_BOUNDARY_STRING + "\r\n");
		        	outStream.writeBytes("Content-Disposition: form-data; name=\"" + link.origin() + "\"\r\n\r\n");
		        	outStream.writeBytes(URLEncoder.encode(link.destination().toString(), "UTF-8"));
		        	outStream.writeBytes("\r\n");
		        }
		        outStream.writeBytes("--" + HTTP_BOUNDARY_STRING + "--\r\n\r\n");
		        outStream.close();
			}
	        // 下载
	        if(null == future) {
	        	StringBuilder builder = new StringBuilder();
		        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		        String line = null;
		        while ((line = reader.readLine()) != null) {
		        	builder.append(line);
		        }
		        reader.close();
		        return builder.toString();
	        }
	        else {
		        future.download(connection.getInputStream());
		        return null;
	        }
		}
		catch(Exception ex) {
			Log.e("pluto", "HttpUtil.send() execute failed", ex);
			return null;
		}
		finally {
			if(null != connection) {
				connection.disconnect();
			}
		}
	}
}
