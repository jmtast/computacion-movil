package com.hw.example;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class LocationService extends Service{

	public LocationManager locationManager;
	public CurrentLocationListener listener;
	public Location previousBestLocation = null;
	
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
		// TODO Auto-generated method stub
		return null;
	}

	
	public class CurrentLocationListener implements LocationListener{

		private Location lastLocation= null;
		private float minDistanceDelta = 0;// (set to 0 for debug purposes) 20; // meters
		private float minElapsedTime = 5000; // milliseconds
		
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
				Toast.makeText( getApplicationContext(), location.toString(), Toast.LENGTH_SHORT ).show();			
			}
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
