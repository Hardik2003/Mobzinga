package com.helper;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobzinga.R;

public class LazyAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    
    public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public HashMap<String, String> getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_item, null);

        TextView companyName = (TextView)vi.findViewById(R.id.companyname); // Company Name
        TextView storeAddress = (TextView)vi.findViewById(R.id.storeaddress); // Store Address
        TextView description = (TextView)vi.findViewById(R.id.description); // Offer Description
        TextView startDate = (TextView)vi.findViewById(R.id.startdate); // Offer Start Date
        TextView endDate = (TextView)vi.findViewById(R.id.enddate); // Offer End Date
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.thumbnail); // thumb image
        TextView distanceToStoreString=(TextView)vi.findViewById(R.id.distancetostore); // Distance to Store String
        TextView distanceToStore=(TextView)vi.findViewById(R.id.distancetostorevalue); // Distance to Store
        
        HashMap<String, String> offer = new HashMap<String, String>();
        offer = data.get(position);
        
        // Setting all values in listview
        companyName.setText(offer.get("name"));
        storeAddress.setText(offer.get("address"));
        description.setText(offer.get("description"));
        startDate.setText(offer.get("startdate"));
        endDate.setText(offer.get("enddate"));
        if(offer.containsKey("distance")){
        	distanceToStoreString.setVisibility(View.VISIBLE);
        	distanceToStore.setVisibility(View.VISIBLE);
        	distanceToStore.setText(offer.get("distance"));
        }
        else{
        	distanceToStoreString.setVisibility(View.INVISIBLE);
        	distanceToStore.setVisibility(View.INVISIBLE);
        }
        imageLoader.DisplayImage(offer.get("url"), thumb_image);
        return vi;
    }
}