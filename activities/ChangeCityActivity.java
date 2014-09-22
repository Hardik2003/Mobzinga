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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.constants.ConnectionURL;
import com.datasource.DataSource;
import com.mobzinga.R;
import com.pojo.OfferDetails;
import com.pojo.UserMasterLogin;

public class ChangeCityActivity extends ListActivity {

	private DataSource datasource = new DataSource(this);
	private String city;

	// Progress Dialog
	private ProgressDialog pDialog;
	UserMasterLogin userLogin;
	Intent intent;
	private static final String URL = "http://www.mobzinga.com/store_management/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_city);
		
		 // Defined Array values to show in ListView
        String[] values = new String[] { "Rajkot",
        								 "Ahmedabad",
                                         "Mumbai",
                                         "Bangalore",
                                         "Delhi",
                                         "Kolkata",
                                         "Chennai",
                                         "Hyderabad" };
		
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        
        setListAdapter(adapter); 
        
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		intent = new Intent(this, EventsActivity.class);
		// ListView Clicked item value
        city = (String) getListAdapter().getItem(position);
        new ChangeCity().execute();
	}

	private class ChangeCity extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			JSONObject jsonResponse;
			
			HttpPost httpPost = new HttpPost(
					ConnectionURL.CHANGE_CITY);
			httpPost.setHeader("Content-type",
					"application/x-www-form-urlencoded; charset=UTF8");

			datasource.open();
			userLogin = datasource.readUserMaster();
			datasource.updateCity(city, userLogin.getUserID());
			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
			String formattedDate = df.format(c.getTime());
			String text = null;
			try {

				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
				nameValuePair.add(new BasicNameValuePair("userid", Integer.valueOf(userLogin.getUserID()).toString()));
				nameValuePair.add(new BasicNameValuePair("city", city));
				nameValuePair.add(new BasicNameValuePair("date", formattedDate));

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
                    offer.setOfferPicURL(ChangeCityActivity.URL + jsonChildNode.optString("companyurl").toString());
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
				return e.getLocalizedMessage();
			}
			return text;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ChangeCityActivity.this);
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
}
