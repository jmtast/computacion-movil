package dc.uba.taxinow.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import dc.uba.taxinow.AlarmReceiver;
import dc.uba.taxinow.R;
import dc.uba.taxinow.R.id;
import dc.uba.taxinow.R.layout;
import dc.uba.taxinow.R.menu;
import dc.uba.taxinow.R.string;
import dc.uba.taxinow.asynctasks.TaxiRequestAsyncTask;
import dc.uba.taxinow.model.AvailableTaxis;
import dc.uba.taxinow.model.TravelRequest;

public class SearchingTaxisActivity extends ActionBarActivity implements
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener{

	private LocationClient mLocationClient;
	
	@Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mLocationClient.connect();
    }
		
	@Override
	protected void onStop() {
	    // Disconnecting the client invalidates it.
	    mLocationClient.disconnect();
	    stopAlarm();
	    super.onStop();
	}
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searching_taxis);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		ActionBar actionbar = getSupportActionBar();
        actionbar.hide();
        
        mLocationClient = new LocationClient(this, this, this);  
        
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
	}

	private void postTaxiRequest() {
		Intent intent = getIntent();
        String destination = intent.getStringExtra(getString(R.string.extra_destination));
        Location origin = mLocationClient.getLastLocation();
    	
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_pref_key), Context.MODE_PRIVATE);
		String userId = sharedPref.getString(getString(R.string.user_id), "");
        
        TaxiRequestAsyncTask taxiRequestAsyncTask = new TaxiRequestAsyncTask(this);
        taxiRequestAsyncTask.execute(new TravelRequest(origin, destination,userId));
        
       //Hay que empezar a consultar si alguien acepto mi request
        startAlarm();
	}
	
	private AlarmManager manager;
	private PendingIntent pendingIntent;
	
	 public void startAlarm() {
	        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
	        int interval = 10000;

	        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
	        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
	    }
	    
    public void stopAlarm() {
        if (manager != null) {
            manager.cancel(pendingIntent);
            Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
        }
    }
	
	public void processAvailableTaxis(AvailableTaxis availableTaxis){
		// call next activity
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.searching_taxis, menu);
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_searching_taxis,
					container, false);
			return rootView;
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Toast.makeText(this, "Connected FAILED", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConnected(Bundle arg0) {
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        
        postTaxiRequest();
	}

	@Override
	public void onDisconnected() {
        Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
	}

}
