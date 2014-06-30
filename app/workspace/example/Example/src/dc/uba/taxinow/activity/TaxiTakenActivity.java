package dc.uba.taxinow.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import dc.uba.taxinow.Api;
import dc.uba.taxinow.R;
import dc.uba.taxinow.model.Travel;

public class TaxiTakenActivity extends Activity {

	private Api api = new Api();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_taxi_taken);
	}
	
	@Override
	protected void onStart() {
		
		SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_pref_key), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(getString(R.string.taxi_state), MainActivity.TAXI_TAKEN);
		editor.commit();
		
		super.onStart();
	}
	
	public void backToAvailable(View view){
		
		finishTravel();
		
		Intent intent = new Intent(this, TaxiAvailableActivity.class);
		startActivity(intent);
	}
	
	private void finishTravel(){
		Intent intent = getIntent();
		String taxiId = intent.getStringExtra(TaxiAvailableActivity.TAXI_ID);
		String passengerId = intent.getStringExtra(TaxiAvailableActivity.PASSENGER_ID);
		Travel travel = new Travel(passengerId, taxiId);

		(new AsyncTask<Travel, Void, Void>() {
			@Override
			protected Void doInBackground(Travel... params) {
				api.finishTravel(params[0]);
				return null;
			}
		}).execute(travel);
	}
}
