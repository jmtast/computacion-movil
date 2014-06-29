package dc.uba.taxinow.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.Toast;
import dc.uba.taxinow.Api;
import dc.uba.taxinow.R;
import dc.uba.taxinow.activity.ShutdownActivity;
import dc.uba.taxinow.activity.TaxiAvailableActivity;
import dc.uba.taxinow.application.LifecycleHandler;
import dc.uba.taxinow.utils.JsonHelper;

public class LocationService extends Service {

	public LocationManager locationManager;
	public CurrentLocationListener listener;
	public Location previousBestLocation = null;
	public TaxiAvailableActivity activity = null;

	public static final String NEW_REQUESTS_ACTION = "dc.uba.taxinow.NEW_REQUESTS_ACTION";
	public static final String NEW_REQUESTS_DATA = "dc.uba.taxinow.NEW_REQUESTS_DATA";

	private static final Integer TRAVEL_REQUESTS_NOTIFICATION_ID = 90003456;

	private Map<String, Map<String, String>> currentRequests = new HashMap<String, Map<String, String>>();

	@Override
	public void onCreate() {
		super.onCreate();

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		listener = new CurrentLocationListener();
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 2000, 0, listener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				2000, 0, listener);
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

	public class CurrentLocationListener implements LocationListener {

		private Location lastLocation = null;
		private float minDistanceDelta = 0;// (set to 0 for debug purposes) 20;
											// // meters
		private float minElapsedTime = 5000; // milliseconds

		private Api api = new Api();

		@Override
		public void onLocationChanged(Location location) {
			boolean sendToServer = true;

			if (lastLocation != null) {
				sendToServer = false;
				float distance = location.distanceTo(lastLocation);
				long elapsedTime = location.getTime() - lastLocation.getTime();
				if (distance > minDistanceDelta && elapsedTime > minElapsedTime) {
					sendToServer = true;
				}
			}

			if (sendToServer) {
				lastLocation = location;

				// Update location on server and get my current requests
				SharedPreferences sharedPref = getSharedPreferences(
						getString(R.string.shared_pref_key),
						Context.MODE_PRIVATE);
				final String userId = sharedPref.getString(
						getString(R.string.user_id), "");

				sendToServer(userId, location);
			}
		}

		private void sendToServer(final String userId, final Location location) {
			(new AsyncTask<String, Void, JSONObject>() {
				@Override
				protected JSONObject doInBackground(String... params) {
					JSONObject requests = api.updatePosition(userId, location);

					if(requests != null){
						updateRequests(requests);
						broadcastRequests(requests);
					}

					return requests;
				}
			}).execute();
		}

		private void broadcastRequests(JSONObject requests) {
			Intent intent = new Intent();
			intent.setAction(NEW_REQUESTS_ACTION);
			intent.putExtra(NEW_REQUESTS_DATA, requests.toString());
			sendBroadcast(intent);
		}

		private void updateRequests(JSONObject requests) {
			List<Map<String, String>> taxiRequestsList = JsonHelper
					.parseTaxiRequestsList(requests);
			Map<String, Map<String, String>> newRequests = new HashMap<String, Map<String, String>>();
			boolean hasChanged = false;
			for (Map<String, String> req : taxiRequestsList) {
				newRequests.put(req.get("requestId"), req);
				if (!currentRequests.containsKey(req.get("requestId"))) {
					hasChanged = true;

				}
			}

			// the hasChanged part covers the case where size is the same but
			// taxi requests are different,
			// the size part covers the case were all previous requests are
			// still there but there are more
			boolean doNotify = (hasChanged || taxiRequestsList.size() != currentRequests.size()) && 
					!LifecycleHandler.isApplicationInForeground();

			currentRequests = newRequests;
			
			if (doNotify) {
				launchNotification();
			}
		}

		public void launchNotification() {
			
			String msg = (currentRequests.size() == 1) ? "Hay 1 viaje disponible" : 
				"Hay " + currentRequests.size() + " viajes disponibles";

			NotificationCompat.Builder builder = new NotificationCompat.Builder(
					LocationService.this).setSmallIcon(R.drawable.taxinowicon)
					.setContentTitle("Nuevas solicitudes de viaje")
					.setContentText(msg)
					.setAutoCancel(true);

			Intent notifyIntent = new Intent(LocationService.this,
					TaxiAvailableActivity.class);

			notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);

			PendingIntent pendingIntent = PendingIntent.getActivity(
					LocationService.this, 0, notifyIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			builder.setContentIntent(pendingIntent);
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(TRAVEL_REQUESTS_NOTIFICATION_ID,
					builder.build());
		}

		@Override
		public void onProviderDisabled(String arg0) {
			Toast.makeText(getApplicationContext(), "Provider Disabled",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onProviderEnabled(String arg0) {
			Toast.makeText(getApplicationContext(), "Provider Enabled",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
		}

	}
}
