package dc.uba.taxinow.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;
import dc.uba.taxinow.Api;
import dc.uba.taxinow.R;
import dc.uba.taxinow.R.string;
import dc.uba.taxinow.activity.TaxiAvailableActivity;
import dc.uba.taxinow.utils.JsonHelper;

public class LocationService extends Service{

	public LocationManager locationManager;
	public CurrentLocationListener listener;
	public Location previousBestLocation = null;
	public TaxiAvailableActivity activity = null;
	public final static String MY_ACTION = "MY_ACTION";
	
	private Map<String, Map<String, String>> currentRequests = new HashMap<String, Map<String, String>>();
	
	@Override
	public void onCreate(){
		super.onCreate();

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    listener = new CurrentLocationListener();        
	    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, listener);
	    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, listener);
	}
	
	@Override
	public void onDestroy() {       
	    super.onDestroy();
	    locationManager.removeUpdates(listener);        
	} 
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public class CurrentLocationListener implements LocationListener{

		private Location lastLocation= null;
		private float minDistanceDelta = 0;// (set to 0 for debug purposes) 20; // meters
		private float minElapsedTime = 5000; // milliseconds		
		
		private Api api = new Api();
		
		@Override
		public void onLocationChanged(Location location) {
			boolean sendToServer = true;
			
			if(lastLocation != null){
				sendToServer = false;
				float distance = location.distanceTo(lastLocation);
				long elapsedTime = location.getTime() - lastLocation.getTime();
				if(distance > minDistanceDelta && elapsedTime > minElapsedTime){
					sendToServer = true;
				}
			}
			
			if(sendToServer){
				lastLocation = location;
				
				//Update location on server and get my current requests
				SharedPreferences sharedPref = getSharedPreferences(getString(R.string.user_id_preference_key), Context.MODE_PRIVATE);
				final String userId = sharedPref.getString(getString(R.string.user_id), "");
				
//				GetTaxiRequestsAsyncTask getTaxiRequestsAsyncTask = new GetTaxiRequestsAsyncTask(this, userId, location);
//				getTaxiRequestsAsyncTask.execute();				
				sendToServer(userId, location);
			}
		}
		
		private void sendToServer(final String userId, final Location location){
			(new AsyncTask<String, Void, JSONObject>() {
				@Override
				protected JSONObject doInBackground(String... params) {
					JSONObject requests = api.updatePosition(userId, location);
					
					updateRequests(requests);
					broadcastRequests(requests);
					
					return requests;
				}
			}).execute();
		}
		
		private void broadcastRequests(JSONObject requests){
			Intent intent = new Intent();
			intent.setAction(MY_ACTION);
			intent.putExtra("DATAPASSED", requests.toString());				      
			sendBroadcast(intent);
		}
		
		private void updateRequests(JSONObject requests){
			List<Map<String,String>> taxiRequestsList = JsonHelper.parseTaxiRequestsList(requests);
			Map<String, Map<String, String>> newRequests = new HashMap<String, Map<String, String>>();
			boolean hasChanged = false;
			for (Map<String, String> req : taxiRequestsList) {
				newRequests.put(req.get("requestId"), req);
				if(!currentRequests.containsKey(req.get("requestId"))){
					hasChanged = true;
				}
			}
			
			// the hasChanged part covers the case where size is the same but taxi requests are different, 
			// the size part covers the case were all previous requests are still there but there are more
			if(hasChanged || taxiRequestsList.size() != currentRequests.size()){
				// notification
			}
		}
		
		// not used in this version
		public void sendData(JSONObject requests){
			Intent intent = new Intent();
			intent.setAction(MY_ACTION);
			intent.putExtra("DATAPASSED", requests.toString());
		      
			sendBroadcast(intent);
		}

		@Override
		public void onProviderDisabled(String arg0) {
			Toast.makeText( getApplicationContext(), "Provider Disabled", Toast.LENGTH_SHORT ).show();
		}

		@Override
		public void onProviderEnabled(String arg0) {
			Toast.makeText( getApplicationContext(), "Provider Enabled", Toast.LENGTH_SHORT ).show();
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
		}
		
	}
}
