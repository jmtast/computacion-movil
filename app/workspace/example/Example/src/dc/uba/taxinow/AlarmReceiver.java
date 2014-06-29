package dc.uba.taxinow;

import org.json.JSONObject;

import dc.uba.taxinow.model.TaxiData;
import dc.uba.taxinow.utils.JsonHelper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
	
	private Api api = new Api();
	
	public static String ACCEPTED_TAXI_ACTION = "dc.uba.taxinow.ACCEPTED_TAXI_ACTION";
	public static String ACCEPTED_TAXI_DATA = "dc.uba.taxinow.ACCEPTED_TAXI_DATA";
	
	private Context context;

	public String userId;

    @Override
    public void onReceive(Context arg0, Intent intent) {
        // For our recurring task
    	SharedPreferences sharedPref = arg0.getSharedPreferences(String.valueOf(R.string.user_id_preference_key), Context.MODE_PRIVATE);
		userId = sharedPref.getString(String.valueOf(R.string.user_id), "");

		taxiThatAccepted(userId);
    }
    
    public void sendData(TaxiData taxiData){
    	if (taxiData != null){
    		Intent intent = new Intent();
    		intent.setAction(ACCEPTED_TAXI_ACTION);
			intent.putExtra(ACCEPTED_TAXI_DATA, taxiData.toString());
    		context.sendBroadcast(intent);
    	}
    }
    
    public String userId(){
    	return userId;
    }

	private void taxiThatAccepted(String userId) {
		(new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				JSONObject taxi = api.getTaxiThatAccepted(userId());
				
				TaxiData taxiData = JsonHelper.getTaxiData(taxi);
				if (taxiData != null){
					sendData(taxiData);
				}
				return null;
			}
		}).execute();
	}
	
	

}