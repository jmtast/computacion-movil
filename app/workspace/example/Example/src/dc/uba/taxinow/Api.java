package dc.uba.taxinow;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;

public class Api {
	
	private HttpClient httpclient = new DefaultHttpClient();
	
	public String register(String type) throws JSONException{
		JSONObject resp = doGet("http://follower-endpoint.herokuapp.com/application/register"+type,new HashMap());
		return resp.getString("id");
	}
	
	public JSONObject doGet(String uri, Map params) {
	    HttpGet httpGet = new HttpGet(uri);
	    return executeHttpRequest(httpGet, uri, params);
	}
	
	public JSONObject doPostJSON(String uri, JSONObject jsonObject) {
		HttpPost httpPost = new HttpPost(uri);
		
        try {
        	String jsonString = jsonObject.toString();
        	StringEntity stringEntity = new StringEntity(jsonString);
			httpPost.setEntity(stringEntity);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    Map params = new HashMap();
		return executeHttpRequest(httpPost, uri, params);
	}
	
	public JSONObject doPost(String uri, Map<String, String> params) {
	    HttpPost httpPost = new HttpPost(uri);
	    
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(params.size());
	    for (Entry<String, String> entry : params.entrySet()) {
			nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
        try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return executeHttpRequest(httpPost, uri, params);
	}
	
	protected JSONObject executeHttpRequest(HttpRequestBase httpRequest, String uri, Map params){
        HttpResponse httpResponse = null;
        JSONObject jsonResponse = null;
        try {
        	httpResponse = httpclient.execute(httpRequest);
			jsonResponse = parseResponse(httpResponse);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return jsonResponse;
	}
	
	protected JSONObject parseResponse(HttpResponse response) throws IllegalStateException, JSONException, IOException{
        JSONObject obj = null;
		obj = new JSONObject(getASCIIContentFromEntity(response.getEntity()));
        return obj;
	}
	
	protected String getASCIIContentFromEntity(HttpEntity entity)
            throws IllegalStateException, IOException {
        InputStream in = entity.getContent();
        StringBuffer out = new StringBuffer();
        int n = 1;
        while (n > 0) {
            byte[] b = new byte[4096];
            n = in.read(b);
            if (n > 0)
                out.append(new String(b, 0, n));
        }
        return out.toString();
    }
	
	public JSONObject updatePosition(String myId, Location location) {
		
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.accumulate("id", myId);
			jsonObject.accumulate("newPosition", location.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JSONObject requests = doPostJSON("http://follower-endpoint.herokuapp.com/application/updatePositionTaxi",jsonObject);
		
		return requests;
		
		// TODO Auto-generated method stub
		
	}

	public JSONObject getRequests(String userId) {
		JSONObject params = new JSONObject();
		try {
			params.accumulate("id", userId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return doPostJSON("http://follower-endpoint.herokuapp.com/application/requestsForTaxi", params);
	}

	public JSONObject requestTaxi(TravelRequest travelRequest) {
		JSONObject params = new JSONObject();
		try {
			params.accumulate("passengerId", travelRequest.getPassengerId());
			params.accumulate("passengerPosition", travelRequest.getOrigin());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return doPostJSON("http://follower-endpoint.herokuapp.com/application/requestTaxi", params);
	}

}
