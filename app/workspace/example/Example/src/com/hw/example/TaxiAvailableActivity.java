package com.hw.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TaxiAvailableActivity extends ActionBarActivity {

	private boolean mIsBound;
	private LocationService mBoundService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_taxi_available);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		ActionBar actionbar = getSupportActionBar();
        actionbar.hide();
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus){
		super.onWindowFocusChanged(hasFocus);
		ListView lv = (ListView) findViewById(R.id.taxiRequestsList);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
	       	 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	       		TextView textView = (TextView) view;
	       		String travelRequest = textView.getText().toString(); 
	       		Toast.makeText( getApplicationContext(), "clicked: " + travelRequest, Toast.LENGTH_SHORT ).show();
//	       		acceptTravel(travelRequest);
	       	 }
        });
		
		SharedPreferences sharedPref = getSharedPreferences(getString(R.string.user_id_preference_key), Context.MODE_PRIVATE);
		String userId = sharedPref.getString(getString(R.string.user_id), "");
		
		TaxiRequests taxiRequests = new TaxiRequests(this,userId);
		taxiRequests.execute();
		
	}
	
	private void acceptTravel(String travelRequest){
		JSONObject jsonTravelRequest = null;
		String requestId = null;
		try {
			jsonTravelRequest = new JSONObject(travelRequest);
			requestId = jsonTravelRequest.getString("requestId");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// send to server
		// launch "not_available" activity ?
	}
	
	MyReceiver myReceiver;
	
	@Override
	protected void onStart(){
		myReceiver = new MyReceiver();
	      IntentFilter intentFilter = new IntentFilter();
	      intentFilter.addAction(LocationService.MY_ACTION);
	      registerReceiver(myReceiver, intentFilter);
	      
			SharedPreferences sharedPref = getSharedPreferences(getString(R.string.service_running_key), Context.MODE_PRIVATE);
	        boolean serviceRunning = sharedPref.getBoolean(getString(R.string.service_running), false);
	        
	        if(!serviceRunning){
	        	startService(new Intent(this, LocationService.class));
	        }
	        
	      super.onStart();
	}
	
	@Override
	protected void onStop() {
		 unregisterReceiver(myReceiver);
		 super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.taxi_available, menu);
		return true;
	}
	
	public void updateRequests(JSONArray requests){
		ListView lv = (ListView) findViewById(R.id.taxiRequestsList);

         List<Map> your_array_list = new ArrayList<Map>();
    	 for (int i = 0; i < requests.length(); i++) {
        	 Map map = new HashMap();
        	 try {
				map.put("requestId",requests.getJSONObject(i).getString("requestId"));
				map.put("positionPassenger",requests.getJSONObject(i).getString("positionPassenger"));
				map.put("passengerId",requests.getJSONObject(i).getString("passengerId"));
				map.put("passengerDestination",requests.getJSONObject(i).getString("passengerDestination"));
				your_array_list.add(map);
			} catch (JSONException e) {
				e.printStackTrace();
			}
         }

    	 ArrayList<String> requestList = new ArrayList<String>();
    	 for (int j = 0; j < requests.length(); j++) {
    		 try{
    			 if (j > 0) {
    				 requestList.add("________________________________");
    			 }
	    		 requestList.add("Pedido: " + requests.getJSONObject(j).getString("requestId"));
	    		 requestList.add("Pasajero: " + requests.getJSONObject(j).getString("passengerId"));
	    		 requestList.add("Ubicaci—n: " + requests.getJSONObject(j).getString("positionPassenger"));
	    		 requestList.add("Va hasta: " + requests.getJSONObject(j).getString("passengerDestination"));
    		 } catch (JSONException e) {
    			 e.printStackTrace();
    		 }
    	 }
    	 
         // This is the array adapter, it takes the context of the activity as a 
         // first parameter, the type of list view as a second parameter and your 
         // array as a third parameter.
         ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                 this, 
                 android.R.layout.simple_list_item_1,
                 requestList );

         if (lv != null) {
        	 lv.setAdapter(arrayAdapter);
         }
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
	
	private class MyReceiver extends BroadcastReceiver{
		 
		 @Override
		 public void onReceive(Context arg0, Intent arg1) {
		  // TODO Auto-generated method stub
		  
//		  int datapassed = arg1.getIntExtra("DATAPASSED", 0);

			 String stringExtra = arg1.getStringExtra("DATAPASSED");
			 JSONArray requests = null;
			try {
				requests = (new JSONObject(stringExtra)).getJSONArray("requests");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			TaxiAvailableActivity.this.updateRequests(requests);
		 }
		 
		}
}
