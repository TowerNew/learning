package com.slfuture.pluto.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.slfuture.carrie.base.type.Set;
import com.slfuture.carrie.base.type.core.ICollection;
import com.slfuture.pluto.config.core.IConfig;
import com.slfuture.pluto.config.core.IRootConfig;
import com.slfuture.pluto.config.xml.RootConfig;

import android.app.Application;
import android.util.Log;

/**
 * 配置模块类
 */
public class Configuration {
	/**
	 * 配置模块根配置
	 */
	public static class ConfigurationConfig extends RootConfig {
	    /**
	     * 遍历指定路径的节点
	     *
	     * @param path 路径
	     * @return 指定路径下的节点，不存在返回null
	     */
	    @Override
	    public IConfig visit(String path) {
	    	if(null == path || path.equals("")) {
	            return this;
	        }
	        String head = null;
	        String tail = null;
	        if(path.startsWith(CONFIG_PATH_SEPARATOR)) {
	        	return visit(path.substring(1));
	        }
	        // 取出本次分析的段
	        int i = path.indexOf(CONFIG_PATH_SEPARATOR);
	        if(-1 == i) {
	            head = path;
	        }
	        else {
	            head = path.substring(0, i) ;
	            tail = path.substring(i + 1);
	        }
	        IConfig conf = null;
	        if(null == children.get(head)) {
	        	conf = attachConfig(head);
	        	if(null == conf) {
	        		return null;
	        	}
	        	children.put(head, new Set<IConfig>(conf));
	        }
	        else {
	        	conf = children.get(head).offer();
	        }
	        return conf.visit(tail);
	    }

	    /**
	     * 遍历指定路径的节点集
	     *
	     * @param path 路径
	     * @return 指定路径下的节点集，不存在返回空集合
	     */
	    @Override
	    public ICollection<IConfig> visits(String path) {
	    	if(null == path || path.equals("")) {
	            return new Set<IConfig>(this);
	        }
	        String head = null;
	        String tail = null;
	        if(path.startsWith(CONFIG_PATH_SEPARATOR)) {
	        	return visits(path.substring(1));
	        }
	        // 取出本次分析的段
	        int i = path.indexOf(CONFIG_PATH_SEPARATOR);
	        if(-1 == i) {
	            head = path;
	        }
	        else {
	            head = path.substring(0, i) ;
	            tail = path.substring(i + 1);
	        }
	        IConfig conf = null;
	        if(null == children.get(head)) {
	        	conf = attachConfig(head);
	        	if(null == conf) {
	        		return new Set<IConfig>();
	        	}
	        	children.put(head, new Set<IConfig>(conf));
	        }
	        else {
	        	conf = children.get(head).offer();
	        }
	        return conf.visits(tail);
	    }
	}


	/**
	 * 应用实例
	 */
	private static Application application = null;
	/**
	 * 根配置节点
	 */
	private static ConfigurationConfig root = null;


	/**
	 * 初始化配置系统
	 * 
	 * @param application 应用实例
	 * @return 执行结果
	 */
	public static boolean initialize(Application application) {
		root = new ConfigurationConfig();
		Configuration.application = application;
		return true;
	}

	/**
	 * 提取指定名称的配置对象
	 * 
	 * @param name 配置名称
	 * @return 配置对象
	 */
	private static IRootConfig attachConfig(String name) {
		StringBuffer buffer = new StringBuffer();
    	try {
    		InputStream inputStream = application.getAssets().open(name + ".xml");
    		BufferedReader bufferReader = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
	    	while ((line = bufferReader.readLine()) != null) {
	    		buffer.append(line);
			}
	    	bufferReader.close();
        	inputStream.close();
    	}
    	catch(Exception ex) {
    		try {
				throw ex;
			}
    		catch (Exception e) {
    			Log.e(application.getApplicationInfo().name, "", e);
			}
    	}
    	RootConfig conf = new RootConfig();
    	if(!conf.load(buffer.toString())) {
    		return null;
    	}
    	if(!root.attach(name, conf)) {
    		return null;
    	}
    	return conf;
	}

	/**
	 * 获取根节点
	 * 
	 * @return 根节点
	 */
	public static IConfig root() {
		return Configuration.root;
	}

	/**
	 * 析构
	 */
	public static void terminate() {
		application = null;
	}
}
