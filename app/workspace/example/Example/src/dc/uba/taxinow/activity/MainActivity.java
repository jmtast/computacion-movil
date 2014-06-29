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

public class MainActivity extends ActionBarActivity implements
    GooglePlayServicesClient.ConnectionCallbacks,
    GooglePlayServicesClient.OnConnectionFailedListener{

    public final static String EXTRA_MESSAGE = "dc.uba.taxinow.MESSAGE";
    
    public final static String NONE = "dc.uba.taxinow.NONE";
    public final static String TAXI = "dc.uba.taxinow.TAXI";
    public final static String PASSENGER = "dc.uba.taxinow.PASSENGER";
    
    public final static String TAXI_TEXT = "Taxi";
    public final static String PASS_TEXT = "Passenger";
    
    private LocationClient mLocationClient;
    private PendingIntent pendingIntent;
    private AlarmManager manager;
    
    /*
     * Called when the Activity becomes visible .
     */
    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mLocationClient.connect();
        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();
    }
    
    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }
    
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        // Retrieve a PendingIntent that will perform a broadcast
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        
        /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        mLocationClient = new LocationClient(this, this, this);
        
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_pref_key), Context.MODE_PRIVATE);
        
        String userType = sharedPref.getString(getString(R.string.user_type), NONE);
        
        if(NONE.equals(userType)){
            Intent intent = new Intent(this, UserTypeChoosingActivity.class);
            startActivity(intent);
        }else if(TAXI.equals(userType)){
        	String taxiConfigSaved = sharedPref.getString(getString(R.string.taxi_config_saved), "");
        	if(getString(R.string.taxi_config_saved).equals(taxiConfigSaved)){
        		Intent intent = new Intent(this, TaxiAvailableActivity.class);
                startActivity(intent);
        	}else{
        		Intent intent = new Intent(this, TaxiConfigActivity.class);
        		intent.putExtra(EXTRA_MESSAGE, "taxi");
        		startActivity(intent);
        	}
        }else if(PASSENGER.equals(userType)){
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
    
    /** Called when the user clicks the Send button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, PassengerConfigActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);  
    }
    
    /** Called when the user clicks the Button button */
    public void updateLocation(View view) {
    	TextView textView= (TextView) findViewById(R.id.textView1);
        Location location = mLocationClient.getLastLocation();
        textView.setText(location.toString());
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
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    
    
    //----------------------------- Google play services -----------------------------------
    // Error handling
    
 // Global constants
    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
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
     * Handle results returned to the FragmentActivity
     * by Google Play services
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
//            ...
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
                switch (resultCode) {
                    case Activity.RESULT_OK :
                    /*
                     * Try the request again
                     */
//                    ...
                    break;
                }
//            ...
        }
     }
    
    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        
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
    
    private void showErrorDialog(int errorCode){
        // Get the error code
//        int errorCode = 1; // connectionResult.getErrorCode(); // WTF ???????????????????
        
        // Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                errorCode,
                this,
                CONNECTION_FAILURE_RESOLUTION_REQUEST);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {
            // Create a new DialogFragment for the error dialog
            ErrorDialogFragment errorFragment =
                    new ErrorDialogFragment();
            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);
            // Show the error dialog in the DialogFragment
            errorFragment.show(getSupportFragmentManager(),
                    "Location Updates");
        }
    }
    
    // Implementing Interfaces
    
    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        TextView textView= (TextView) findViewById(R.id.textView1);
        Location location = mLocationClient.getLastLocation();
        textView.setText(location.toString());
    }
    
    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }
    
    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            showErrorDialog(connectionResult.getErrorCode());
        }
    }
    
    public void startAlarm(View view) {
        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        int interval = 10000;

        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }
    
    public void cancelAlarm(View view) {
        if (manager != null) {
            manager.cancel(pendingIntent);
            Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
        }
    }
    
    public void startLocationService(View view){
    	startService(new Intent(this, LocationService.class));
    }
    
    public void stopLocationService(View view){
    	stopService(new Intent(this, LocationService.class));
    }
    
    public void launchNotification(View view){    	
    	
    	// Instantiate a Builder object.
    	NotificationCompat.Builder builder =
    	        new NotificationCompat.Builder(this)
    	        .setSmallIcon(R.drawable.taxinowicon)
    	        .setContentTitle("My notification")
    	        .setContentText("Hello World!")
    	        .setAutoCancel(true);
    	// Creates an Intent for the Activity
//    	Intent notifyIntent =  new Intent(new ComponentName(this, ShutdownActivity.class));
    	Intent notifyIntent = new Intent(this, ShutdownActivity.class);
    	
    	// Sets the Activity to start in a new, empty task
    	notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    	// Creates the PendingIntent
    	PendingIntent pendingIntent =
    	        PendingIntent.getActivity(
    	        this,
    	        0,
    	        notifyIntent,
    	        PendingIntent.FLAG_UPDATE_CURRENT
    	);

    	// Puts the PendingIntent into the notification builder
    	builder.setContentIntent(pendingIntent);
    	// Notifications are issued by sending them to the
    	// NotificationManager system service.
    	NotificationManager mNotificationManager =
    	    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    	// Builds an anonymous Notification object from the builder, and
    	// passes it to the NotificationManager
    	mNotificationManager.notify(123456, builder.build());
    }
    
}
