package com.slfuture.pluto.config.core;

import com.slfuture.carrie.base.type.core.ICollection;
import com.slfuture.carrie.base.type.core.IMixedMapping;

/**
 * 配置对象接口
 */
public interface IConfig extends IMixedMapping<String, String> {
    /**
     * 通配符
     */
    public final static String CONFIG_NAME_WILDCARD = "*";
    /**
     * XML节点路径分隔符
     */
    public final static String CONFIG_PATH_SEPARATOR = "/";
    /**
     * XML节点路径中节点集索引左括号
     */
    public final static String CONFIG_PATH_SBL = "[";
    /**
     * XML节点路径中节点集索引右括号
     */
    public final static String CONFIG_PATH_SBR = "]";


    /**
     * 遍历指定路径的节点
     *
     * @param path 路径
     * @return 指定路径下的节点，不存在返回null
     */
    public IConfig visit(String path);

    /**
     * 遍历指定路径的节点集
     *
     * @param path 路径
     * @return 指定路径下的节点集，不存在返回空集合
     */
    public ICollection<IConfig> visits(String path);
}
