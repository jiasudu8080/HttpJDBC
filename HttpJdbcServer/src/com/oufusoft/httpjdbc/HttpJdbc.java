package com.oufusoft.httpjdbc;


import java.sql.*;
 
public class HttpJdbc {
 
    // MySQL 8.0 以下版本 - JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://192.168.199.170:3306/ecpt";
 
    // MySQL 8.0 以上版本 - JDBC 驱动名及数据库 URL
    //static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  
    //static final String DB_URL = "jdbc:mysql://192.168.199.170:3306/ecpt?useSSL=false&serverTimezone=UTC";
 
 
    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "root";
    static final String PASS = "123456";
 
 public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
    	
    	
    	String driverClass = "com.mysql.jdbc.Driver";         
        String jdbcUrl =  "jdbc:mysql://192.168.199.170:3306/ecpt";
        String userName = "root";
        String passwd = "123456";
        int poolSize = 10;
        C3P0Manager instance = C3P0Manager.getInstance(driverClass, jdbcUrl, userName, passwd, poolSize);

        Connection connection = instance.getConnection();
        
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