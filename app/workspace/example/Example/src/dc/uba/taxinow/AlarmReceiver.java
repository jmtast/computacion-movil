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
	private static int count = 0;
	private static int max_count = 30;

    @Override
    public void onReceive(Context arg0, Intent intent) {
    	context = arg0;
        // For our recurring task
    	SharedPreferences sharedPref = arg0.getSharedPreferences(arg0.getString(R.string.shared_pref_key), Context.MODE_PRIVATE);
		userId = sharedPref.getString(arg0.getString(R.string.user_id), "");
		
		if (count < max_count){
			taxiThatAccepted(userId);
			count++;
		}else{
			cancelTrip();
			count = 0;
			sendData("TimeOut");
		}
    }
    
    public void sendData(TaxiData taxiData){
    	if (taxiData != null){
			Intent intent = new Intent();
			intent.setAction(ACCEPTED_TAXI_ACTION);
			intent.putExtra(ACCEPTED_TAXI_DATA, taxiData.toString());
			context.sendBroadcast(intent);
    	}
    }
    
    public void sendData(String error){
    	if (error != null){
			Intent intent = new Intent();
			intent.setAction(ACCEPTED_TAXI_ACTION);
			intent.putExtra(ACCEPTED_TAXI_DATA, error);
			context.sendBroadcast(intent);
    	}
    }
    
    public String userId(){
    	return userId;
    }
    
    private void cancelTrip(){
    	(new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				JSONObject taxi = api.cancelTrip(userId());
				
				return null;
			}
		}).execute();
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