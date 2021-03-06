package com.slfuture.pluto.communication;

import java.io.File;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.slfuture.carrie.base.etc.Serial;
import com.slfuture.carrie.base.model.core.ITargetEventable;
import com.slfuture.carrie.base.text.Text;
import com.slfuture.carrie.base.type.List;
import com.slfuture.carrie.base.type.safe.Table;
import com.slfuture.pluto.communication.response.CommonResponse;
import com.slfuture.pluto.communication.response.FileResponse;
import com.slfuture.pluto.communication.response.ImageResponse;
import com.slfuture.pluto.communication.response.Response;
import com.slfuture.pluto.communication.response.core.IResponse;
import com.slfuture.pluto.config.Configuration;
import com.slfuture.pluto.config.core.IConfig;
import com.slfuture.pluto.etc.GraphicsHelper;
import com.slfuture.pluto.net.HttpLoader;
import com.slfuture.pluto.net.future.FileFuture;
import com.slfuture.pluto.net.future.TextFuture;
import com.slfuture.pluto.storage.SDCard;

/**
 * 通信服务器
 */
public class Networking {
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
					if(null != fileBundle.response && fileBundle.response instanceof ImageResponse) {
						ImageResponse response = (ImageResponse) fileBundle.response;
						if(response.width > 0 && response.height > 0) {
							fileBundle.content = GraphicsHelper.decodeFile(new File(this.file.getAbsolutePath()), response.width, response.height);
						}
					}
					if(null == fileBundle.content) {
						fileBundle.content = BitmapFactory.decodeFile(this.file.getAbsolutePath());
					}
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
			Log.i("pluto", "HostHandler HOST MESSAGE");
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
	 * 缓存目录
	 */
	public static String cache = null;
	/**
	 * 当前是否正在模拟
	 */
	public static boolean mock = false;
	/**
	 * 主机句柄
	 */
	private static final ThreadLocal<HostHandler> hostHandlers = new ThreadLocal<HostHandler>();
	/**
	 * 回调包映射
	 */
	private static Table<Integer, HostBundle> hostBundles = new Table<Integer, HostBundle>();
	/**
	 * 环境列表
	 */
	public static List<Environment> environments = new List<Environment>();
	/**
	 * 当前环境
	 */
	public static Environment currentEnvironment = null;


	/**
	 * 初始化
	 * 
	 * @param context 程序上下文
	 * @return 执行结果
	 */
	public static boolean initialize(Context context) {
		cache = SDCard.root() + Configuration.root().visit("/network").get("cache");
		File directory = new File(cache);
		if(!directory.exists()) {
			directory.mkdirs();
		}
		SharedPreferences sharedPreferences = context.getSharedPreferences("pluto", Activity.MODE_PRIVATE); 
		mock = sharedPreferences.getBoolean("mock", false);
		// 解析环境
		environments.clear();
		currentEnvironment = null;
		String environmentName = sharedPreferences.getString("environment", null);
		for(IConfig conf : Configuration.root().visits("/network/environment")) {
			Environment environment = Environment.build(conf);
			environments.add(environment);
			if(null != environmentName && environment.name.equals(environmentName)) {
				currentEnvironment = environment;
			}
		}
		if(null == currentEnvironment) {
			currentEnvironment = environments.get(0);
		}
		// 解析协议
		protocols.clear();
		for(IConfig conf : Configuration.root().visits("/network/protocol")) {
			String urlTemplate = null;
			if(null != conf.visit("url")) {
				urlTemplate = Text.trim(conf.visit("url").get(null));
			}
			else {
				urlTemplate = "http://" + currentEnvironment.fetchHost(conf.visit("path").getString("host")).domain + "/" + Text.trim(conf.visit("path").get(null));
			}
			if(null != conf.visit("mock") && null != conf.visit("mock").get(null)) {
				String mock = Text.trim(conf.visit("mock").get(null));
				protocols.put(conf.get("name"), Protocol.build(urlTemplate, mock));
			}
			else {
				protocols.put(conf.get("name"), Protocol.build(urlTemplate, null));
			}
		}
		protocols.put("", Protocol.build("[1]", null));
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
		environments.clear();
	}

