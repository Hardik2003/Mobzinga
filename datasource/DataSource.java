package com.datasource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.databasehelper.MySQLiteHelper;
import com.pojo.LikeMaster;
import com.pojo.OfferDetails;
import com.pojo.UserLikes;
import com.pojo.UserMasterLogin;

public class DataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;

	public DataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public List<LikeMaster> readLikes(String argument) {
		List<LikeMaster> likeList = new ArrayList<LikeMaster>();
		Cursor c;
		if(argument!="")
			c = database.rawQuery("Select * from LikeMaster l where l.LikeName LIKE '" + argument +"'", null);
		else 
			c = database.rawQuery("Select * from LikeMaster l ORDER BY l.LikeType ASC", null);
		c.moveToFirst();
		for (boolean hasItem = c.moveToFirst(); hasItem; hasItem = c
				.moveToNext()) {
			LikeMaster like = new LikeMaster();
			like.setLikeID(c.getInt(0));
			like.setLikeName(c.getString(1));
			like.setLikeType(c.getString(2));
			like.setApplication(c.getString(3));
			like.setRefreshDateTime(c.getString(4));

			likeList.add(like);
		}

		return likeList;
	}
	
	public List<String> readLikeTypes() {
		List<String> likeTypes = new ArrayList<String>();
		Cursor c;
		c = database.rawQuery("Select * from LikeMaster l GROUP BY l.LikeType ORDER BY l.LikeType ASC", null);
		c.moveToFirst();
		for (boolean hasItem = c.moveToFirst(); hasItem; hasItem = c
				.moveToNext()) {
			likeTypes.add(c.getString(2));
		}

		return likeTypes;
	}
	
	public List<HashMap<String, String>> readOffers(double lat, double longt, float distance) {
		List<HashMap<String, String>> offerList = new ArrayList<HashMap<String, String>>();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MMM/yyyy", Locale.US);
		
		Cursor c;
		c = database.rawQuery("Select * from OfferDetails", null);
		c.moveToFirst();
		try{
			if(lat!=0 && longt!=0){
				for (boolean hasItem = c.moveToFirst(); hasItem; hasItem = c
						.moveToNext()) {
					Float distanceFromStore = findDistance(lat,longt,c.getFloat(6),c.getFloat(7));
					if(distanceFromStore < distance){
						Map<String, String> map = new HashMap<String, String>();
						map.put("name", c.getString(1));
						map.put("address", c.getString(5));
						map.put("startdate", formatter1.format(formatter.parse(c.getString(11))));
						map.put("enddate", formatter1.format(formatter.parse(c.getString(12))));
						map.put("description", c.getString(13));
						map.put("url", c.getString(14));
						map.put("distance", distanceFromStore.toString());
	
						offerList.add((HashMap<String, String>) map);
					}
				}
				Collections.sort(offerList, new Comparator<Map< String,String >>() {
			        @Override
			        public int compare(Map<String, String> lhs,
			                Map<String, String> rhs) {
			        	return lhs.get("distance").compareTo(rhs.get("distance"));
			        }
			    });
			}
			else{
				for (boolean hasItem = c.moveToFirst(); hasItem; hasItem = c
						.moveToNext()) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("name", c.getString(1));
					map.put("address", c.getString(5));
					map.put("startdate", formatter1.format(formatter.parse(c.getString(11))));
					map.put("enddate", formatter1.format(formatter.parse(c.getString(12))));
					map.put("description", c.getString(13));
					map.put("url", c.getString(14));
					
					offerList.add((HashMap<String, String>) map);
				}
			}
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		return offerList;
	}
	
	public List<UserLikes> readUserLikes() {
		List<UserLikes> userLikeList = new ArrayList<UserLikes>();
		Cursor c = database.rawQuery("Select * from UserLikes", null);
		c.moveToFirst();
		for (boolean hasItem = c.moveToFirst(); hasItem; hasItem = c
				.moveToNext()) {
			UserLikes userLikes = new UserLikes();
			userLikes.setUserID(c.getInt(0));
			userLikes.setLikeID(c.getInt(1));

			userLikeList.add(userLikes);
		}

		return userLikeList;
	}

	public void createUserLike(UserLikes userLikes) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_USER_ID,
				userLikes.getUserID());
		values.put(MySQLiteHelper.COLUMN_LIKE_ID,
				userLikes.getLikeID());
		database.insert(MySQLiteHelper.TABLE_NAME_USER_LIKES, null, values);
	}
	
	public void deleteUserLikes() {
		database.delete(MySQLiteHelper.TABLE_NAME_USER_LIKES, null, null);
	}
	
	public void createUserLogin(int userId, Double userPhone, String city) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_USER_ID,
				userId);
		values.put(MySQLiteHelper.COLUMN_USER_PHONE,
				userPhone);
		values.put(MySQLiteHelper.COLUMN_USER_LOCATION,
				city);
		database.insert(MySQLiteHelper.TABLE_NAME_USERMASTER_LOGIN, null, values);
	}
	
	public void deleteLikes() {
		database.delete(MySQLiteHelper.TABLE_NAME_LIKE_MASTER, null, null);
	}
	
	public void addLikes(LikeMaster likeMaster) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_LIKE_ID,
				likeMaster.getLikeID());
		values.put(MySQLiteHelper.COLUMN_LIKE_NAME,
				likeMaster.getLikeName());
		values.put(MySQLiteHelper.COLUMN_LIKE_TYPE,
				likeMaster.getLikeType());
		database.insert(MySQLiteHelper.TABLE_NAME_LIKE_MASTER, null, values);
	}
	
	public UserMasterLogin readUserMaster() {
		UserMasterLogin userMaster = new UserMasterLogin();
		Cursor c = database.rawQuery("Select * from UserMasterLogIn", null);
		c.moveToFirst();
		for (boolean hasItem = c.moveToFirst(); hasItem; hasItem = c
				.moveToNext()) {
			userMaster.setUserID(c.getInt(0));
			userMaster.setUserLocation(c.getString(2));
		}

		return userMaster;
	}
	
	public void updateCity(String city, int id) {
		database.execSQL("UPDATE UserMasterLogIn SET UserLocation='" + city + "' WHERE UserID=" + id + ""); 
	}
	
	public void deleteOffers() {
		database.delete(MySQLiteHelper.TABLE_NAME_OFFER_DETAILS, null, null);
	}
	
	public void addOffer(OfferDetails offerDetail) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_USER_ID,
				offerDetail.getUserID());
		values.put(MySQLiteHelper.COLUMN_COMPANY_NAME,
				offerDetail.getCompanyName());
		values.put(MySQLiteHelper.COLUMN_STORE_LOCATION,
				offerDetail.getStoreLocation());
		values.put(MySQLiteHelper.COLUMN_LAT,
				offerDetail.getLat());
		values.put(MySQLiteHelper.COLUMN_LONG,
				offerDetail.getLongt());
		values.put(MySQLiteHelper.COLUMN_LIKE_ID,
				offerDetail.getLikeID());
		values.put(MySQLiteHelper.COLUMN_STORE_ADDR_ONE,
				offerDetail.getStoreAddOne());
		values.put(MySQLiteHelper.COLUMN_OFFERID,
				offerDetail.getOfferID());
		values.put(MySQLiteHelper.COLUMN_OFFER_STARTDATE,
				offerDetail.getOfferStartDate().toString());
		values.put(MySQLiteHelper.COLUMN_OFFER_EXPIRYDATE,
				offerDetail.getOfferEndDate().toString());
		values.put(MySQLiteHelper.COLUMN_OFFER_DESCRIPTION,
				offerDetail.getOfferDescription());
		values.put(MySQLiteHelper.COLUMN_OFFER_PIC_URL, 
				offerDetail.getOfferPicURL());
		database.insert(MySQLiteHelper.TABLE_NAME_OFFER_DETAILS, null, values);
	}
	
	private float findDistance(double lat1, double lng1, float lat2, float lng2){
		double earthRadius = 3958.75;
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;

	    int meterConversion = 1609;

	    return (float) (dist * meterConversion);
	}
}
