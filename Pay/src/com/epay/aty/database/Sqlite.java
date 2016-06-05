package com.epay.aty.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Sqlite extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "bankinfo.db";
	private static final int DATABASE_VERSION = 1;
	public Sqlite(Context context) {
		super(context, DATABASE_NAME, null,DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}
	final String CREATE_TABLE_SQL="create table bank (_id integer primary key autoincrement,bankname text,bankcode text);";
	final String INSERT_DATA1="insert into bank(_id,bankname,bankcode) values(1,'中国银行','BKCHCNBJ')";
	final String INSERT_DATA2="insert into bank(_id,bankname,bankcode) values(2,'中国工商银行','ICBKCNBJ')";
	final String INSERT_DATA3="insert into bank(_id,bankname,bankcode) values(3,'中国建设银行','PCBCCNBJ')";
	final String INSERT_DATA4="insert into bank(_id,bankname,bankcode) values(4,'中国农业银行','ABOCCNBJ')";
	final String INSERT_DATA5="insert into bank(_id,bankname,bankcode) values(5,'交通银行','COMMCNSH')";
	final String INSERT_DATA6="insert into bank(_id,bankname,bankcode) values(6,'招商银行','CMBCCNBS')";
	final String INSERT_DATA7="insert into bank(_id,bankname,bankcode) values(7,'兴业银行','FJIBCNBA')";
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_TABLE_SQL);
		db.execSQL(INSERT_DATA1);
		db.execSQL(INSERT_DATA2);
		db.execSQL(INSERT_DATA3);
		db.execSQL(INSERT_DATA4);
		db.execSQL(INSERT_DATA5);
		db.execSQL(INSERT_DATA6);
		db.execSQL(INSERT_DATA7);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
