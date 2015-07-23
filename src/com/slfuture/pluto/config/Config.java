package com.slfuture.pluto.config;

import com.slfuture.carrie.base.model.Path;
import com.slfuture.carrie.base.type.Set;
import com.slfuture.carrie.base.type.StringMixedMapping;
import com.slfuture.carrie.base.type.Table;
import com.slfuture.carrie.base.type.core.ICollection;
import com.slfuture.carrie.base.type.core.ISet;
import com.slfuture.carrie.base.type.core.ITable;

import com.slfuture.pluto.config.core.IConfig;

/**
 * XML配置对象类
 */
public class Config extends StringMixedMapping<String> implements IConfig {
    /**
     * 属性映射
     */
    public ITable<String, String> properties = new Table<String, String>();
    /**
     * 子节点映射
     */
    public ITable<String, ISet<IConfig>> children = new Table<String, ISet<IConfig>>();
    /**
     * 父节点
     */
    public IConfig parent = null;


    /**
     * 获取指定键的值
     *
     * @param name 键名称
     * @return 值
     */
    @Override
    public String get(String name) {
        return properties.get(name);
    }

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
        // 取出本次分析的段
        int i = path.indexOf(CONFIG_PATH_SEPARATOR);
        if(-1 == i) {
            head = path;
        }
        else {
            head = path.substring(0, i) ;
            tail = path.substring(i + 1);
        }
        // 向上遍历
        if(Path.PATH_PARENT.equals(head)) {
            if(null == tail) {
                return parent;
            }
            else {
                // 迭代遍历
                return parent.visit(tail);
            }
        }
        // 尝试取出子节点索引
        String name = null;
        int index = 0;
        i = head.indexOf(CONFIG_PATH_SBL);
        if(-1 == i) {
            name = head;
        }
        else {
            int j = head.indexOf(CONFIG_PATH_SBR, i);
            if(-1 == j) {
                name = head;
            }
            else {
                name = head.substring(0, i) ;
                index = Integer.valueOf(head.substring(i + 1, j));
            }
        }
        // 提取子节点
        ISet<IConfig> nodeSet = children.get(name);
        if(null == nodeSet) {
            // 子节点名称查找失败
            return null;
        }
        i = 0;
        for(IConfig node : nodeSet) {
            if(index == i) {
                if(null == tail) {
                    return node;
                }
                else {
                    // 迭代遍历
                    return node.visit(tail);
                }
            }
            i++;
        }
        // 子节点索引查找失败
        return null;
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
        // 取出本次分析的段
        int i = path.indexOf(CONFIG_PATH_SEPARATOR);
        if(-1 == i) {
            head = path;
        }
        else {
            head = path.substring(0, i) ;
            tail = path.substring(i + 1);
        }
        // 向上遍历
        if(Path.PATH_PARENT.equals(head)) {
            if(null == tail) {
                return new Set<IConfig>(parent);
            }
            else {
                // 迭代遍历
                return parent.visits(tail);
            }
        }
        // 尝试取出子节点索引
        String name = null;
        int index = -1;
        i = head.indexOf(CONFIG_PATH_SBL);
        if(-1 == i) {
            name = head;
        }
        else {
            int j = head.indexOf(CONFIG_PATH_SBR, i);
            if(-1 == j) {
                name = head;
            }
            else {
                name = head.substring(0, i) ;
                index = Integer.valueOf(head.substring(i + 1, j));
            }
        }
        // 提取子节点
        ISet<IConfig> nodeSet = children.get(name);
        if(null == nodeSet) {
            // 子节点名称查找失败
            return new Set<IConfig>();
        }
        if(null == tail) {
            // 末节点解析
            if(-1 == index) {
                return nodeSet;
            }
            else {
                i = 0;
                for(IConfig node : nodeSet) {
                    if(index == i) {
                        return new Set<IConfig>(node);
                    }
                    i++;
                }
                return new Set<IConfig>();
            }
        }
        // 需要继续迭代
        if(-1 == index) {
            index = 0;
        }
        i = 0;
        for(IConfig node : nodeSet) {
            if(index == i) {
                // 迭代遍历
                return node.visits(tail);
            }
            i++;
        }
        // 子节点索引查找失败
        return new Set<IConfig>();
    }
}
