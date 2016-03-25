package com.slfuture.pluto.communication;

import com.slfuture.carrie.base.type.List;
import com.slfuture.carrie.base.type.Table;
import com.slfuture.pluto.config.core.IConfig;

/**
 * 网络环境配置类
 */
public class Environment {
	/**
	 * 环境名称
	 */
	public String name;
	/**
	 * 主机列表
	 */
	public List<Host> hosts = new List<Host>();
	/**
	 * 参数列表
	 */
	public Table<String, String> parameters = new Table<String, String>();


	/**
	 * 通过名称获取主机
	 * 
	 * @param name 主机名称 
	 * @return 主机配置
	 */
	public Host fetchHost(String name) {
		if(null == name && hosts.size() > 0) {
			return hosts.get(0);
		}
		for(Host host : hosts) {
			if(host.name.equals(name)) {
				return host;
			}
		}
		return null;
	}

	/**
	 * 构建
	 * <environment name="product">
     *	<host name="server" domain="service.wehop.cn" />
     *	<host name="cdn" domain="cdn.oss.wehop-resources.wehop.cn" />
     * </environment>
	 * 
	 * @param conf 配置
	 * @return 网络环境配置类
	 */
	public static Environment build(IConfig conf) {
		Environment result = new Environment();
		result.name = conf.getString("name");
		if(null != conf.visits("host")) {
			result.hosts.clear();
			for(IConfig item : conf.visits("host")) {
				result.hosts.add(Host.build(item));
			}
		}
		if(null != conf.visits("parameter")) {
			result.parameters.clear();
			for(IConfig item : conf.visits("parameter")) {
				result.parameters.put(item.getString("name"), item.getString("value"));
			}
		}
		return result;
	}
}
