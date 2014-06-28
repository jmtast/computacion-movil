package com.hw.example;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;

public class AvailableTaxisActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_available_taxis);
		
		ActionBar actionbar = getActionBar();
        actionbar.hide();
	}
}
