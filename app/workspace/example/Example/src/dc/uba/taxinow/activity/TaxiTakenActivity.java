package dc.uba.taxinow.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import dc.uba.taxinow.R;

public class TaxiTakenActivity extends Activity {

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
		Intent intent = new Intent(this, TaxiAvailableActivity.class);
		startActivity(intent);
	}
}
