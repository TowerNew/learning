package com.slfuture.pluto.communication;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.slfuture.carrie.base.etc.Serial;
import com.slfuture.carrie.base.text.Text;
import com.slfuture.carrie.base.type.safe.Table;
import com.slfuture.pluto.communication.response.CommonResponse;
import com.slfuture.pluto.communication.response.FileResponse;
import com.slfuture.pluto.communication.response.ImageResponse;
import com.slfuture.pluto.communication.response.Response;
import com.slfuture.pluto.communication.response.core.IResponse;
import com.slfuture.pluto.config.Configuration;
import com.slfuture.pluto.config.core.IConfig;
import com.slfuture.pluto.net.HttpLoader;
import com.slfuture.pluto.net.future.FileFuture;
import com.slfuture.pluto.net.future.TextFuture;
import com.slfuture.pluto.storage.SDCard;

/**
 * 通信服务器
 */
public class Host {
	/**
	 * 主机交互包类
	 */
	public static class HostBundle {
		/**
		 * 目标类
		 */
		public Class<?> clazz = null;
		/**
		 * 内容
		 */
		public Object content = null;
		/**
		 * 反馈对象
		 */
		public Response response = null;
		/**
		 * 句柄
		 */
		public HostHandler handler = null;
	}


	/**
	 * 主机文本回调
	 */
	public static class HostTextFuture extends TextFuture {
		/**
		 * 键
		 */
		public int key = 0;
		
		
		/**
		 * 构造函数
		 * 
		 * @param commandResponse 回执
		 * @param handler 句柄
		 */
		public HostTextFuture(CommonResponse<String> commandResponse, HostHandler handler) {
			key = Serial.makeLoopInteger();
			HostBundle textBundle = new HostBundle();
			textBundle.clazz = String.class;
			textBundle.response = commandResponse;
			textBundle.handler = handler;
			hostBundles.put(key, textBundle);
		}

		/**
		 * 设置状态
		 * 
		 * @param status 新状态
		 */
		@Override
		public void setStatus(int status) {
			super.setStatus(status);
			Message message = new Message();
			message.what = key;
			if(STATUS_COMPLETED == status) {
				HostBundle textBundle = hostBundles.get(key);
				textBundle.response.setCode(IResponse.CODE_SUCCESS);
				textBundle.content = this.text;
				textBundle.handler.sendMessage(message);
			}
			else if(STATUS_TIMEOUT == status) {
				HostBundle textBundle = hostBundles.get(key);
				textBundle.response.setCode(IResponse.CODE_TIMEOUT);
				textBundle.handler.sendMessage(message);
			}
			else if(STATUS_ERROR == status) {
				HostBundle textBundle = hostBundles.get(key);
				textBundle.response.setCode(IResponse.CODE_ERROR);
				textBundle.handler.sendMessage(message);
			}
		}
	}


	/**
	 * 主机文件回调
	 */
	public static class HostFileFuture extends FileFuture {
		/**
		 * 键
		 */
		public int key = 0;
		
		
		/**
		 * 构造函数
		 * 
		 * @param commandResponse 回执
		 * @param handler 句柄
		 * @param file 文件对象
		 * @param clazz 目标类
		 */
		public HostFileFuture(CommonResponse<?> commandResponse, HostHandler handler, File file, Class<?> clazz) {
			key = Serial.makeLoopInteger();
			HostBundle fileBundle = new HostBundle();
			fileBundle.response = commandResponse;
			fileBundle.handler = handler;
			this.file = file;
			fileBundle.clazz = clazz;
			hostBundles.put(key, fileBundle);
		}

