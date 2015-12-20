package com.slfuture.pluto.net;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import android.util.Log;

import com.slfuture.carrie.base.type.core.ILink;
import com.slfuture.carrie.base.type.core.ITable;
import com.slfuture.pluto.net.future.Future;
import com.slfuture.pluto.net.future.TextFuture;

/**
 * HTTP工具类
 */
public class HttpUtil {
	/**
	 * 分割符
	 */
	private static final String HTTP_BOUNDARY_STRING = "-------------------205f2a74205f2a74";
	/**
	 *  下载块大小
	 */
	private static final int SIZE_UPLOAD = 1024 * 10;
	/**
	 *  下载块大小
	 */
	private static final int METHOD_GET = 1;
	/**
	 *  下载块大小
	 */
	private static final int METHOD_POST = 2;


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
		// 连接
		HttpURLConnection connection = null;
		if(null != future) {
			future.setStatus(Future.STATUS_CONNECTING);
		}
		try {
			int method = 0;
			if(null == parameters) {
				method = METHOD_GET;
			}
			else {
				method = METHOD_POST;
			}
			int timeout = 0;
			if(null != option) {
				timeout = option.timeout;
			}
			connection = connect(url, method, timeout);
		}
		catch(SocketTimeoutException ex) {
			if(null != future) {
				future.setStatus(Future.STATUS_TIMEOUT);
			}
			return null;
		}
		// 上传
		if(null != parameters && parameters.size() > 0) {
			if(null != future) {
				future.setStatus(Future.STATUS_UPLOADING);
			}
			try {
				upload(connection.getOutputStream(), parameters);
			}
			catch(IOException ex) {
				Log.e("pluto", "call HttpUtil.upload(?, ?)", ex);
				if(null != future) {
					future.setStatus(Future.STATUS_ERROR);
				}
				return null;
			}
		}
		try {
			if(200 != connection.getResponseCode()) {
				connection = null;
			}
		}
		catch (IOException e) {
			Log.e("pluto", "call connection.getResponseCode() failed", e);
		}
		if(null == connection) {
			if(null != future) {
				future.setStatus(Future.STATUS_ERROR);
			}
			return null;
		}
		if(null != future) {
			future.setTotal(connection.getContentLength());
		}
		// 下载
		if(null == future) {
			try {
				return TextFuture.convert(connection.getInputStream());
			}
			catch(Exception ex) {
				Log.e("pluto", "call TextFuture.convert(?) failed", ex);
			}
			return null;
		}
		else {
			InputStream stream = null;
			try {
				stream = connection.getInputStream();
			}
			catch(Exception ex) {
				Log.e("pluto", "call TextFuture.convert(?) failed", ex);
				future.setStatus(Future.STATUS_ERROR);
				return null;
			}
			future.download(stream);
			return null;
		}
	}

	/**
	 * 连接
	 *
	 * @param url 地址
	 * @param timeout 连接超时时间
	 * @return 成功返回连接对象，失败返回null
	 */
	private static HttpURLConnection connect(String url, int method, int timeout) throws SocketTimeoutException {
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) (new URL(url)).openConnection();
			connection.setUseCaches(false);
			if (timeout > 0) {
				connection.setConnectTimeout(timeout);
			}
			connection.setDoInput(true);
			if (METHOD_POST == method) {
				connection.setDoOutput(true);
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + HTTP_BOUNDARY_STRING);
				connection.connect();
			} 
		}
		catch (SocketTimeoutException e) {
			throw e;
		}
		catch(IOException e) {
			Log.e("pluto", "HttpUtil.connect('" + url + "', " + method + ", " + timeout + ")", e);
			return null;
		}
		return connection;
	}

	/**
	 * 上传数据
	 *
	 * @param stream 输出流
	 * @param parameters POST参数集
	 */
	private static void upload(OutputStream stream, ITable<String, Object> parameters) throws IOException {
		DataOutputStream outStream = null;
		try {
			outStream = new DataOutputStream(stream);
			for (ILink<String, Object> link : parameters) {
				if(null == link.destination()) {
					continue;
				}
				if(link.destination() instanceof File) {
					continue;
				}
				outStream.writeBytes("--" + HTTP_BOUNDARY_STRING + "\r\n");
				outStream.writeBytes("Content-Disposition: form-data; name=\"" + link.origin() + "\"\r\n\r\n");
				outStream.write(link.destination().toString().getBytes("UTF-8"));
				outStream.writeBytes("\r\n");
			}
			for (ILink<String, Object> link : parameters) {
				if(null == link.destination()) {
					continue;
				}
				if(!(link.destination() instanceof File)) {
					continue;
				}
				File file = (File) link.destination();
				outStream.writeBytes("--" + HTTP_BOUNDARY_STRING + "\r\n");
				outStream.writeBytes("Content-Disposition: form-data; name=\"" + link.origin() + "\"; filename=\"" + file.getName() + "\"\r\n");
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
			outStream.writeBytes("--" + HTTP_BOUNDARY_STRING + "--\r\n\r\n");
		}
		catch (IOException ex) {
			throw ex;
		}
		finally {
			try {
				if(null != outStream) {
					outStream.close();
				}
			}
			catch (Exception e) {
				Log.e("pluto", "call DataOutputStream.close() failed", e);
			}
		}
	}
}
