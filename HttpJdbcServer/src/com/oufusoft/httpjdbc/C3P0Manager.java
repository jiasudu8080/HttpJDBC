package com.oufusoft.httpjdbc;



import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyVetoException;
import java.lang.reflect.Field;
import java.sql.*;

/**
 * C3PO 纯java使用，不需要借助其他框架
 * [@Author](https://my.oschina.net/arthor) liufu
 * [@Company](https://my.oschina.net/u/3478402) 任子行网络技术股份有限公司
 * @CreateTime 2017/11/3  9:16
 */
public class C3P0Manager {
    private static final Logger LOGGER = LoggerFactory.getLogger(C3P0Manager.class);

    private static C3P0Manager inStance;
    private ComboPooledDataSource dataSource;

    private C3P0Manager(){}

    private C3P0Manager(String driverClass, String jdbcUrl, String user, String passwd, int poolSize) {
        dataSource = new ComboPooledDataSource();
        try {
            dataSource.setDriverClass(driverClass);
        } catch (PropertyVetoException e) {
            LOGGER.error("C3PO配置驱动类时，报错：{}", e);
        }
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUser(user);
        dataSource.setPassword(passwd);
        dataSource.setAcquireIncrement(5);
        dataSource.setInitialPoolSize(poolSize);
        dataSource.setMaxPoolSize(poolSize + 10);
    }

    /**
     * 单例模式
     * [@param](https://my.oschina.net/u/2303379) driverClass
     * [@param](https://my.oschina.net/u/2303379) jdbcUrl
     * @param user
     * @param passwd
     * @param poolSize
     * @return
     */
    public static C3P0Manager getInstance(String driverClass, String jdbcUrl, String user, String passwd, int poolSize) {//完成初始化
        if (inStance == null) {
            synchronized ("getC3P0Manager") {
                if (inStance == null) {
                    inStance = new C3P0Manager(driverClass, jdbcUrl, user, passwd, poolSize);
                }
            }
        }
        return inStance;
    }

    /**
     * 同步获取一个连接
     * @return
     */
    public synchronized Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            LOGGER.error("C3PO获取connection报错：{}", e);
        }
        return null;
    }

    /**
     * 同步返回一个连接
     * @param connection
     * @return
     */
    public synchronized boolean returnConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            LOGGER.error("C3PO关闭connection报错：{}", e);
        }
        return true;
    }

    /**
     * 释放Statement 和 ResultSet
     * @param st
     * @param rs
     */
    public void closeStatement(Statement st, ResultSet rs) {
        try {
            rs.close();
            st.close();
        } catch (Exception e) {
            LOGGER.error("C3PO关闭Statement 或者 resultSet报错：{}", e);
        }
    }

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
    	
    	
    	String driverClass = "com.mysql.jdbc.Driver";
//        String jdbcUrl = "jdbc:mysql://192.168.50.11:3306/caiwutong?useUnicode=true&characterEncoding=utf8&autoReconnect=true&allowMultiQueries=true";
        
         
        String jdbcUrl =  "jdbc:mysql://192.168.199.170:3306/ecpt";
        String userName = "root";
        String passwd = "123456";
        int poolSize = 10;
        C3P0Manager instance = C3P0Manager.getInstance(driverClass, jdbcUrl, userName, passwd, poolSize);

        Connection connection = null;
       
        // 1、循环获取connection，拿一个放回去一个，判断是否是同一个 ==》反复都是使用前几个相同的
//        for (int i = 0; i < 10; i++) {
//            connection = instance.getConnection();
//            Field inner = connection.getClass().getDeclaredField("inner");
//            inner.setAccessible(true);
//           // System.out.println(inner.get(connection));
//            instance.returnConnection(connection);      //只需要connection.close()，c3p0就会把它回收，而不是关闭
//        }
       
        // 2、循环获取connection，没有放回去，判断是否是同一个 ==》每次都不一样，同时达到20个上限时会被阻塞
//        for (int i = 0; i < 10; i++) {
//            connection = instance.getConnection();
//            Field inner = connection.getClass().getDeclaredField("inner");
//            inner.setAccessible(true);
//            //System.out.println(inner.get(connection));
//        }

       
        connection = instance.getConnection();
        
        try {
        	String sql = "select * from smart_user";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            
            ResultSetMetaData rsmd = resultSet.getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            	if (i > 1) {
            		System.out.print(",");
            	}
            	System.out.print(rsmd.getColumnName(i));
            }
            while (resultSet.next()) {
            	System.out.println();
            	for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            		if (i > 1) {
            			System.out.print(",");
            		}
            		System.out.print(resultSet.getObject(i));
            	}
            }

            //关闭连接statement he resultSet ， 同时将connection放回到连接池中
            instance.closeStatement(preparedStatement, resultSet);
            instance.returnConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}