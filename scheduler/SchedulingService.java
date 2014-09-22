package com.scheduler;

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

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.activities.EventsActivity;
import com.constants.ConnectionURL;
import com.datasource.DataSource;
import com.mobzinga.R;
import com.pojo.OfferDetails;
import com.pojo.UserMasterLogin;

/**
 * This {@code IntentService} does the app's actual work.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class SchedulingService extends IntentService {
	
	private static final String URL = "http://www.mobzinga.com/store_management/";
	
    public SchedulingService() {
        super("SchedulingService");
    }
    
    @Override
    protected void onHandleIntent(Intent intent) {
    	DataSource datasource = new DataSource(this);
    	
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
                offer.setOfferPicURL(SchedulingService.URL + jsonChildNode.optString("companyurl").toString());
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
            
           /*Intent showIntent = new Intent(this, EventsActivity.class);
           PendingIntent contentIntent = PendingIntent.getActivity(this, 0, showIntent, 0);*/ 
            
           NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
           //mBuilder.setContentIntent(contentIntent);
           mBuilder.setAutoCancel(true);
           mBuilder.setSmallIcon(R.drawable.ic_launcher_notification);
           mBuilder.setContentTitle("MobZinga");
           mBuilder.setContentText("Congratulations!! Yours offers just got updated");
           mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
           
           Intent resultIntent = new Intent(this, EventsActivity.class);
           TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
           stackBuilder.addParentStack(EventsActivity.class);

           // Adds the Intent that starts the Activity to the top of the stack
           stackBuilder.addNextIntent(resultIntent);
           PendingIntent resultPendingIntent =
                   stackBuilder.getPendingIntent(
                       0,
                       PendingIntent.FLAG_UPDATE_CURRENT
                   );
           mBuilder.setContentIntent(resultPendingIntent);
           
           Notification notification = mBuilder.build();
           notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
           
           NotificationManager mNotificationManager =
        		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        		    
           // notificationID allows you to update the notification later on.
           //mNotificationManager.notify(0, mBuilder.build());
           mNotificationManager.notify(0, notification);
           
        }
        catch(Exception e){
        	System.out.println(e.getLocalizedMessage());
        }
    }
}
