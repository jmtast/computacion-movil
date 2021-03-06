package com.hw.example;

import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class UserTypeChoosingActivity extends ActionBarActivity {
	
	public static SharedPreferences sharedPref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_type_choosing);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		ActionBar actionbar = getSupportActionBar();
        actionbar.hide();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_type_choosing, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void chooseTaxi(View view){
		Toast.makeText(this, "Contactando al servidor. Por favor espere...", Toast.LENGTH_SHORT).show();
		saveUserType(MainActivity.TAXI);
		
		Register register = new Register(this, TaxiConfigActivity.class,"taxi");
		register.execute(MainActivity.TAXI_TEXT);
	}
	
	public void finishRegistration(Class nextActivityClass, String type, String newId){
		saveUserId(newId);
		
		Intent intent = new Intent(this, nextActivityClass);
    	intent.putExtra(MainActivity.EXTRA_MESSAGE, type);
    	startActivity(intent);
	}
	

	public void choosePassenger(View view){
		Toast.makeText(this, "Contactando al servidor. Por favor espere...", Toast.LENGTH_SHORT).show();
		saveUserType(MainActivity.PASSENGER);
		
		Register register = new Register(this, PassengerConfigActivity.class, "passenger");
		register.execute(MainActivity.PASS_TEXT);
	}
	
    public void sendData(View view) throws InterruptedException, ExecutionException {
    	// Create a new HttpClient and Post Header
		Retrievedata retrieveData = new Retrievedata(this);
		retrieveData.execute();
    }
    
    public void setData(String result){
    	Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
    }
    
    private void saveUserId(String newId) {
		SharedPreferences sharedPref = getSharedPreferences(getString(R.string.user_id_preference_key), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(getString(R.string.user_id), newId);
		editor.commit();
	}
	
	private void saveUserType(String userType){
		sharedPref = getSharedPreferences(getString(R.string.user_type_preference_key), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(getString(R.string.user_type), userType);
		editor.commit();
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_user_type_choosing, container, false);
			return rootView;
		}
	}

}
