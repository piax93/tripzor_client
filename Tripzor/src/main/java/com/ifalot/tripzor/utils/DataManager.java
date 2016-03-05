package com.ifalot.tripzor.utils;

import com.ifalot.tripzor.web.Codes;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

@SuppressWarnings({"Duplicates"})
public class DataManager {
	
	private static final String databaseName = "TripzorData";
	private static final String tableName = "data";
	private static Context context;

	private static SQLiteDatabase getDB(){
		return context.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null);
	}
	
	public static void init(Context c){
		context = c;
		SQLiteDatabase database = getDB();
		database.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + 
				" (key VARCHAR(255) PRIMARY KEY, value VARCHAR(255) )");
		database.close();
	}
	
	public static boolean isPresent(String key){
		return !(selectData(key).equals(Codes.EMPTY));
	}
	
	public static boolean insertData(String key, String value){
		boolean stat = true;
		SQLiteDatabase database = null;
		try	{
			if(!isPresent(key)){
				String sql = "INSERT INTO " + tableName + " VALUES('" + key + "','" + value + "')";
				database = getDB();
				database.execSQL(sql);
				stat = true;
			}else{
				stat = updateValue(key, value);
			}
		} catch (SQLException e){
			stat = false;
		} finally {
			if(database != null) database.close();
		}
		return stat;
	}
	
	public static boolean updateValue(String key, String newValue){
		String sql = "UPDATE " + tableName + " SET value = '" + newValue + "' WHERE key = '" + key + "'";
		SQLiteDatabase database = null;
		boolean stat = true;
		try{
			database = getDB();
			database.execSQL(sql);
		} catch(SQLException e){
			stat = false;
		} finally {
			if(database != null) database.close();
		}
		return stat;
	}
	
	public static boolean deleteData(String key){
		String sql = "DELETE FROM " + tableName + " WHERE key = '" + key + "'";
		SQLiteDatabase database = null;
		boolean stat = true;
		try{
			database = getDB();
			database.execSQL(sql);
		}catch(SQLException e){
			stat = false;
		}finally {
			if(database != null) database.close();
		}
		return stat;
	}
	
	@SuppressWarnings("finally")
	public static String selectData(String key){
		String result = Codes.EMPTY;
		Cursor c = null;
		String sql = "SELECT value FROM " + tableName + " WHERE key = '" + key + "'";
		SQLiteDatabase database = null;
		try {
			database = getDB();
			c = database.rawQuery(sql, null);
			if(c.moveToFirst()) result = c.getString(0);
			c.close();
		} catch(Exception e){
			result = Codes.EMPTY;
		} finally {
			if(c != null && !c.isClosed()) c.close();
			if(database != null) database.close();
		}
		return result;
	}

}