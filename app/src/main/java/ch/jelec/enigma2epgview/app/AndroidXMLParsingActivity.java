package ch.jelec.enigma2epgview.app;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;


public class AndroidXMLParsingActivity extends ListActivity {

	// All static variables
	//"http://api.androidhive.info/pizza/?format=xml"
	static final String URL = "http://192.168.1.200/web/";
	static final String getAllServices = "getallservices";
	static final String getEpgServiceNow = "epgservicenow?sRef=";
	// XML node keys
	static final String KEY_SERVICE_SERVICES = "e2service"; // parent node
	static final String KEY_SERVICE_REFERENCE = "e2servicereference";
	static final String	KEY_EVENT = "e2event";
	static final String KEY_EVENT_SERVICENAME = "e2eventservicename";
	static final String KEY_EVENT_TITLE = "e2eventtitle";
	static final String KEY_EVENT_DESCRIPTION = "e2eventdescription";
	static final String KEY_EVENT_STARTTIME = "e2eventstart";
	static final String KEY_EVENT_DURATION = "e2eventduration";
	static final String KEY_EVENT_DESCRIPTIONEXTENDED = "e2eventdescriptionextended";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		String LOG_TAG = AndroidXMLParsingActivity.class.getSimpleName();

		Log.v(LOG_TAG, "verbose     - Meldung");
		Log.d(LOG_TAG, "debug       - Meldung");
		Log.i(LOG_TAG, "information - Meldung");
		Log.w(LOG_TAG, "warning     - Meldung");
		Log.e(LOG_TAG, "error       - Meldung");


		ArrayList<HashMap<String, String>> menuServices = new ArrayList<HashMap<String, String>>();

		XMLParser parserServices = new XMLParser();
		String xmlServices = parserServices.getXmlFromUrl(URL+getAllServices); // getting XML
		Document docServices = parserServices.getDomElement(xmlServices); // getting DOM element

		NodeList nlServices = docServices.getElementsByTagName(KEY_SERVICE_SERVICES);
		// looping through all item nodes <item>
		for (int i = 0; i < nlServices.getLength(); i++) {
			// creating new HashMap
			HashMap<String, String> map = new HashMap<String, String>();
			Element e = (Element) nlServices.item(i);
			// adding each child node to HashMap key => value
			map.put(KEY_SERVICE_REFERENCE, parserServices.getValue(e, KEY_SERVICE_REFERENCE));
			// adding HashList to ArrayList
			menuServices.add(map);
		}

		final ArrayList<HashMap<String, String>> menuEvents = new ArrayList<HashMap<String, String>>();

		for (int i=0; i<menuServices.size(); i++){

			XMLParser parserEvents = new XMLParser();
			String xmlEvents = parserEvents.getXmlFromUrl(URL+getEpgServiceNow+menuServices.get(i).get("e2servicereference")); // getting XML
			Document docEvents = parserEvents.getDomElement(xmlEvents); // getting DOM element

			NodeList nlEvents = docEvents.getElementsByTagName(KEY_EVENT);
			// looping through all item nodes <item>
			for (int y = 0; y < nlEvents.getLength(); y++) {
				// creating new HashMap
				HashMap<String, String> map = new HashMap<String, String>();
				Element e = (Element) nlEvents.item(y);
				// adding each child node to HashMap key => value
				Long startTime = Long.parseLong(parserEvents.getValue(e, KEY_EVENT_STARTTIME));
				Calendar cal = Calendar.getInstance();
				cal.setTime(new java.util.Date(startTime * 1000));
				Date date = cal.getTime();
				String startTimeToString = date.toString();
				String time = startTimeToString.split("\\s")[3].split("\\.")[0];

				Integer durationToInt = Integer.parseInt(parserEvents.getValue(e, KEY_EVENT_DURATION));
				Integer timeLeft = durationToInt/60;
				String timeLeftToString = timeLeft.toString();
				map.put(KEY_EVENT_SERVICENAME, parserEvents.getValue(e, KEY_EVENT_SERVICENAME));
				map.put(KEY_EVENT_TITLE, parserEvents.getValue(e, KEY_EVENT_TITLE));
				map.put(KEY_EVENT_DESCRIPTION, parserEvents.getValue(e, KEY_EVENT_DESCRIPTION));
				map.put(KEY_EVENT_STARTTIME, time);
				map.put(KEY_EVENT_DURATION, timeLeftToString + " Min.");
				map.put(KEY_EVENT_DESCRIPTIONEXTENDED, parserEvents.getValue(e, KEY_EVENT_DESCRIPTIONEXTENDED));
				// adding HashList to ArrayList
				menuEvents.add(map);
			}

		}


		// Adding menuServices to ListView
		ListAdapter adapter = new SimpleAdapter(this, menuEvents,
				R.layout.list_item,
				new String[] {KEY_EVENT_SERVICENAME, KEY_EVENT_TITLE, KEY_EVENT_STARTTIME, KEY_EVENT_DURATION}, new int[] {
						R.id.servicename, R.id.eventtitle, R.id.starttime, R.id.eventlefttime});

		setListAdapter(adapter);

		// selecting single ListView item
		ListView lv = getListView();

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// getting values from selected ListItem
				String eventdescriptionextended = menuEvents.get(position).get("e2eventdescriptionextended");

				// Starting new intent
				Intent in = new Intent(getApplicationContext(), SingleMenuItemActivity.class);
				in.putExtra(KEY_EVENT_DESCRIPTIONEXTENDED, eventdescriptionextended);
				startActivity(in);

			}
		});
	}

	public Date getDate(Timestamp timestamp){
		return new Date(timestamp.getTime());
	}


}