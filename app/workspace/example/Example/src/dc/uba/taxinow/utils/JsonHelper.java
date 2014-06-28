package dc.uba.taxinow.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonHelper {

	public static List<Map<String, String>> parseTaxiRequestsList(JSONObject src){
		List<Map<String, String>> travelRequests = null;
		try {
			JSONArray requests = src.getJSONArray("requests");
			
			travelRequests = new ArrayList<Map<String, String>>();
			for (int i = 0; i < requests.length(); i++) {
				Map<String, String> map = new HashMap<String, String>();
					map.put("requestId",
							requests.getJSONObject(i).getString("requestId"));
					
					map.put("positionPassenger", requests.getJSONObject(i)
							.getString("positionPassenger"));
					
					map.put("passengerId",
							requests.getJSONObject(i).getString("passengerId"));
					
					travelRequests.add(map);
			}
			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		return travelRequests;
	}
	
	public static List<Map<String, String>> parseTaxiRequestsList(String src){
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(src);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return parseTaxiRequestsList(jsonObject);
	}
}
