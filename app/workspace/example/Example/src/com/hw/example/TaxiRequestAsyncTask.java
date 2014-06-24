package com.hw.example;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.location.Location;
import android.os.AsyncTask;

public class TaxiRequestAsyncTask extends AsyncTask<TravelRequest, Void, AvailableTaxis> {

	private SearchingTaxis context;

	public TaxiRequestAsyncTask(SearchingTaxis context) {
		super();
		this.context = context;
	}

	@Override
	protected AvailableTaxis doInBackground(TravelRequest... arg0) {
		TravelRequest travelRequest = arg0[0];
		
		Api api = new Api();
        Map<String, String> params = new HashMap<String, String>(2);
        params.put("origin", travelRequest.getOrigin().toString());
        params.put("destination", travelRequest.getDestination());
		JSONObject jsonResponse = api.doPost(context.getString(R.string.save_taxi_config_url), params);
		
		AvailableTaxis availableTaxis = null; // someMagic(jsonResponse);
		return availableTaxis;
	}
	
	@Override
	protected void onPostExecute(AvailableTaxis result) {
		super.onPostExecute(result);
		context.processAvailableTaxis(result);
	}
}
