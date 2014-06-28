package dc.uba.taxinow.asynctasks;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.json.JSONException;

import dc.uba.taxinow.Api;
import dc.uba.taxinow.activity.UserTypeChoosingActivity;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

public class Register extends AsyncTask<String, Void, String> {
	
	private UserTypeChoosingActivity activity;
	private Class nextActivity;
	private String nextType;

	public Register(UserTypeChoosingActivity context, Class nextActivity, String nextType){
	    this.activity = context;
	    this.nextActivity = nextActivity;
	    this.nextType = nextType;
	}

	@Override
	protected String doInBackground(String... type) {
		Api api = new Api();
		try {
			return api.register(type[0]);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {
		activity.finishRegistration(nextActivity,nextType,result);
	}
	

}
