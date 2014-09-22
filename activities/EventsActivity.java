package com.activities;
 
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.adapter.TabsPagerAdapter;
import com.constants.ConnectionURL;
import com.datasource.DataSource;
import com.mobzinga.R;
import com.pojo.LikeMaster;
import com.pojo.OfferDetails;
import com.pojo.UserLikes;
import com.pojo.UserMasterLogin;
import com.scheduler.OffersAlarmReceiver;
 

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class EventsActivity extends FragmentActivity implements
        ActionBar.TabListener {
 
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    
    private static final int RESULT_SETTINGS = 1;
    private static final int PRIVACY_POLICY = 2;
    private static final int PREFERENCES = 3;
	private DataSource datasource = new DataSource(this);
	List<HashMap<String, String>> offers = new ArrayList<HashMap<String, String>>();
	private static final String URL = "http://www.mobzinga.com/store_management/";
    
    // Progress Dialog
  	private ProgressDialog pDialog;
  	Intent i;
  	
    // Tab titles
    private String[] tabs = { "Events Near You", "Ongoing events" };
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
 
        // Initialization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
 
        viewPager.setAdapter(mAdapter);
        //actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);        
 
        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }
 
        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
 
            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + viewPager.getCurrentItem());
                if(position == 0)
                	((GPSEventsActivity) fragment).refresh();
 	           	else if(position == 1)
 	           		((OngoingEventsActivity) fragment).refresh();
            }
 
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
 
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }
    
    @Override
	public void onBackPressed() {
		Intent startMain = new Intent(Intent.ACTION_MAIN);
		startMain.addCategory(Intent.CATEGORY_HOME);
		startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(startMain);
	}
 
    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }
 
    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }
 
    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.menu_settings:
			i = new Intent(this, LikeMasterActivity.class);
			new FetchLikes().execute();
			break;
			
		case R.id.change_city:
			Intent intent1 = new Intent(this, ChangeCityActivity.class);
			startActivity(intent1);
			break;
			
		case R.id.refresh:
			new Refresh().execute();
			break;
			
		case R.id.privacy_policy:
			Intent inten = new Intent(this, PrivacyPolicyActivity.class);
			startActivityForResult(inten, PRIVACY_POLICY);
			break;
			
		case R.id.preferences:
			Intent in= new Intent(this, UserPreferencesActivity.class);
			startActivityForResult(in, PREFERENCES);
			break;
		}
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case RESULT_SETTINGS:
			saveUserLikes();
			break;
			
		case PRIVACY_POLICY:
			Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + viewPager.getCurrentItem());
            if(viewPager.getCurrentItem() == 0)
            	((GPSEventsActivity) fragment).refresh();
	        else if(viewPager.getCurrentItem() == 1)
	        	((OngoingEventsActivity) fragment).refresh();
			break;
			
		case PREFERENCES:
			SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
			
			OffersAlarmReceiver alarm = new OffersAlarmReceiver();
			alarm.setAlarm(this,sharedPrefs.getInt("Hour", 0), sharedPrefs.getInt("Minute", 0));
			
			Fragment fragment1 = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + viewPager.getCurrentItem());
            if(viewPager.getCurrentItem() == 0)
            	((GPSEventsActivity) fragment1).refresh();
	        else if(viewPager.getCurrentItem() == 1)
	        	((OngoingEventsActivity) fragment1).refresh();
			break;
		}
	}
 
	private class FetchLikes extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpGet httpGet = new HttpGet(
					ConnectionURL.FETCH_LIKES);
			JSONObject jsonResponse;
			datasource.open();
			datasource.deleteLikes();

			try {

				HttpResponse response = httpClient.execute(httpGet,
						localContext);
				
				jsonResponse = new JSONObject(EntityUtils.toString(response.getEntity()));
				JSONArray jsonMainNode = jsonResponse.optJSONArray("likes");
				/*********** Process each JSON Node ************/
				 
                int lengthJsonArr = jsonMainNode.length();  

                for(int i=1; i < lengthJsonArr; i++) 
                {
                    /****** Get Object for each JSON node.***********/
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                     
                    LikeMaster like = new LikeMaster();
                    like.setLikeID(Integer.parseInt(jsonChildNode.optString("likeid").toString()));
                    like.setLikeName(jsonChildNode.optString("likename").toString());
                    like.setLikeType(jsonChildNode.optString("liketype").toString());
                    
                    datasource.addLikes(like);
               }
			
				String respStr = EntityUtils.toString(response.getEntity());
				System.out.println(respStr);

			} catch (Exception e) {
				return e.getLocalizedMessage();
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EventsActivity.this);
			pDialog.setMessage("Please wait");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected void onPostExecute(String results) {
			datasource.close();
			pDialog.dismiss();
			startActivityForResult(i, RESULT_SETTINGS);
			if (results != null) {

			}
		}
	}
	
	private class Refresh extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			JSONObject jsonResponse;
			HttpPost httpPost = new HttpPost(
					ConnectionURL.FETCH_OFFERS);
			httpPost.setHeader("Content-type",
					"application/x-www-form-urlencoded; charset=UTF8");

			datasource.open();
			UserMasterLogin userLogin = datasource.readUserMaster();	
			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
			String formattedDate = df.format(c.getTime());
			
			List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
	        nameValuePair.add(new BasicNameValuePair("userid", ((Integer)userLogin.getUserID()).toString()));
	        nameValuePair.add(new BasicNameValuePair("city", userLogin.getUserLocation()));
	        nameValuePair.add(new BasicNameValuePair("date", formattedDate));
	        
	        try{
	        	httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

	        	HttpResponse response = httpClient.execute(httpPost,
					localContext);
	        	datasource.deleteOffers();
	        	jsonResponse = new JSONObject(EntityUtils.toString(response.getEntity()));
				JSONArray jsonMainNode = jsonResponse.optJSONArray("offers");
				/*********** Process each JSON Node ************/
				 
	            int lengthJsonArr = jsonMainNode.length();  

	            for(int i=0; i < lengthJsonArr; i++) 
	            {
	                /****** Get Object for each JSON node.***********/
	                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
	                 
	                OfferDetails offer = new OfferDetails();
	                offer.setUserID(userLogin.getUserID());
	                offer.setCompanyName(jsonChildNode.optString("companyname").toString());
	                offer.setOfferPicURL(EventsActivity.URL + jsonChildNode.optString("companyurl").toString());
	                offer.setStoreLocation(jsonChildNode.optString("storelocation").toString());
	                offer.setLat(Double.parseDouble(jsonChildNode.optString("lat").toString()));
	                offer.setLongt(Double.parseDouble(jsonChildNode.optString("longt").toString()));
	                offer.setLikeID(Integer.parseInt(jsonChildNode.optString("likeid").toString()));
	                offer.setStoreAddOne(jsonChildNode.optString("storeaddrone").toString());
	                offer.setOfferID(Integer.parseInt(jsonChildNode.optString("offerid").toString()));
	                offer.setOfferStartDate(Date.valueOf(jsonChildNode.optString("offerstartdate").toString()));
	                offer.setOfferEndDate(Date.valueOf(jsonChildNode.optString("offerexpirydate").toString()));
	                offer.setOfferDescription(jsonChildNode.optString("offerdescription").toString());
	                
	                datasource.addOffer(offer);
	           }
	           
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EventsActivity.this);
			pDialog.setMessage("Please wait");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected void onPostExecute(String results) {
			datasource.close();
			pDialog.dismiss();
			Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + viewPager.getCurrentItem());
	        if(viewPager.getCurrentItem() == 0){
	        	GPSEventsActivity.gps.stopUsingGPS();
	        	((GPSEventsActivity) fragment).refresh();
	        }
	        else if(viewPager.getCurrentItem() == 1)
	        	((OngoingEventsActivity) fragment).refresh();
			if (results != null) {

			}
		}
	}
	
	private void saveUserLikes() {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		datasource = new DataSource(this);
		datasource.open();

		datasource.deleteUserLikes();
		UserMasterLogin userMaster = datasource.readUserMaster();
		List<LikeMaster> likeList = datasource.readLikes("");
		for (int i = 0; i < likeList.size(); i++) {
			if(sharedPrefs.getBoolean(Integer.valueOf(likeList.get(i).getLikeID()).toString(), false)){
				UserLikes userLike = new UserLikes();
				userLike.setUserID(userMaster.getUserID());
				userLike.setLikeID(likeList.get(i).getLikeID());
				datasource.createUserLike(userLike);
			}
		}
		
		datasource.close();
		new SaveLikesInCentral().execute();
	}
	
	private class SaveLikesInCentral extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			JSONObject jsonResponse;
			HttpPost httpPost = new HttpPost(
					ConnectionURL.ADD_USER_LIKES);
			httpPost.setHeader("Content-type",
					"application/json; charset=UTF8");

			datasource.open();
			UserMasterLogin userLogin = datasource.readUserMaster();
			List<UserLikes> userLikesList = datasource.readUserLikes();
			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
			String formattedDate = df.format(c.getTime());
			
			String text = "{\"id\": \"" + userLogin.getUserID() + "\",\"city\": \"" + userLogin.getUserLocation() + "\",\"date\": \"" + 
					formattedDate + "\",\"likes\": [";
			for(int i=0;i<userLikesList.size();i++){
				text = text + "{\"userid\": \"" + userLikesList.get(i).getUserID() + "\"," +
						"\"likeid\": \"" + userLikesList.get(i).getLikeID() + "\"},";
			}
			if(userLikesList.size()!=0)
				text = text.substring(0, text.length()-1);
			text = text + "]}";
			try {

				StringEntity se = new StringEntity(text);
				httpPost.setEntity(se);
				HttpResponse response = httpClient.execute(httpPost,
						localContext);
				
				datasource.deleteOffers();
				jsonResponse = new JSONObject(EntityUtils.toString(response.getEntity()));
				JSONArray jsonMainNode = jsonResponse.optJSONArray("offers");
				/*********** Process each JSON Node ************/
				 
                int lengthJsonArr = jsonMainNode.length();  

                for(int i=0; i < lengthJsonArr; i++) 
                {
                    /****** Get Object for each JSON node.***********/
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                     
                    OfferDetails offer = new OfferDetails();
                    offer.setUserID(userLogin.getUserID());
                    offer.setCompanyName(jsonChildNode.optString("companyname").toString());
                    offer.setOfferPicURL(EventsActivity.URL + jsonChildNode.optString("companyurl").toString());
                    offer.setStoreLocation(jsonChildNode.optString("storelocation").toString());
                    offer.setLat(Double.parseDouble(jsonChildNode.optString("lat").toString()));
                    offer.setLongt(Double.parseDouble(jsonChildNode.optString("longt").toString()));
                    offer.setLikeID(Integer.parseInt(jsonChildNode.optString("likeid").toString()));
                    offer.setStoreAddOne(jsonChildNode.optString("storeaddrone").toString());
                    offer.setOfferID(Integer.parseInt(jsonChildNode.optString("offerid").toString()));
                    offer.setOfferStartDate(Date.valueOf(jsonChildNode.optString("offerstartdate").toString()));
                    offer.setOfferEndDate(Date.valueOf(jsonChildNode.optString("offerexpirydate").toString()));
                    offer.setOfferDescription(jsonChildNode.optString("offerdescription").toString());
                    
                    datasource.addOffer(offer);
               }
               
			} catch (Exception e) {
				 System.out.println(e.getLocalizedMessage());
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EventsActivity.this);
			pDialog.setMessage("Please wait");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected void onPostExecute(String results) {
			datasource.close();
			pDialog.dismiss();
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + viewPager.getCurrentItem());
            if(viewPager.getCurrentItem() == 0)
            	((GPSEventsActivity) fragment).refresh();
	        else if(viewPager.getCurrentItem() == 1)
	        	((OngoingEventsActivity) fragment).refresh(); 
			if (results != null) {

			}
		}
	}
}