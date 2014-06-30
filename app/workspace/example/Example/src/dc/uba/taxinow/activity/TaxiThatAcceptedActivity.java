package dc.uba.taxinow.activity;

import org.json.JSONObject;

import dc.uba.taxinow.Api;
import dc.uba.taxinow.R;
import dc.uba.taxinow.R.layout;
import dc.uba.taxinow.model.TaxiData;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class TaxiThatAcceptedActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_taxi_that_accepted);
		Intent intent = getIntent();
		String taxiFullData = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
		TaxiData taxiData = new TaxiData(taxiFullData);
		TextView textView = (TextView) findViewById(R.id.taxiThatAccepted);
		textView.setText(taxiData.toString());
	}
	
	public void cancelTrip(View view){
		(new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				Api api = new Api();
				JSONObject taxi = api.cancelTrip(userId());
				
				return null;
			}
		}).execute();
		
		Intent intent = new Intent(this, PassengerConfigActivity.class);
    	startActivity(intent);
	}
	
	public String userId(){
		SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_pref_key), Context.MODE_PRIVATE);
		String userId = sharedPref.getString(getString(R.string.user_id), "");
		return userId;
	}
}
