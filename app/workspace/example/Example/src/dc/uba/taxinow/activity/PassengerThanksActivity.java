package dc.uba.taxinow.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import dc.uba.taxinow.R;

public class PassengerThanksActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_thanks);
        
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .commit();
        }
        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();
    }

}