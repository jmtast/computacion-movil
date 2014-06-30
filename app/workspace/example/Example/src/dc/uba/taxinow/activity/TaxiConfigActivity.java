package dc.uba.taxinow.activity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import dc.uba.taxinow.Api;
import dc.uba.taxinow.R;
import dc.uba.taxinow.R.id;
import dc.uba.taxinow.R.layout;
import dc.uba.taxinow.R.menu;
import dc.uba.taxinow.R.string;
import dc.uba.taxinow.model.TaxiData;

public class TaxiConfigActivity extends ActionBarActivity {

	private static final int REQUEST_TAKE_PHOTO = 1;
	
	public static final int REQUEST_NO_GOING_BACK = 111;
	public static final String NO_GOING_BACK_EXTRA = "NO_GOING_BACK";
	
	private String none;
	private SharedPreferences sharedPref;
	private String mCurrentPhotoPath = null;
	
	private Api api = new Api();
	
	public static TaxiConfigActivity instance = null;
	
	@Override
	protected void onStart() {
		TaxiConfigActivity.instance= this; 
		super.onStart();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_taxi_config);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		ActionBar actionbar = getSupportActionBar();
        actionbar.hide();
        
        none = getString(R.string.none);
        sharedPref = getSharedPreferences(getString(R.string.shared_pref_key), Context.MODE_PRIVATE);
	}
	
	@Override
	public void onPostCreate(Bundle savedInstanceState){
		super.onPostCreate(savedInstanceState);
		loadTaxiConfig();
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus){
		super.onWindowFocusChanged(hasFocus);
		if(mCurrentPhotoPath == null){
        	mCurrentPhotoPath = sharedPref.getString(getString(R.string.taxi_config_photo_file), none);
            
            if(!none.equals(mCurrentPhotoPath)){
            	setPic(mCurrentPhotoPath);
            }
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.taxi_config, menu);
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
	    	ImageView mImageView = (ImageView) findViewById(R.id.imageView_taxi_driver);
	    	
	    	setPic(mCurrentPhotoPath);
	    	savePhotoFileName(mCurrentPhotoPath);
	    }
	}
	
	public void saveTaxiConfig(View view){
		SharedPreferences.Editor editor = sharedPref.edit();
		
		EditText editText = (EditText) findViewById(R.id.edit_marca);
        String marca = editText.getText().toString();
		editor.putString(getString(R.string.taxi_config_marca), marca);
		
		editText = (EditText) findViewById(R.id.edit_modelo);
        String modelo = editText.getText().toString();
		editor.putString(getString(R.string.taxi_config_modelo), modelo);
		
		editText = (EditText) findViewById(R.id.edit_patente);
        String patente = editText.getText().toString();
		editor.putString(getString(R.string.taxi_config_patente), patente);
		
		editor.commit();
		
		String userId = sharedPref.getString(getString(R.string.user_id), "");
		
		sendToServer(new TaxiData(userId, patente, marca, modelo));
		
		Toast.makeText(this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
		
		launchNextActivity();
	}
	
	private void sendToServer(TaxiData taxiData){
		(new AsyncTask<TaxiData, Void, Void>() {
			@Override
			protected Void doInBackground(TaxiData... params) {
				api.saveTaxiConfig(params[0]);
				return null;
			}
		}).execute(taxiData);
	}
	
	private void launchNextActivity(){
		Intent intent = new Intent(this, TaxiAvailableActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	private void loadTaxiConfig(){
		none = getString(R.string.none);
        
		EditText editText;
		
		String message = sharedPref.getString(getString(R.string.taxi_config_marca), none);
		if(!none.equals(message)){
			editText = (EditText) findViewById(R.id.edit_marca);
			editText.setText(message);
		}
		
		message = sharedPref.getString(getString(R.string.taxi_config_modelo), none);
		if(!none.equals(message)){
			editText = (EditText) findViewById(R.id.edit_modelo);
			editText.setText(message);
		}
		
		message = sharedPref.getString(getString(R.string.taxi_config_patente), none);
		if(!none.equals(message)){
			editText = (EditText) findViewById(R.id.edit_patente);
			editText.setText(message);
		}
	}
	
	public void takePic(View view){
		dispatchTakePictureIntent();
	}
	
	private File createImageFile() throws IOException {
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "taxi_driver_" + timeStamp;
	    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	    File image = new File(storageDir, imageFileName+".jpg");

	    mCurrentPhotoPath = image.getAbsolutePath();
	    return image;
	}
	
	private void dispatchTakePictureIntent() {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    // Ensure that there's a camera activity to handle the intent
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        // Create the File where the photo should go
	        File photoFile = null;
	        try {
	            photoFile = createImageFile();
	        } catch (IOException ex) {
	            // Error occurred while creating the File
	        }
	        // Continue only if the File was successfully created
	        if (photoFile != null) {
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
	            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
	        }
	    }
	}
	
	private void savePhotoFileName(String filename){
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(getString(R.string.taxi_config_photo_file), filename);
		editor.commit();
	}
	
	private void setPic(String mCurrentPhotoPath) {
		ImageView mImageView = (ImageView) findViewById(R.id.imageView_taxi_driver);
	    
		// Get the dimensions of the View
	    int targetW = mImageView.getWidth();
	    int targetH = mImageView.getHeight();

	    // Get the dimensions of the bitmap
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;

	    // Determine how much to scale down the image
	    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

	    // Decode the image file into a Bitmap sized to fill the View
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;

	    Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
	    mImageView.setImageBitmap(bitmap);
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
			View rootView = inflater.inflate(R.layout.fragment_taxi_config,
					container, false);
			return rootView;
		}
	}
	
	public void chooseUserType(View view) {
		SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_pref_key), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.remove(getString(R.string.user_type));
		editor.commit();
		
		Toast.makeText(this, "Preferencias borradas", Toast.LENGTH_SHORT).show();
	}

}
