package com.activities;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.constants.ConnectionURL;
import com.datasource.DataSource;
import com.helper.FileCache;
import com.mobzinga.R;
import com.pojo.OfferDetails;
import com.pojo.UserLikes;
import com.scheduler.OffersAlarmReceiver;

public class RegisterUserActivity extends Activity implements OnItemSelectedListener{

	private DataSource datasource;
	private EditText mPhoneView;
	private EditText mNameView;
	private RadioGroup radioSexGroup;
	private RadioButton radioSexButton;
	private DatePicker dobView;

	private String phone;
	private String firstName;
	private String city;
	private String gender;
	private String dob;
	private String day;
	private String month;
	private String year;
	
	// Progress Dialog
	private ProgressDialog pDialog;
	OffersAlarmReceiver alarm = new OffersAlarmReceiver();
	Intent intent;
	
	private LruCache<String, Bitmap> mMemoryCache;
	FileCache fileCache;
	private static final String URL = "http://www.mobzinga.com/store_management/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		alarm.setAlarm(this,0,0);
		setContentView(R.layout.activity_register_user);

		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {};
        fileCache=new FileCache(this);
		
		// Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.city);
        // Spinner click listener
        spinner.setOnItemSelectedListener((OnItemSelectedListener) this);
 
        // Spinner Drop down elements
        List<String> cities = new ArrayList<String>();
        cities.add("Rajkot");
        cities.add("Ahmedabad");
        cities.add("Mumbai");
        cities.add("Bangalore");
        cities.add("Delhi");
        cities.add("Kolkata");
        cities.add("Chennai");
        cities.add("Hyderabad");
        
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cities);
 
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
 
        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        
		mPhoneView = (EditText) findViewById(R.id.registerphone);
		mNameView = (EditText) findViewById(R.id.firstName);
		radioSexGroup = (RadioGroup) findViewById(R.id.gender);
		dobView = (DatePicker) findViewById(R.id.dob);
		
	}

	/** Called when the user clicks the Register button */
	public void register(View view) {
		intent = new Intent(this, EventsActivity.class);

		phone = mPhoneView.getText().toString();
		firstName = mNameView.getText().toString();
		day = Integer.valueOf(dobView.getDayOfMonth()).toString();
		month = Integer.valueOf(dobView.getMonth()+1).toString();
		year = Integer.valueOf(dobView.getYear()).toString();
		dob = day + "/" + month + "/" + year;
		
		// get selected radio button from radioGroup
        int selectedId = radioSexGroup.getCheckedRadioButtonId();
        // find the radiobutton by returned id
        radioSexButton = (RadioButton) findViewById(selectedId);
        gender = radioSexButton.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid phone.
		if (TextUtils.isEmpty(phone)) {
			mPhoneView.setError(getString(R.string.error_field_required));
			focusView = mPhoneView;
			cancel = true;
		}

		if (TextUtils.isEmpty(firstName)) {
			mNameView.setError(getString(R.string.error_field_required));
			focusView = mNameView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			datasource = new DataSource(this);
			new CreateUser().execute();

		}
	}
	
	@Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        city = parent.getItemAtPosition(position).toString();
        
    }
	
	public void onNothingSelected(AdapterView<?> parent) {
		city = parent.getItemAtPosition(0).toString();
    }
	
	private class CreateUser extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			JSONObject jsonResponse;

			HttpPost httpPost = new HttpPost(
					ConnectionURL.REGISTER_USER);
			httpPost.setHeader("Content-type",
					"application/x-www-form-urlencoded; charset=UTF8");

			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
			String formattedDate = df.format(c.getTime());
			String text = null;
			try {
				
				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
	            nameValuePair.add(new BasicNameValuePair("phone", phone));
	            nameValuePair.add(new BasicNameValuePair("city", city));
	            nameValuePair.add(new BasicNameValuePair("firstname", firstName));
	            if(gender.equalsIgnoreCase("Male"))
	            	nameValuePair.add(new BasicNameValuePair("gender", "M"));
	            else
	            	nameValuePair.add(new BasicNameValuePair("gender", "F"));
	            nameValuePair.add(new BasicNameValuePair("dob", dob));
				
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

				HttpResponse response = httpClient.execute(httpPost,
						localContext);
				
				jsonResponse = new JSONObject(EntityUtils.toString(response.getEntity()));
				int id = Integer.parseInt(jsonResponse.opt("id").toString());
				
				datasource.open();
				datasource.createUserLogin(id, Double.valueOf(phone), city);
                
				SharedPreferences sharedPrefs = PreferenceManager
						.getDefaultSharedPreferences(RegisterUserActivity.this);
				Editor editor = sharedPrefs.edit();
				
				datasource.deleteUserLikes();
				JSONArray jsonMainNode1 = jsonResponse.optJSONArray("likes");
				/*********** Process each JSON Node ************/
				 
				int lengthJsonArr1 = jsonMainNode1.length();  

				for(int i=1; i < lengthJsonArr1; i++) 
                {
					/****** Get Object for each JSON node.***********/
                    JSONObject jsonChildNode = jsonMainNode1.getJSONObject(i);
                    
                    UserLikes userLike = new UserLikes();
   				    userLike.setUserID(id);
   				    userLike.setLikeID(Integer.parseInt(jsonChildNode.optString("likeid").toString()));
   				    datasource.createUserLike(userLike);
   				    editor.putBoolean(jsonChildNode.optString("likeid").toString(), true);
                }

				editor.commit();
				
				//Fetch All Offers
				HttpPost httpPost1 = new HttpPost(
						ConnectionURL.ADD_USER_LIKES);
				httpPost1.setHeader("Content-type",
						"application/json; charset=UTF8");

      		  	List<UserLikes> userLikesList = datasource.readUserLikes();
      			
      		  	String text1 = "{\"id\": \"" + id + "\",\"city\": \"" + city + "\",\"date\": \"" + 
      		  			formattedDate + "\",\"likes\": [";
      		  	for(int i=0;i<userLikesList.size();i++){
      		  		text1 = text1 + "{\"userid\": \"" + userLikesList.get(i).getUserID() + "\"," +
      						"\"likeid\": \"" + userLikesList.get(i).getLikeID() + "\"},";
      		  	}
      		  
      		  	if(userLikesList.size()!=0)
      		  		text1 = text1.substring(0, text1.length()-1);
      		  	text1 = text1 + "]}";

      		  	StringEntity se = new StringEntity(text1);
      		  	httpPost1.setEntity(se);
      		  	response = httpClient.execute(httpPost1, localContext);
    				
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
      		  		offer.setUserID(id);
      		  		offer.setCompanyName(jsonChildNode.optString("companyname").toString());
      		  		offer.setOfferPicURL(RegisterUserActivity.URL + jsonChildNode.optString("companyurl").toString());
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
      			   
      		  	String respStr = EntityUtils.toString(response.getEntity());
      		  	System.out.println(respStr);

			} catch (Exception e) {
				return e.getLocalizedMessage();
			}
			return text;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(RegisterUserActivity.this);
			pDialog.setMessage("Please wait");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected void onPostExecute(String results) {
			datasource.close();
			startActivity(intent);
			pDialog.dismiss();
			if (results != null) {

			}
		}
	}
	
	public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
}
