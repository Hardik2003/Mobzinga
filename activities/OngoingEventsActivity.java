package com.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class OngoingEventsActivity extends Fragment implements PopulateData{

	DataSource datasource;
	List<HashMap<String, String>> offers;
	ListView listView;
	LazyAdapter adapter;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

		datasource = new DataSource(getActivity());
		offers = new ArrayList<HashMap<String,String>>();
		datasource.open();
		offers = datasource.readOffers(0, 0, 0);
	    
        View rootView = inflater.inflate(R.layout.activity_ongoing_events, container, false);
        listView=(ListView)rootView.findViewById(R.id.list);
		
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
		        startActivity(newActivity1);
			}});
        
        /*listView.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            @Override
            public void onSwipeLeft() {
            	Toast.makeText(getActivity(), "left", Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onSwipeRight() {
            	Toast.makeText(getActivity(), "right", Toast.LENGTH_SHORT).show();
            }
        });*/
         
        return rootView;
    }
	
	@Override
	public void onStart() {
		super.onStart();
		
		UserMasterLogin user = datasource.readUserMaster();
        TextView text = (TextView) getView().findViewById(R.id.city_ongoing);
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
	public void refresh(){
		offers.clear();
		offers.addAll(datasource.readOffers(0, 0, 0));
		getActivity().runOnUiThread(new Runnable() {
		    public void run() {
		        adapter.notifyDataSetChanged();
		    }
		});
	}
}
