package dc.uba.taxinow.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

import dc.uba.taxinow.AlarmReceiver;
import dc.uba.taxinow.R;
import dc.uba.taxinow.R.drawable;
import dc.uba.taxinow.R.id;
import dc.uba.taxinow.R.layout;
import dc.uba.taxinow.R.menu;
import dc.uba.taxinow.R.string;
import dc.uba.taxinow.services.LocationService;

public class MainActivity extends ActionBarActivity {

	public final static String EXTRA_MESSAGE = "dc.uba.taxinow.MESSAGE";

	public final static String NONE = "dc.uba.taxinow.NONE";
	public final static String TAXI = "dc.uba.taxinow.TAXI";
	public final static String PASSENGER = "dc.uba.taxinow.PASSENGER";

	public final static String TAXI_TEXT = "Taxi";
	public final static String PASS_TEXT = "Passenger";

	public static final String TAXI_AVAILABLE = "dc.uba.taxinow.TAXI_AVAILABLE";
	public static final String TAXI_TAKEN = "dc.uba.taxinow.TAXI_TAKEN";
	public static final String TAXI_NO_CONFIG = "dc.uba.taxinow.TAXI_NO_CONFIG";

	private PendingIntent pendingIntent;
	private AlarmManager manager;

	/*
	 * Called when the Activity becomes visible .
	 */
	@Override
	protected void onStart() {
		super.onStart();
		// Connect the client.
		ActionBar actionbar = getSupportActionBar();
		actionbar.hide();
	}

	/*
	 * Called when the Activity is no longer visible.
	 */
	@Override
	protected void onStop() {
		// Disconnecting the client invalidates it.
		super.onStop();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		// Retrieve a PendingIntent that will perform a broadcast
		Intent alarmIntent = new Intent(this, AlarmReceiver.class);
		pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

		/*
		 * Create a new location client, using the enclosing class to handle
		 * callbacks.
		 */
		SharedPreferences sharedPref = getSharedPreferences(
				getString(R.string.shared_pref_key), Context.MODE_PRIVATE);

		String userType = sharedPref.getString(getString(R.string.user_type),
				NONE);

		if (NONE.equals(userType)) {
			Intent intent = new Intent(this, UserTypeChoosingActivity.class);
			startActivity(intent);
		} else if (TAXI.equals(userType)) {

			String taxiState = sharedPref.getString(
					getString(R.string.taxi_state), TAXI_NO_CONFIG);

			if (TAXI_AVAILABLE.equals(taxiState)) {
				Intent intent = new Intent(this, TaxiAvailableActivity.class);
				startActivity(intent);
			} else if (TAXI_TAKEN.equals(taxiState)) {
				Intent intent = new Intent(this, TaxiTakenActivity.class);
				startActivity(intent);
			} else {
				Intent intent = new Intent(this, TaxiConfigActivity.class);
				intent.putExtra(EXTRA_MESSAGE, "taxi");
				startActivity(intent);
			}

		} else if (PASSENGER.equals(userType)) {
			Intent intent = new Intent(this, PassengerConfigActivity.class);
			intent.putExtra(EXTRA_MESSAGE, "passenger");
			startActivity(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	// ----------------------------- Google play services
	// -----------------------------------
	// Error handling

	// Global constants
	/*
	 * Define a request code to send to Google Play services This code is
	 * returned in Activity.onActivityResult
	 */
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	// Define a DialogFragment that displays the error dialog
	public static class ErrorDialogFragment extends DialogFragment {
		// Global field to contain the error dialog
		private Dialog mDialog;

		// Default constructor. Sets the dialog field to null
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}

		// Set the dialog to display
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}

		// Return a Dialog to the DialogFragment.
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}

	/*
	 * Handle results returned to the FragmentActivity by Google Play services
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Decide what to do based on the original request code
		switch (requestCode) {
		// ...
		case CONNECTION_FAILURE_RESOLUTION_REQUEST:
			/*
			 * If the result code is Activity.RESULT_OK, try to connect again
			 */
			switch (resultCode) {
			case Activity.RESULT_OK:
				/*
				 * Try the request again
				 */
				// ...
				break;
			}
			// ...
		}
	}

	private boolean servicesConnected() {
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);

		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d("Location Updates", "Google Play services is available.");
			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			showErrorDialog(1);

			return false;
		}
	}

	// ... ???

	private void showErrorDialog(int errorCode) {
		// Get the error code
		// int errorCode = 1; // connectionResult.getErrorCode(); // WTF
		// ???????????????????

		// Get the error dialog from Google Play services
		Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode,
				this, CONNECTION_FAILURE_RESOLUTION_REQUEST);

		// If Google Play services can provide an error dialog
		if (errorDialog != null) {
			// Create a new DialogFragment for the error dialog
			ErrorDialogFragment errorFragment = new ErrorDialogFragment();
			// Set the dialog in the DialogFragment
			errorFragment.setDialog(errorDialog);
			// Show the error dialog in the DialogFragment
			errorFragment.show(getSupportFragmentManager(), "Location Updates");
		}
	}

	public void startAlarm(View view) {
		manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		int interval = 10000;

		manager.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis(), interval, pendingIntent);
		Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
	}

	public void cancelAlarm(View view) {
		if (manager != null) {
			manager.cancel(pendingIntent);
			Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
		}
	}
}
