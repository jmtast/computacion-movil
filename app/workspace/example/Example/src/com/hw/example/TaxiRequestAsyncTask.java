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
		JSONObject jsonResponse = api.requestTaxi(travelRequest);
		
		AvailableTaxis availableTaxis = null; // someMagic(jsonResponse);
		return availableTaxis;
	}
	
}
