package com.hrz.common.data.db;


/**
 * 增删改查基础操作 待扩展
 * @param <T> 数据类型
 */
public interface IDb<T> {

    T query();

    boolean modify(T target,T subtitude);

    boolean add(T t);

    boolean delete(T t);


}
