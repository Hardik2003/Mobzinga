package com.databasehelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author DK
 *
 */
public class MySQLiteHelper extends SQLiteOpenHelper{

	public static final String TABLE_NAME_LIKE_MASTER = "LikeMaster";
	public static final String COLUMN_LIKE_ID = "LikeID";
	public static final String COLUMN_LIKE_NAME = "LikeName";
	public static final String COLUMN_LIKE_TYPE = "LikeType";
	public static final String COLUMN_APPLICATION = "Application";
	public static final String COLUMN_REFRESH_DATE_TIME = "RefreshDateTime";
	
	public static final String TABLE_NAME_USER_LIKES = "UserLikes";
	public static final String COLUMN_USER_ID = "UserID";
	
	public static final String TABLE_NAME_USERMASTER_LOGIN = "UserMasterLogIn";
	public static final String COLUMN_USER_PHONE = "UserPhone";
	public static final String COLUMN_USER_LOCATION = "UserLocation";
	public static final String COLUMN_USER_ADRANGE = "UserAdRange";
	
	public static final String TABLE_NAME_OFFER_DETAILS = "OfferDetails";
	public static final String COLUMN_COMPANY_NAME = "CompanyName";
	public static final String COLUMN_COMPANY_LOGO = "CompanyLogo";
	public static final String COLUMN_STORE_ID = "StoreID";
	public static final String COLUMN_STORE_LOCATION = "StoreLocation";
	public static final String COLUMN_STORE_ADDR_ONE = "StoreAddrLineOne";
	public static final String COLUMN_LAT = "Lat";
	public static final String COLUMN_LONG = "Longt";
	public static final String COLUMN_OFFERID = "OfferID";
	public static final String COLUMN_OFFER_STARTDATE = "OfferStartDate";
	public static final String COLUMN_OFFER_EXPIRYDATE = "OfferExpiryDate";
	public static final String COLUMN_OFFER_DESCRIPTION = "OfferDescription";
	public static final String COLUMN_OFFER_PIC_URL = "OfferPicURL";
	
	public static final String TABLE_NAME_PUSHEDDATA_HISTORY = "PushedDataHistory";
	public static final String COLUMN_TIMESTAMP = "TimeStamp";

	private static final String DATABASE_NAME = "MobZinga.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String TABLE_CREATE_LIKE_MASTER = "create table "
	      + TABLE_NAME_LIKE_MASTER + "(" + COLUMN_LIKE_ID
	      + " integer not null, " + COLUMN_LIKE_NAME
	      + " text not null, " + COLUMN_LIKE_TYPE
	      + " text, " + COLUMN_APPLICATION
	      + " text, " + COLUMN_REFRESH_DATE_TIME
	      + " text);";
	
	private static final String TABLE_CREATE_USER_LIKES = "create table " + TABLE_NAME_USER_LIKES
			+ "(" + COLUMN_USER_ID + " integer not null, " + COLUMN_LIKE_ID
			+ " integer);";
	
	private static final String TABLE_CREATE_USERMASTER_LOGIN = "create table " + TABLE_NAME_USERMASTER_LOGIN
			+ "(" + COLUMN_USER_ID + " integer not null, " + COLUMN_USER_PHONE
			+ " real not null, " + COLUMN_USER_LOCATION
			+ " real, " + COLUMN_USER_ADRANGE
			+ " integer);";
	
	private static final String TABLE_CREATE_OFFER_DETAILS = "create table " + TABLE_NAME_OFFER_DETAILS
			+ "(" + COLUMN_USER_ID + " integer not null, " + COLUMN_COMPANY_NAME
			+ " text, " + COLUMN_COMPANY_LOGO
			+ " text, " + COLUMN_STORE_ID
			+ " integer, " + COLUMN_STORE_LOCATION
			+ " text, " + COLUMN_STORE_ADDR_ONE
			+ " text, " + COLUMN_LAT
			+ " real, " + COLUMN_LONG
			+ " real, " + COLUMN_LIKE_ID
			+ " integer, " + COLUMN_LIKE_TYPE
			+ " text, " + COLUMN_OFFERID
			+ " integer, " + COLUMN_OFFER_STARTDATE
			+ " text, " + COLUMN_OFFER_EXPIRYDATE
			+ " text, " + COLUMN_OFFER_DESCRIPTION
			+ " text, " + COLUMN_OFFER_PIC_URL
			+ " text);";
	
	private static final String TABLE_CREATE_PUSHEDDATA_HISTORY = "create table " + TABLE_NAME_PUSHEDDATA_HISTORY
			+ "(" + COLUMN_USER_ID + " integer not null, " + COLUMN_OFFERID
			+ " integer, " + COLUMN_TIMESTAMP
			+ " real);";

	public MySQLiteHelper(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
	    database.execSQL(TABLE_CREATE_LIKE_MASTER);
	    database.execSQL(TABLE_CREATE_USER_LIKES);
	    database.execSQL(TABLE_CREATE_USERMASTER_LOGIN);
	    database.execSQL(TABLE_CREATE_OFFER_DETAILS);
	    database.execSQL(TABLE_CREATE_PUSHEDDATA_HISTORY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(MySQLiteHelper.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_LIKE_MASTER);
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USER_LIKES);
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USERMASTER_LOGIN);
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_OFFER_DETAILS);
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PUSHEDDATA_HISTORY);
	    onCreate(db);
	}
}
