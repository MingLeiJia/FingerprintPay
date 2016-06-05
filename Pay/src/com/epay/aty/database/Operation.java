package com.epay.aty.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Operation {
	private/* static */Sqlite dbsqlite;
	private/* static */SQLiteDatabase db;

	public Operation(Context context) {
		dbsqlite = new Sqlite(context);
	}


	public Cursor querybankname() {
		db = dbsqlite.getReadableDatabase();
		Cursor banknamedetail = db.rawQuery("select bankname from bank",null);
		//closeDB();
		return banknamedetail;
	}

	public Cursor querybankcode(String bankofname) {
		db = dbsqlite.getWritableDatabase();
		Cursor bankcode = db
				.rawQuery("select bankcode from bank where bankname = ? ",new String[] {bankofname});
		//closeDB();
		return bankcode;
	}
	public void closeDB(){
		if(db!=null){
			db.close();
		}
	}
}

