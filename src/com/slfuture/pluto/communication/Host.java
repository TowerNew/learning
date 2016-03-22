package com.slfuture.pluto.communication;

import com.slfuture.pluto.config.core.IConfig;

/**
 * 主机配置类
 */
public class Host {
	/**
	 * 主机名称
	 */
	public String name;
	/**
	 * 主机域名
	 */
	public String domain;


	/**
	 * 构造函数
	 */
	public Host() { }
	public Host(String name, String domain) {
		this.name = name;
		this.domain = domain;
	}

	/**
	 * 构建
	 * <host name="server" domain="service.wehop.cn" />
	 * 
	 * @param conf 配置
	 * @return 主机配置类
	 */
	public static Host build(IConfig conf) {
		Host result = new Host();
		result.name = conf.getString("name");
		result.domain = conf.getString("domain");
		return result;
	}
}
