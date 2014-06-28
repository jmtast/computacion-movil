package dc.uba.taxinow.asynctasks;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import dc.uba.taxinow.Api;
import dc.uba.taxinow.activity.SearchingTaxisActivity;
import dc.uba.taxinow.model.AvailableTaxis;
import dc.uba.taxinow.model.TravelRequest;
import android.location.Location;
import android.os.AsyncTask;

public class TaxiRequestAsyncTask extends AsyncTask<TravelRequest, Void, AvailableTaxis> {

	private SearchingTaxisActivity context;

	public TaxiRequestAsyncTask(SearchingTaxisActivity context) {
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
