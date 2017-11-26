package ch.jelec.enigma2epgview.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.widget.Toast;
import java.lang.Object;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;


public class SingleMenuItemActivity  extends Activity {

    // this is how should URL looks like to zap channel
    // http://192.168.1.200/api/zap?sRef=1:0:19:EF74:3F9:1:C00000:0:0:0:)

    Button button;

 	// XML node keys
	static final String KEY_EVENT_DESCRIPTIONEXTENDED = "e2eventdescriptionextended";
	static final String KEY_EVENT_SERVICEREFERENCE = "e2eventservicereference";
	static final String staticURL = "http://192.168.1.200/api/zap?sRef=";
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

    public void onClickBtn(View v)
    {
        // getting intent data
        Intent in = getIntent();
        // Get XML values from previous intent
        String serviceID = in.getStringExtra(KEY_EVENT_SERVICEREFERENCE);
        String urlToSend = staticURL+serviceID;

        // Create a new HttpClient and Post Header
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(urlToSend);
        try {
            //execute http post
            HttpResponse response = httpclient.execute(httppost);

        } catch (ClientProtocolException e) {
                Toast.makeText(this, "Protokolfehler", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
                Toast.makeText(this, "IOException Fehler", Toast.LENGTH_LONG).show();
        }

        Toast.makeText(this, "Umgeschaltet", Toast.LENGTH_LONG).show();
    }
}
