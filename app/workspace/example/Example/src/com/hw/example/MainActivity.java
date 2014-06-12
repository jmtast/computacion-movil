package com.hw.example;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class MainActivity extends ActionBarActivity {

	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	
	public final static String NONE = "com.example.myfirstapp.NONE";
	public final static String TAXI = "com.example.myfirstapp.TAXI";
	public final static String PASSENGER = "com.example.myfirstapp.PASSENGER";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.user_type_preference_key), Context.MODE_PRIVATE);
        
        //--------This part takes care that all previous configuration is deleted. For development purposes. Comment to keep data saved permanently as it should for real. 
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
        //----------------------------------------------------------------------------------------------------------------------------------------------------------------
        
        String userType = sharedPref.getString(getString(R.string.user_type), NONE);
        
        if(NONE.equals(userType)){
        	Intent intent = new Intent(this, UserTypeChoosingActivity.class);
        	startActivity(intent);
        }else if(TAXI.equals(userType)){
        	Intent intent = new Intent(this, DisplayMessageActivity.class);
        	intent.putExtra(EXTRA_MESSAGE, "taxi");
        	startActivity(intent);
        }else if(PASSENGER.equals(userType)){
        	Intent intent = new Intent(this, DisplayMessageActivity.class);
        	intent.putExtra(EXTRA_MESSAGE, "passenger");
        	startActivity(intent);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /** Called when the user clicks the Send button */
    public void sendMessage(View view) {
    	Intent intent = new Intent(this, DisplayMessageActivity.class);
    	EditText editText = (EditText) findViewById(R.id.edit_message);
    	String message = editText.getText().toString();
    	intent.putExtra(EXTRA_MESSAGE, message);
    	startActivity(intent);	
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
