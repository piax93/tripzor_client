package com.ifalot.tripzor.utils;

import com.ifalot.tripzor.web.Codes;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

@SuppressWarnings("Duplicates")
public class DataManager {
	
	private static final String databaseName = "TripzorData";
	private static final String tableName = "data";
	private static SQLiteDatabase database;
	
	public static void init(Context c){
		database = c.openOrCreateDatabase(databaseName, 
				Context.MODE_PRIVATE, null);
		database.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + 
				" (key VARCHAR(255) PRIMARY KEY, value VARCHAR(255) )");
	}
	
	public static boolean isPresent(String key){
		return !(selectData(key).equals(Codes.EMPTY));
	}
	
	public static boolean insertData(String key, String value){
		try	{
			if(!isPresent(key)){
				String sql = "INSERT INTO " + tableName + " VALUES('" + key + "','" + value + "')";
				if(database != null) database.execSQL(sql);
				return true;
			}else{
				return updateValue(key, value);
			}
		} catch (SQLException e){
			return false;
		}
	}
	
	public static boolean updateValue(String key, String newValue){
		String sql = "UPDATE TABLE " + tableName + "SET value = '" + newValue + "' WHERE key = '" + key + "'";
		try{
			if(database != null) database.execSQL(sql);
			return true;
		}catch(SQLException e){
			return false;
		}
	}
	
	public static boolean deleteData(String key){
		String sql = "DELETE FROM " + tableName + " WHERE key = '" + key + "'";
		try{
			if(database != null) database.execSQL(sql);
			return true;
		}catch(SQLException e){
			return false;
		}
	}
	
	@SuppressWarnings("finally")
	public static String selectData(String key){
		String result = Codes.EMPTY; Cursor c;
		String sql = "SELECT value FROM " + tableName + " WHERE key = '" + key + "'";
		try {
			if(database != null){
				c = database.rawQuery(sql, null);
				c.moveToFirst();
				result = c.getString(0);
				c.close();
			}
		} finally {
			return result;
		}
	}

}