		/**
		 * 设置状态
		 * 
		 * @param status 新状态
		 */
		@Override
		public void setStatus(int status) {
			super.setStatus(status);
			Message message = new Message();
			message.what = key;
			if(STATUS_COMPLETED == status) {
				HostBundle fileBundle = hostBundles.get(key);
				fileBundle.response.setCode(IResponse.CODE_SUCCESS);
				String path = this.file.getAbsolutePath();
				path = path.substring(0, path.lastIndexOf("."));
				File file = new File(path);
				if(file.exists()) {
					file.delete();
				}
				this.file.renameTo(file);
				this.file = new File(path);
				if(fileBundle.clazz.equals(Bitmap.class)) {
					fileBundle.content = BitmapFactory.decodeFile(this.file.getAbsolutePath());
				}
				else {
					fileBundle.content = this.file;
				}
				fileBundle.handler.sendMessage(message);
			}
			else if(STATUS_TIMEOUT == status) {
				HostBundle fileBundle = hostBundles.get(key);
				fileBundle.response.setCode(IResponse.CODE_TIMEOUT);
				fileBundle.handler.sendMessage(message);
			}
			else if(STATUS_ERROR == status) {
				HostBundle fileBundle = hostBundles.get(key);
				fileBundle.response.setCode(IResponse.CODE_ERROR);
				fileBundle.handler.sendMessage(message);
			}
		}
	}

