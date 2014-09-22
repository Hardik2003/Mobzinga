package com.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.datasource.DataSource;
import com.helper.LazyAdapter;
import com.interfaces.PopulateData;
import com.mobzinga.R;
import com.pojo.UserMasterLogin;

public class GPSEventsActivity extends Fragment implements PopulateData{

	private static final int RESULT_SETTINGS = 1;
	private DataSource datasource;
	List<HashMap<String, String>> offers;
	ListView listView;
	LazyAdapter adapter;
	
	// GPSTracker class
    static GPSTracker gps;
    SharedPreferences sharedPrefs;
    
    @Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (!isVisibleToUser) {
			if(gps!=null){
				if(gps.canGetLocation()){
					//Toast.makeText(getActivity(), "Stopping GPS", Toast.LENGTH_LONG).show();
					gps.stopUsingGPS();
				}
			}
		}
    }	
    
    @Override
	public void onStart() {
		super.onStart();
		
		UserMasterLogin user = datasource.readUserMaster();
        TextView text = (TextView) getView().findViewById(R.id.city_gps);
        text.setText(user.getUserLocation());
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	Intent intent = new Intent(getActivity(), ChangeCityActivity.class);
        		startActivity(intent);
            }
        });
	}
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

    	sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity().getApplicationContext());
    	
 		datasource = new DataSource(getActivity());
		offers = new ArrayList<HashMap<String,String>>();
		datasource.open();
		gps = new GPSTracker(getActivity());
	
		View rootView = inflater.inflate(R.layout.activity_gps_events, container, false);
		
		// check if GPS enabled     
        if(gps.canGetLocation()){
             
        	//Toast.makeText(getActivity(), gps.getLatitude() + "" + gps.getLongitude(), Toast.LENGTH_LONG).show();
        	offers.clear();
        	if(sharedPrefs.contains("prefOfferDistance"))
        		offers.addAll(datasource.readOffers(gps.getLatitude(), gps.getLongitude(), Float.parseFloat(sharedPrefs.getString("prefOfferDistance","100"))));
        	else
        		offers.addAll(datasource.readOffers(gps.getLatitude(), gps.getLongitude(), 100));
    		
    		listView=(ListView)rootView.findViewById(R.id.list_gps);
            adapter=new LazyAdapter(getActivity(), (ArrayList<HashMap<String, String>>) offers);        
            listView.setAdapter(adapter);
            
            // Click event for single list row
            listView.setOnItemClickListener(new OnItemClickListener() {

    			@Override
    			public void onItemClick(AdapterView<?> arg0, View v, int position,
    					long id) {
    				//get selected items
    				Map<?, ?> selectedValue = (HashMap<?, ?>) adapter.getItem(position);
    				Intent newActivity1 = new Intent(getActivity(), OfferDetailsActivity.class);
    		        newActivity1.putExtra("startdate", selectedValue.get("startdate").toString());
    		        newActivity1.putExtra("enddate", selectedValue.get("enddate").toString());
    		        newActivity1.putExtra("address", selectedValue.get("address").toString());
    		        newActivity1.putExtra("description", selectedValue.get("description").toString());
    		        newActivity1.putExtra("name", selectedValue.get("name").toString());
    		        newActivity1.putExtra("url", selectedValue.get("url").toString());
    		        //startActivity(newActivity1);
    		        startActivityForResult(newActivity1,1);
    			}});
            
        }else{
            gps.showSettingsAlert();
        }
        
        return rootView;
    }
    
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case RESULT_SETTINGS:
			refresh();
			break;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if(gps!=null){
			//Toast.makeText(getActivity(), "Stopping GPS", Toast.LENGTH_LONG).show();
			gps.stopUsingGPS();
		}
	}
	
	public class GPSTracker extends Service implements LocationListener {
		 
	    private final Context mContext;
	 
	    // flag for GPS status
	    boolean isGPSEnabled = false;
	 
	    // flag for network status
	    boolean isNetworkEnabled = false;
	 
	    // flag for GPS status
	    boolean canGetLocation = false;
	 
	    Location location; // location
	    double latitude; // latitude
	    double longitude; // longitude
	 
	    // The minimum distance to change Updates in meters
	    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
	 
	    // The minimum time between updates in milliseconds
	    private static final long MIN_TIME_BW_UPDATES = 500 * 6 * 1; // 3 seconds
	 
	    // Declaring a Location Manager
	    protected LocationManager locationManager;
	    
	    public GPSTracker(Context context) {
	        this.mContext = context;
	        getLocation();
	    }
	 
	    public Location getLocation() {
	        try {
	            locationManager = (LocationManager) mContext
	                    .getSystemService(LOCATION_SERVICE);
	 
	            // getting GPS status
	            isGPSEnabled = locationManager
	                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
	 
	            // getting network status
	            isNetworkEnabled = locationManager
	                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	 
	            if (!isGPSEnabled && !isNetworkEnabled) {
	                // no network provider is enabled
	            } else {
	                this.canGetLocation = true;
	                // First get location from Network Provider
	                if (isNetworkEnabled) {
	                    locationManager.requestLocationUpdates(
	                            LocationManager.NETWORK_PROVIDER,
	                            MIN_TIME_BW_UPDATES,
	                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
	                    Log.d("Network", "Network");
	                    if (locationManager != null) {
	                        location = locationManager
	                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	                        if (location != null) {
	                            latitude = location.getLatitude();
	                            longitude = location.getLongitude();
	                        }
	                    }
	                }
	                // if GPS Enabled get lat/long using GPS Services
	                if (isGPSEnabled) {
	                    if (location == null) {
	                        locationManager.requestLocationUpdates(
	                                LocationManager.GPS_PROVIDER,
	                                MIN_TIME_BW_UPDATES,
	                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
	                        Log.d("GPS Enabled", "GPS Enabled");
	                        if (locationManager != null) {
	                            location = locationManager
	                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
	                            if (location != null) {
	                                latitude = location.getLatitude();
	                                longitude = location.getLongitude();
	                            }
	                        }
	                    }
	                }
	            }
	 
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	 
	        return location;
	    }
	     
	    /**
	     * Stop using GPS listener
	     * Calling this function will stop using GPS in your app
	     * */
	    public void stopUsingGPS(){
	        if(locationManager != null){
	            locationManager.removeUpdates(GPSTracker.this);
	        }       
	    }
	     
	    /**
	     * Function to get latitude
	     * */
	    public double getLatitude(){
	        if(location != null){
	            latitude = location.getLatitude();
	        }
	         
	        // return latitude
	        return latitude;
	    }
	     
	    /**
	     * Function to get longitude
	     * */
	    public double getLongitude(){
	        if(location != null){
	            longitude = location.getLongitude();
	        }
	         
	        // return longitude
	        return longitude;
	    }
	     
	    /**
	     * Function to check GPS/wifi enabled
	     * @return boolean
	     * */
	    public boolean canGetLocation() {
	        return this.canGetLocation;
	    }
	     
	    /**
	     * Function to show settings alert dialog
	     * On pressing Settings button will launch Settings Options
	     * */
	    public void showSettingsAlert(){
	        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
	      
	        // Setting Dialog Title
	        alertDialog.setTitle("GPS is settings");
	  
	        // Setting Dialog Message
	        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
	  
	        // On pressing Settings button
	        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog,int which) {
	                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	                mContext.startActivity(intent);
	            }
	        });
	  
	        // on pressing cancel button
	        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            dialog.cancel();
	            }
	        });
	  
	        // Showing Alert Message
	        alertDialog.show();
	    }
	 
	    @Override
	    public void onLocationChanged(Location location) {
	    	if(canGetLocation()){
	    		
	    		//Toast.makeText(getActivity(), location.getLatitude() + "" + location.getLongitude(), Toast.LENGTH_LONG).show();
	    		offers.clear();
				offers.addAll(datasource.readOffers(location.getLatitude(), location.getLongitude(), Float.parseFloat(sharedPrefs.getString("prefOfferDistance","100"))));
				getActivity().runOnUiThread(new Runnable() {
				    public void run() {
				        adapter.notifyDataSetChanged();
				    }
				});
				
	        }else{
	            // can't get location
	            // GPS or Network is not enabled
	            // Ask user to enable GPS/network in settings
	            showSettingsAlert();
	        }
	    }
	 
	    @Override
	    public void onProviderDisabled(String provider) {
	    }
	 
	    @Override
	    public void onProviderEnabled(String provider) {
	    }
	 
	    @Override
	    public void onStatusChanged(String provider, int status, Bundle extras) {
	    }
	 
	    @Override
	    public IBinder onBind(Intent arg0) {
	        return null;
	    }
	}
	
	@Override
	public void refresh(){
		gps = new GPSTracker(getActivity());
		// check if GPS enabled     
        if(gps.canGetLocation()){

        	//Toast.makeText(getActivity(), gps.getLatitude() + "" + gps.getLongitude(), Toast.LENGTH_LONG).show();
        	offers.clear();
        	offers.addAll(datasource.readOffers(gps.getLatitude(), gps.getLongitude(), Float.parseFloat(sharedPrefs.getString("prefOfferDistance","100"))));
        	getActivity().runOnUiThread(new Runnable() {
        		public void run() {
        			adapter.notifyDataSetChanged();
        		}
        	});
			
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
	}
}
