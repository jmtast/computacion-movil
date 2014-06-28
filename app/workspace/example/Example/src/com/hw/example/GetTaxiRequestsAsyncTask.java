package com.hw.example;

import org.json.JSONObject;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.hw.example.LocationService.CurrentLocationListener;

public class GetTaxiRequestsAsyncTask extends AsyncTask<String, Void, JSONObject> {

	private Location location;
	private String userId;
	private CurrentLocationListener currentLocationListener;
	private Api api = new Api();

	public GetTaxiRequestsAsyncTask(
			CurrentLocationListener currentLocationListener, String userId,
			Location location) {
		this.userId = userId;
		this.location = location;
		this.currentLocationListener = currentLocationListener;
	}

	@Override
	protected JSONObject doInBackground(String... arg0) {
		JSONObject requests = api.updatePosition(userId,location);
		return requests;
	}
	
	@Override
	protected void onPostExecute(JSONObject requests) {
		currentLocationListener.sendData(requests);
	}

}
