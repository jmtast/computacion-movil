package dc.uba.taxinow.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import dc.uba.taxinow.R;
import dc.uba.taxinow.R.id;
import dc.uba.taxinow.R.layout;
import dc.uba.taxinow.R.menu;
import dc.uba.taxinow.R.string;
import dc.uba.taxinow.asynctasks.TaxiRequests;
import dc.uba.taxinow.services.LocationService;
import dc.uba.taxinow.utils.JsonHelper;

public class TaxiAvailableActivity extends ActionBarActivity {

	public static final String FROM_TRAVEL_LIST = "dc.uba.taxinow.FROM_TRAVEL_LIST";
		
	private NewRequestsReceiver newRequestsReceiver;
	private TravelRequestTakenReceiver travelRequestTakenReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_taxi_available);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		ListView lv = (ListView) findViewById(R.id.taxiRequestsList);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView textView = (TextView) view;
				String travelRequest = textView.getText().toString();
				Toast.makeText(getApplicationContext(),
						"clicked: " + travelRequest, Toast.LENGTH_SHORT).show();
				 
				acceptTravel(travelRequest);
			}
		});

		SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_pref_key),Context.MODE_PRIVATE);
		String userId = sharedPref.getString(getString(R.string.user_id), "");

		TaxiRequests taxiRequests = new TaxiRequests(this, userId);
		taxiRequests.execute();

	}

	private void acceptTravel(String travelRequest) {
		JSONObject jsonTravelRequest = null;
		String requestId = null;
		try {
			jsonTravelRequest = new JSONObject(travelRequest);
			requestId = jsonTravelRequest.getString("requestId");
			requestId = jsonTravelRequest.getString("passengerId");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_pref_key),Context.MODE_PRIVATE);
		String userId = sharedPref.getString(getString(R.string.user_id), "");
		
		// send to server
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... arg0) {
				
				return null;
			}
			
		};
		// launch taxi taken activity through receiver callback
	}

	public void launchTaxiTakenActivity(String data){
		stopService(new Intent(this, LocationService.class));
		
		Intent intent = new Intent(this, TaxiTakenActivity.class);
		startActivity(intent);
	}
	
	@Override
	protected void onStart() {
		newRequestsReceiver = new NewRequestsReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(LocationService.NEW_REQUESTS_ACTION);
		registerReceiver(newRequestsReceiver, intentFilter);
		
		travelRequestTakenReceiver = new TravelRequestTakenReceiver();
		intentFilter = new IntentFilter();
		intentFilter.addAction(LocationService.TRAVEL_REQUEST_TAKEN_ACTION);
		registerReceiver(travelRequestTakenReceiver, intentFilter);

		SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_pref_key), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(getString(R.string.taxi_state), MainActivity.TAXI_AVAILABLE);
		editor.commit();
		
		startService(new Intent(this, LocationService.class));

		super.onStart();
	}

	@Override
	protected void onStop() {
		unregisterReceiver(newRequestsReceiver);
		unregisterReceiver(travelRequestTakenReceiver);
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.taxi_available, menu);
		return true;
	}

	public void updateRequests(String requests) {
		ListView lv = (ListView) findViewById(R.id.taxiRequestsList);

		List<Map<String, String>> travelRequests = JsonHelper
				.parseTaxiRequestsList(requests);

		// This is the array adapter, it takes the context of the activity as a
		// first parameter, the type of list view as a second parameter and your
		// array as a third parameter.
		ArrayAdapter<Map<String, String>> arrayAdapter = new ArrayAdapter<Map<String, String>>(
				this, android.R.layout.simple_list_item_1, travelRequests);

		if (lv != null) {
			lv.setAdapter(arrayAdapter);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	        case R.id.action_config:
	            openConfig();
	            return true;
	        case R.id.action_shutdown:
	            shutDown();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
		}
	}

	private void openConfig() {
		Intent intent = new Intent(this, TaxiConfigActivity.class);
		intent.putExtra(FROM_TRAVEL_LIST, FROM_TRAVEL_LIST);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);		
	}

	private void shutDown() {
		stopService(new Intent(this, LocationService.class));
		this.finish();
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
			View rootView = inflater.inflate(R.layout.fragment_taxi_available,
					container, false);
			return rootView;
		}
	}

	private class NewRequestsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			String stringExtra = arg1.getStringExtra(LocationService.NEW_REQUESTS_DATA);
			updateRequests(stringExtra);
		}

	}
	
	private class TravelRequestTakenReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			String stringExtra = arg1.getStringExtra(LocationService.TRAVEL_REQUEST_TAKEN_DATA);
			launchTaxiTakenActivity(stringExtra);
		}

	}
}
