package com.hw.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

public class Api {
	
	public String register(String type) throws JSONException{
		JSONObject resp = doPost("http://follower-endpoint.herokuapp.com/application/register"+type,new HashMap());
		return resp.getString("id");
	}
	
	protected JSONObject doPost(String URI, Map params){
		HttpClient httpclient = new DefaultHttpClient();
	    HttpGet httppost = new HttpGet("http://follower-endpoint.herokuapp.com/application/puta");
	
	    try {
	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        JSONObject obj =  new JSONObject(getASCIIContentFromEntity(response.getEntity()));
	        return obj;
//	        return getASCIIContentFromEntity(response.getEntity());
	        
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    } catch (Exception e){
//	    	return e.getMessage();
	    }
	    
	    return null;
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

}
