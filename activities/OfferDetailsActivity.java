package com.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.helper.ImageLoader;
import com.mobzinga.R;

public class OfferDetailsActivity extends Activity{

	public ImageLoader imageLoader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_offer_details);
		
		imageLoader=new ImageLoader(getApplicationContext());
		ImageView image=(ImageView)findViewById(R.id.list_image_details); // full image
		imageLoader.DisplayImage(getIntent().getExtras().getString("url"), image);
		
		TextView startdate = (TextView) findViewById(R.id.startdate_details);
		startdate.setText(getIntent().getExtras().getString("startdate"));
		
		TextView enddate = (TextView) findViewById(R.id.enddate_details);
		enddate.setText(getIntent().getExtras().getString("enddate"));
		
		TextView address = (TextView) findViewById(R.id.storeaddress_details);
		address.setText(getIntent().getExtras().getString("address"));
		
		TextView description = (TextView) findViewById(R.id.description_details);
		description.setText(getIntent().getExtras().getString("description"));
		
		TextView name = (TextView) findViewById(R.id.companyname_details);
		name.setText(getIntent().getExtras().getString("name"));
	}
}
