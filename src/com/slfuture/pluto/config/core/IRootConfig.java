package com.slfuture.pluto.config.core;

/**
 * 根配置接口
 */
public interface IRootConfig extends IConfig {
    /**
     * 加载根配置
     *
     * @param text 内容
     * @return 是否执行成功
     */
    public boolean load(String text);

    /**
     * 附加配置节点
     *
     * @param path 节点路径
     * @param conf 配置节点
     */
    public boolean attach(String path, IConfig conf);
}
