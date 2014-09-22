package com.custompreference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

@SuppressLint("SimpleDateFormat")
public class TimePreference extends DialogPreference {
	private int lastHour = 0;
	private int lastMinute = 0;
	private TimePicker picker = null;

	public static int getHour(String time) {
		String[] pieces = time.split(":");

		return (Integer.parseInt(pieces[0]));
	}

	public static int getMinute(String time) {
		String[] pieces = time.split(":");

		return (Integer.parseInt(pieces[1]));
	}

	public TimePreference(Context ctxt, AttributeSet attrs) {
		super(ctxt, attrs);

		setPositiveButtonText("Set");
		setNegativeButtonText("Cancel");
	}

	@Override
	protected View onCreateDialogView() {
		picker = new TimePicker(getContext());

		return (picker);
	}

	@Override
	protected void onBindDialogView(View v) {
		super.onBindDialogView(v);

		picker.setCurrentHour(lastHour);
		picker.setCurrentMinute(lastMinute);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		
		if (positiveResult) {
			
			SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences( getContext() );
			SharedPreferences.Editor editor = mPrefs.edit();
			
			lastHour = picker.getCurrentHour();
			lastMinute = picker.getCurrentMinute();

			String time = String.valueOf(lastHour) + ":"
					+ String.valueOf(lastMinute);

			if (callChangeListener(time)) {
				editor.putInt( "Hour", lastHour );
				editor.putInt( "Minute", lastMinute );
				editor.commit();
				persistString(time);
			}
		}
		setSummary(getSummary());
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return (a.getString(index));
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		String time = null;

		if (restoreValue) {
			if (defaultValue == null) {
				time = getPersistedString("00:00");
			} else {
				time = getPersistedString(defaultValue.toString());
			}
		} else {
			time = defaultValue.toString();
		}

		lastHour = getHour(time);
		lastMinute = getMinute(time);
		setSummary(getSummary());
	}

	@Override
	public CharSequence getSummary() {
		Date date = new Date();
		java.text.DateFormat f = DateFormat.getTimeFormat(getContext());
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
			date = sdf.parse(lastHour + ":" + lastMinute);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return String.format(f.format(date));
	}
}