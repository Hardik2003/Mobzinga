package com.activities;
 
import java.util.List;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;

import com.datasource.DataSource;
import com.mobzinga.R;
import com.pojo.LikeMaster;
 
public class SearchLikesActivity extends PreferenceActivity {
	
	private DataSource datasource;
 
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        // get the action bar
        ActionBar actionBar = getActionBar();
 
        // Enabling Back navigation on Action Bar icon
        actionBar.setDisplayHomeAsUpEnabled(true);
 
        handleIntent(getIntent());
    }
 
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }
 
    /**
     * Handling intent data
     */
    @SuppressWarnings("deprecation")
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            addPreferencesFromResource(R.xml.likes);
            datasource = new DataSource(this);
    		datasource.open();

    		PreferenceCategory targetCategory = (PreferenceCategory) findPreference("Likes");
    		List<LikeMaster> likeList = datasource.readLikes(query);
    		for (int i = 0; i < likeList.size(); i++) {
    			// create one check box for each setting you need
    			CheckBoxPreference checkBoxPreference = new CheckBoxPreference(this);
    			// make sure each key is unique
    			checkBoxPreference.setKey(Integer.valueOf(likeList.get(i).getLikeID()).toString());
    			checkBoxPreference.setChecked(false);
    			checkBoxPreference.setTitle(likeList.get(i).getLikeName());
    			checkBoxPreference.setSummary(likeList.get(i).getLikeType());

    			targetCategory.addPreference(checkBoxPreference);
    		}

    		datasource.close();
        }
 
    }
}