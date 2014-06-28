package dc.uba.taxinow;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.os.AsyncTask;
import dc.uba.taxinow.LocationService.CurrentLocationListener;

public class TaxiRequests extends AsyncTask<String, Void, JSONObject> {

	private String userId;
	private TaxiAvailableActivity taxiAvailableActivity;
	private Api api = new Api();

	public TaxiRequests(
			TaxiAvailableActivity taxiAvailableActivity, String userId) {
		this.userId = userId;
		this.taxiAvailableActivity = taxiAvailableActivity;
	}

	@Override
	protected JSONObject doInBackground(String... arg0) {
		JSONObject requests = api.getRequests(userId);
		return requests;
	}
	
	@Override
	protected void onPostExecute(JSONObject requests) {
		taxiAvailableActivity.updateRequests(requests.toString());
//		try {
//			taxiAvailableActivity.updateRequests(requests.getJSONArray("requests"));
//		} catch (JSONException e) {
//			 TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
