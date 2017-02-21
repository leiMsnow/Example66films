package com.shuyu.core.db;

import android.text.TextUtils;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.Iterator;
import java.util.List;

/**
 * 数据库操作类
 * Created by zhangleilei on 15/7/30.
 */
public abstract class BaseDaoHelper<D extends AbstractDao, T> implements IDaoHelper<T> {

    protected D tableDao;

    protected abstract T getDataInfoById(String id);

    protected abstract boolean hasKeyById(String id);

    protected BaseDaoHelper(D dao) {
        try {
            tableDao = dao;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public long addData(T bean) {
        if (checkDaoNotNull() && bean != null) {
            return tableDao.insertOrReplace(bean);
        }
        return -1;
    }

    @Override
    public void addDataAll(Iterable<T> entities) {
        if (checkDaoNotNull() && entities != null) {
            tableDao.insertOrReplaceInTx(entities);
        }
    }

    @Override
    public void deleteData(String id) {
        if (checkDaoNotNull() && !TextUtils.isEmpty(id)) {
            tableDao.deleteByKey(id);
        }
    }

    @Override
    public T getDataById(String id) {
        if (checkDaoNotNull() && !TextUtils.isEmpty(id)) {
            return getDataInfoById(id);
        }
        return null;
    }

    @Override
    public List<T> getDataAll() {
        if (checkDaoNotNull()) {
            return tableDao.loadAll();
        }
        return null;
    }

    @Override
    public boolean hasKey(String id) {
        return !TextUtils.isEmpty(id) && checkDaoNotNull() && hasKeyById(id);
    }

    @Override
    public long getTotalCount() {
        if (checkDaoNotNull()) {
            QueryBuilder<T> qb = tableDao.queryBuilder();
            return qb.buildCount().count();
        }
        return 0;
    }

    @Override
    public void deleteAll() {
        if (checkDaoNotNull()) {
            tableDao.deleteAll();
        }
    }

    @Override
    public void update(T bean) {
        if (checkDaoNotNull() && bean != null) {
            tableDao.update(bean);
        }
    }

    @Override
    public void updateAll(Iterator<T> entities) {
        if (checkDaoNotNull() && entities != null) {
            tableDao.updateInTx(entities);
        }
    }

    protected boolean checkDaoNotNull() {
        return tableDao != null;
    }
}
