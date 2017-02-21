package com.shuyu.core.db;

import java.util.Iterator;
import java.util.List;

/**
 * 数据库操作接口
 *
 * @param <T> 数据类型
 */
interface IDaoHelper<T> {

    long addData(T bean);

    void addDataAll(Iterable<T> entities);

    boolean hasKey(String id);

    T getDataById(String id);

    long getTotalCount();

    List<T> getDataAll();

    void deleteData(String id);

    void deleteAll();

    void update(T bean);

    void updateAll(Iterator<T> entities);
}