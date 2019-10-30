package com.oufusoft.httpjdbc;

import java.io.IOException;

import com.google.gson.Gson;
import com.mzlion.easyokhttp.HttpClient;

public class OkHttpTest {

	
	 public static void main(String[] args) {
		 System.out.println("start");
		 postHttpJDBC();
		
		 System.out.println("end");
	}
	 
	 
	 private static void postHttpJDBC() {
		 try {
		 String sql ="select * from smart_user where user_name ='����ҫ'";
		 
		 String responseData = HttpClient
				
	                .post("http://localhost:8080/httpjdbc/")
	                .param("db","ecpt")  // ������
	                .param("sql", Crypt.encrypt(sql, "ecpt123456"))     // ������
	                
	                .execute()
	                .asString();
		 
		 System.out.println(responseData);
		 
		 
		 
		 Gson  gson =new Gson();
		 RowSet rowSet =gson.fromJson(responseData, RowSet.class);
		
		 System.out.println(rowSet.size());
		 
		 } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 }
	 
		  
}
