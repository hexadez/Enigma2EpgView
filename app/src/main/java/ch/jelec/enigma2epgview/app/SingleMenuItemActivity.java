package ch.jelec.enigma2epgview.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;

public class SingleMenuItemActivity  extends Activity {

 	// XML node keys
	static final String KEY_EVENT_DESCRIPTIONEXTENDED = "e2eventdescriptionextended";
	@Override
    public void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_list_item);

        String LOG_TAG = AndroidXMLParsingActivity.class.getSimpleName();

        Log.v(LOG_TAG, "verbose     - Meldung");
        Log.d(LOG_TAG, "debug       - Meldung");

        Log.i(LOG_TAG, "information - Meldung");
        Log.w(LOG_TAG, "warning     - Meldung");
        Log.e(LOG_TAG, "error       - Meldung");

        // getting intent data
        Intent in = getIntent();
        
        // Get XML values from previous intent
        String description = in.getStringExtra(KEY_EVENT_DESCRIPTIONEXTENDED);
        
        // Displaying all values on the screen
        TextView lblDesc = (TextView) findViewById(R.id.eventdescriptionextended);
        lblDesc.setText(description);
    }
}
