package com.activities;

import java.util.List;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;

import com.datasource.DataSource;
import com.mobzinga.R;
import com.pojo.LikeMaster;

public class LikeMasterActivity extends PreferenceActivity {

	private DataSource datasource;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int j=0;
		PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(this);
		datasource = new DataSource(this);
		datasource.open();
		List<String> likeListTypes = datasource.readLikeTypes();
		List<LikeMaster> likeList = datasource.readLikes("");
		
		for(int i=0; i<likeListTypes.size();i++){
			PreferenceCategory targetCategory = new PreferenceCategory(this);
			targetCategory.setTitle(likeListTypes.get(i));
			screen.addPreference(targetCategory);
			while(likeList.size()!=0 && likeList.get(j).getLikeType().equalsIgnoreCase(likeListTypes.get(i))){
				// create one check box for each setting you need
				CheckBoxPreference checkBoxPreference = new CheckBoxPreference(this);
				// make sure each key is unique
				checkBoxPreference.setKey(Integer.valueOf(likeList.get(j).getLikeID()).toString());
				checkBoxPreference.setChecked(true);
				checkBoxPreference.setTitle(likeList.get(j).getLikeName());

				targetCategory.addPreference(checkBoxPreference);
				likeList.remove(j);
			}
		}

		datasource.close();
		setPreferenceScreen(screen);
		
		/*addPreferencesFromResource(R.xml.likes);
		datasource = new DataSource(this);
		datasource.open();

		PreferenceCategory targetCategory = (PreferenceCategory) findPreference("Likes");
		List<LikeMaster> likeList = datasource.readLikes("");
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

		datasource.close();*/
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.likes_master, menu);
 
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
 
        return super.onCreateOptionsMenu(menu);
    }
}