	/**
	 * 通信回调线程通知句柄
	 */
	public static class HostHandler extends Handler {
		/**
		 * 处理消息
		 * 
		 * @param msg 消息对象
		 */
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			Log.i("Pluto", "HostHandler HOST MESSAGE");
			HostBundle bundle = hostBundles.get(msg.what);
			hostBundles.delete(msg.what);
			if(null == bundle.content) {
				((CommonResponse<?>)(bundle.response)).onFinished(null);
			}
			else {
				if(bundle.clazz.equals(String.class)) {
					((CommonResponse<String>)(bundle.response)).onFinished((String)bundle.content);
				}
				else if(bundle.clazz.equals(File.class)) {
					((CommonResponse<File>)(bundle.response)).onFinished((File) bundle.content);
				}
				else if(bundle.clazz.equals(Bitmap.class)) {
					((CommonResponse<Bitmap>)(bundle.response)).onFinished((Bitmap) bundle.content);
				}
			}
			super.handleMessage(msg);
		}
	}


	/**
	 * 消息结束
	 */
	public final static int MESSAGE_FINISHED = 9527;
	/**
	 * 命令信道
	 */
	private static HttpLoader command = null;
	/**
	 * 物料信道
	 */
	private static HttpLoader material = null;
	/**
	 * 协议映射
	 */
	private static Table<String, Protocol> protocols = new Table<String, Protocol>();
	/**
	 * 域名
	 */
	public static String domain = null;
	/**
	 * 缓存目录
	 */
	public static String storage = null;
	/**
	 * 当前是否正在模拟
	 */
	public static boolean isMock = false;
	/**
	 * 主机句柄
	 */
	private static final ThreadLocal<HostHandler> hostHandlers = new ThreadLocal<HostHandler>();
	/**
	 * 回调包映射
	 */
	private static Table<Integer, HostBundle> hostBundles = new Table<Integer, HostBundle>();


	/**
	 * 初始化
	 * 
	 * @return 执行结果
	 */
	public static boolean initialize() {
		domain = Configuration.root().visit("/protocols").get("domain");
		storage = SDCard.root() + Configuration.root().visit("/protocols").get("storage");
		if("true".equalsIgnoreCase(Configuration.root().visit("/protocols").get("mock"))) {
			isMock = true;
		}
		else {
			isMock = false;
		}
		protocols.clear();
		for(IConfig conf : Configuration.root().visits("/protocols/protocol")) {
			String urlTemplate = null;
			if(null != conf.visit("url")) {
				urlTemplate = Text.trim(conf.visit("url").get(null));
			}
			else if(null != conf.visit("path")) {
				urlTemplate = "http://" + domain + "/" + Text.trim(conf.visit("path").get(null));
			}
			if(null != conf.visit("mock") && null != conf.visit("mock").get(null)) {
				String mock = Text.trim(conf.visit("mock").get(null));
				protocols.put(conf.get("name"), Protocol.build(urlTemplate, mock));
			}
			else {
				protocols.put(conf.get("name"), Protocol.build(urlTemplate, null));
			}
		}
		return true;
	}

	/**
	 * 销毁
	 */
	public static void terminate() {
		if(null != command) {
			command.terminate();
			command = null;
		}
		if(null != material) {
			material.terminate();
			material = null;
		}
		protocols.clear();
	}

	/**
	 * 获取有效命令信道
	 * 
	 * @return 命令信道
	 */
	public static HttpLoader command() {
		if(null == command) {
			synchronized(Host.class) {
				if(null == command) {
					command = new HttpLoader();
					command.initialize(2);
				}
			}
		}
		return command;
	}

	/**
	 * 获取有效物料信道
	 * 
	 * @return 物料信道
	 */
	public static HttpLoader material() {
		if(null == material) {
			synchronized(Host.class) {
				if(null == material) {
					material = new HttpLoader();
					material.initialize(5);
				}
			}
		}
		return material;
	}
	
	/**
	 * 获取协议URL
	 * 
	 * @param protocol 协议名称
	 * @param parameters 参数列表
	 * @return URL
	 */
	public static String fetchURL(String protocol, Object... parameters) {
		return protocols.get(protocol).buildURL(parameters);
	}

	/**
	 * 执行网络命令
	 * 
	 * @param protocol 协议名称
	 * @param commandResponse 回调
	 * @param parameters 参数列表
	 */
	public static void doCommand(String protocol, CommonResponse<String> commandResponse, Object... parameters) {
		if(isMock) {
			commandResponse.setCode(Response.CODE_SUCCESS);
			commandResponse.onFinished(protocols.get(protocol).mock);
			return;
		}
		HostHandler hostHandler = hostHandlers.get();
		if(null == hostHandler) {
			hostHandler = new HostHandler();
			hostHandlers.set(hostHandler);
		}
		protocols.get(protocol).invoke(command(), new HostTextFuture(commandResponse, hostHandler), parameters);
	}
	
	/**
	 * 执行图片下载命令
	 * 
	 * @param protocol 协议名称
	 * @param imageResponse 图片回调
	 * @param parameters 参数列表
	 */
	public static void doFile(String protocol, FileResponse fileResponse, Object... parameters) {
		String filePath = storage + fileResponse.fileName();
		if(null != fileResponse.file()) {
			filePath = fileResponse.file().getAbsolutePath();
		}
		File file = new File(filePath);
		if(file.exists()) {
			fileResponse.setCode(Response.CODE_SUCCESS);
			fileResponse.onFinished(new File(filePath));
			return;
		}
		HostHandler hostHandler = hostHandlers.get();
		if(null == hostHandler) {
			hostHandler = new HostHandler();
			hostHandlers.set(hostHandler);
		}
		String path = filePath + "." + Serial.makeLoopInteger();
		protocols.get(protocol).invoke(material(), new HostFileFuture(fileResponse, hostHandler, new File(path), File.class), parameters);
	}

	/**
	 * 执行图片下载命令
	 * 
	 * @param protocol 协议名称
	 * @param imageResponse 图片回调
	 * @param parameters 参数列表
	 */
	public static void doImage(String protocol, ImageResponse imageResponse, Object... parameters) {
		String filePath = storage + imageResponse.fileName();
		File file = new File(filePath);
		if(file.exists()) {
			imageResponse.setCode(Response.CODE_SUCCESS);
			imageResponse.onFinished(BitmapFactory.decodeFile(filePath));
			return;
		}
		HostHandler hostHandler = hostHandlers.get();
		if(null == hostHandler) {
			hostHandler = new HostHandler();
			hostHandlers.set(hostHandler);
		}
		String path = filePath + "." + Serial.makeLoopInteger();
		protocols.get(protocol).invoke(material(), new HostFileFuture(imageResponse, hostHandler, new File(path), Bitmap.class), parameters);
	}
}
