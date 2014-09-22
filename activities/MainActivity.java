package com.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.datasource.DataSource;
import com.mobzinga.R;
import com.pojo.UserMasterLogin;

public class MainActivity extends Activity {

	private DataSource datasource = new DataSource(this);
	SharedPreferences appPreferences; 
	boolean isAppInstalled = false; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		appPreferences = PreferenceManager.getDefaultSharedPreferences(this); 
		isAppInstalled = appPreferences.getBoolean("isAppInstalled",false); 
		if(isAppInstalled==false){ 
		
			//Adding shortcut for MainActivity 
		    //on Home screen
		    Intent shortcutIntent = new Intent(getApplicationContext(),
		            MainActivity.class);
		     
		    shortcutIntent.setAction(Intent.ACTION_MAIN);
		 
		    Intent addIntent = new Intent();
		    addIntent
		            .putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		    addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "MobZinga");
		    addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
		            Intent.ShortcutIconResource.fromContext(getApplicationContext(),
		                    R.drawable.ic_launcher));
		 
		    addIntent
		            .setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		    getApplicationContext().sendBroadcast(addIntent);
		    
		    // finally isAppInstalled should be true. 
		    SharedPreferences.Editor editor = appPreferences.edit(); 
		    editor.putBoolean("isAppInstalled", true); 
		    editor.commit(); 
		    
		}
		
		datasource.open();
		UserMasterLogin userLogin = datasource.readUserMaster();
		Intent intent;
		if(userLogin.getUserID()==0)
			intent = new Intent(this, RegisterUserActivity.class);
		else
			intent = new Intent(this, EventsActivity.class);
		
		datasource.close();
		startActivity(intent);
	}
}
