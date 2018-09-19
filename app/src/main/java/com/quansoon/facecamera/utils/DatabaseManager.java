package com.quansoon.facecamera.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.quansoon.facecamera.constant.Constants;
import com.quansoon.facecamera.model.DaoMaster;
import com.quansoon.facecamera.model.DaoSession;
import com.quansoon.facecamera.model.PersonModel;
import com.quansoon.facecamera.model.PersonModelDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Caoy
 */
public class DatabaseManager {

    private static DatabaseManager sDatabaseManager;
    private DaoSession mDaoSession;
    private DaoMaster.DevOpenHelper devOpenHelper;

    public static DatabaseManager getInstance(Context context, String name) {
        if (sDatabaseManager == null) {
            synchronized (DatabaseManager.class) {
                if (sDatabaseManager == null) {
                    sDatabaseManager = new DatabaseManager(context, name);
                }
            }
        }
        return sDatabaseManager;
    }

    private DatabaseManager(Context context, String name) {
        init(context, name);
    }

    private void init(Context context, String name) {
        String databaseName = name + Constants.Database.DATABASE_NAME;
        devOpenHelper = new DaoMaster.DevOpenHelper(context, databaseName, null);
        SQLiteDatabase writableDatabase = devOpenHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(writableDatabase);
        //开启数据库的会话
        mDaoSession = daoMaster.newSession();
    }

    /**
     * 保存或者更新员工数据到数据库
     *
     * @param employeeEntity
     */
    public void saveContact(PersonModel employeeEntity) {
        PersonModelDao personModelDao = mDaoSession.getPersonModelDao();
        personModelDao.save(employeeEntity);
    }

    /**
     * 返回数据库里面的员工列表
     *
     * @return
     */
    public List<PersonModel> getEmployeeList() {
        PersonModelDao personModelDao = mDaoSession.getPersonModelDao();
        QueryBuilder<PersonModel> contactQueryBuilder = personModelDao.queryBuilder().limit(50);
        List<PersonModel> list = contactQueryBuilder.list();
        Collections.reverse(list);
        return list;
    }

    /**
     * 删除员工数据库数据
     */
    public void clearEmployees() {
        PersonModelDao personModelDao = mDaoSession.getPersonModelDao();
        personModelDao.deleteAll();
    }


    /**
     * 关闭数据库
     */
    public void closeDataBase() {
        closeHelper();
        closeDaoSession();
        sDatabaseManager = null;
    }

    public void closeDaoSession() {
        if (null != mDaoSession) {
            mDaoSession.clear();
            mDaoSession = null;
        }
    }

    public void closeHelper() {
        if (devOpenHelper != null) {
            devOpenHelper.close();
            devOpenHelper = null;
        }
    }
}