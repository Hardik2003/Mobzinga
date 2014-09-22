package com.activities;
 
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.mobzinga.R;
 
public class UserPreferencesActivity extends PreferenceActivity {
 
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        addPreferencesFromResource(R.xml.preferences);
 
    }
}