	/**
	 * 获取有效命令信道
	 * 
	 * @return 命令信道
	 */
	public static HttpLoader command() {
		if(null == command) {
			synchronized(Networking.class) {
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
			synchronized(Networking.class) {
				if(null == material) {
					material = new HttpLoader();
					material.initialize(5);
				}
			}
		}
		return material;
	}

	/**
	 * 获取当前环境下指定名称的配置
	 * 
	 * @param name 协议名称
	 * @return URL
	 */
	public static String fetchParameter(String name) {
		if(null == currentEnvironment) {
			return null;
		}
		return currentEnvironment.parameters.get(name);
	}

	/**
	 * 获取协议URL
	 * 
	 * @param protocol 协议名称
	 * @param parameters 参数列表
	 * @return URL
	 */
	public static String fetchURL(String protocol, Object... parameters) {
		Protocol p = protocols.get(protocol);
		if(null == p) {
			return null;
		}
		return p.buildURL(parameters);
	}

	/**
	 * 获取mock的返回值
	 * 
	 * @param protocol 协议名称
	 * @return mock的返回值
	 */
	public static String fetchMock(String protocol) {
		Protocol p = protocols.get(protocol);
		if(null == p) {
			return null;
		}
		return p.mock;
	}

	/**
	 * 执行网络命令
	 * 
	 * @param protocol 协议名称
	 * @param commandResponse 回调
	 * @param parameters 参数列表
	 */
	public static void doCommand(String protocol, CommonResponse<String> commandResponse, Object... parameters) {
		if(mock) {
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
		String filePath = cache + fileResponse.fileName();
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
		String filePath = cache + imageResponse.fileName();
		File file = new File(filePath);
		if(file.exists()) {
			imageResponse.setCode(Response.CODE_SUCCESS);
			if(imageResponse.width > 0 && imageResponse.height > 0) {
				imageResponse.onFinished(GraphicsHelper.decodeFile(new File(filePath), imageResponse.width, imageResponse.height));
			}
			else {
				imageResponse.onFinished(BitmapFactory.decodeFile(filePath));
			}
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

	/**
	 * 执行图片下载命令
	 * 
	 * @param protocol 协议名称
	 * @param target 回调参数目标
	 * @param event 回调
	 * @param parameters 参数列表
	 */
	public static <T>void doImage(String protocol, T target, ITargetEventable<T, Bitmap> event, Object... parameters) {
		String url = protocols.get(protocol).buildURL(parameters);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("target", target);
		map.put("event", event);
		Networking.doImage(protocol, new ImageResponse(url, map) {
			@SuppressWarnings("unchecked")
			@Override
			public void onFinished(Bitmap content) {
				HashMap<String, Object> map = (HashMap<String, Object>) tag;
				T target = (T) map.get("target");
				ITargetEventable<T, Bitmap> event = (ITargetEventable<T, Bitmap>) map.get("event");
				if(null == event) {
					return;
				}
				event.on(target, content);
			}
		}, parameters);
	}

	/**
	 * 从URL中提取文件名
	 * 
	 * @param url
	 * @return 文件名
	 */
	public static String parseFileNameWithURL(String url) {
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

	/**
	 * 设置指定名称的环境
	 * 
	 * @param context 上下文
	 * @param name 环境名称
	 */
	public static boolean selectEnvironment(Context context, String name) {
		for(Environment environment : environments) {
			if(environment.name.equals(name)) {
				SharedPreferences sharedPreferences = context.getSharedPreferences("pluto", Activity.MODE_PRIVATE); 
				SharedPreferences.Editor editor = sharedPreferences.edit(); 
				editor.putString("environment", name);
				editor.commit(); 
				return initialize(context);
			}
		}
		return false;
	}

	/**
	 * 设置是否模拟网络
	 * 
	 * @param context 上下文
	 * @param mock 是否模拟
	 */
	public static boolean selectMock(Context context, boolean mock) {
		Networking.mock = mock;
		SharedPreferences sharedPreferences = context.getSharedPreferences("pluto", Activity.MODE_PRIVATE); 
		SharedPreferences.Editor editor = sharedPreferences.edit(); 
		editor.putBoolean("mock", mock);
		editor.commit(); 
		return true;
	}
}
