package ch.jelec.enigma2epgview.app;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import java.util.Calendar;
import android.view.Menu;
import android.view.MenuItem;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;



public class AndroidXMLParsingActivity extends FragmentActivity  {


	// Auslesen der ausgewählten Aktienliste aus den SharedPreferences

	// All static variables
	static final String getAllServices = "getallservices";
	static final String getEpgServiceNow = "epgservicenow?sRef=";
	// XML node keys
	static final String KEY_SERVICE_SERVICES = "e2service"; // parent node
	static final String KEY_SERVICE_REFERENCE = "e2servicereference";
	static final String	KEY_EVENT = "e2event";
	static final String KEY_EVENT_SERVICENAME = "e2eventservicename";
	static final String KEY_EVENT_SERVICEREFERENCE = "e2eventservicereference";
	static final String KEY_EVENT_TITLE = "e2eventtitle";
	static final String KEY_EVENT_DESCRIPTION = "e2eventdescription";
	static final String KEY_EVENT_STARTTIME = "e2eventstart";
	static final String KEY_EVENT_DURATION = "e2eventduration";
	static final String KEY_EVENT_DESCRIPTIONEXTENDED = "e2eventdescriptionextended";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// Menü bekannt geben, dadurch kann unser Fragment Menü-Events verarbeiten

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		String LOG_TAG = AndroidXMLParsingActivity.class.getSimpleName();

		Log.v(LOG_TAG, "verbose     - Meldung");
		Log.d(LOG_TAG, "debug       - Meldung");
		Log.i(LOG_TAG, "information - Meldung");
		Log.w(LOG_TAG, "warning     - Meldung");
		Log.e(LOG_TAG, "error       - Meldung");

		// Auslesen der ausgewählten Aktienliste aus den SharedPreferences
		SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		String prefAktienlisteKey = getString(R.string.preference_ipaddress_key);
		String prefAktienlisteDefault = getString(R.string.preference_ipaddress_default);
		String ipaddress = sPrefs.getString(prefAktienlisteKey,prefAktienlisteDefault);

		String URL = "http://"+ipaddress+"/web/";

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
				// get event start time as Long from String
				Long startTime = Long.parseLong(parserEvents.getValue(e, KEY_EVENT_STARTTIME));
				// create java.util.calendar instance and get date/time from variable startTime (Timestamp)
				Calendar cal = Calendar.getInstance();
				cal.setTime(new java.util.Date(startTime * 1000));
				Date date = cal.getTime();
				// convert Date date to String
				String startTimeToString = date.toString();
				// catch only start time (hh:mm:ss) from date string
				String time = startTimeToString.split("\\s")[3].split("\\.")[0];
				// get event duration time as Integer from String
				Long durationToLong = Long.parseLong(parserEvents.getValue(e, KEY_EVENT_DURATION));
				// calculcate end time (start time + duration in sconds)
				Long endTime = startTime + durationToLong;
				cal.setTime(new java.util.Date(endTime * 1000));
				Date endDate = cal.getTime();
				String endTimeToString = endDate.toString();
				String endTimeString = endTimeToString.split("\\s")[3].split("\\.")[0];

				// adding each child node to HashMap key => value
				map.put(KEY_EVENT_SERVICEREFERENCE, parserEvents.getValue(e, KEY_EVENT_SERVICEREFERENCE));
				map.put(KEY_EVENT_SERVICENAME, parserEvents.getValue(e, KEY_EVENT_SERVICENAME));
				map.put(KEY_EVENT_TITLE, parserEvents.getValue(e, KEY_EVENT_TITLE));
				map.put(KEY_EVENT_DESCRIPTION, parserEvents.getValue(e, KEY_EVENT_DESCRIPTION));
				map.put(KEY_EVENT_STARTTIME, time);
				map.put(KEY_EVENT_DURATION, endTimeString);
				map.put(KEY_EVENT_DESCRIPTIONEXTENDED, parserEvents.getValue(e, KEY_EVENT_DESCRIPTIONEXTENDED));
				// adding HashList to ArrayList
				menuEvents.add(map);
			}
		}

		// Adding menuServices to ListView
		ListView lv = findViewById(android.R.id.list);
		ListAdapter adapter = new SimpleAdapter(this, menuEvents,
				R.layout.list_item,
				new String[] {KEY_EVENT_SERVICENAME, KEY_EVENT_TITLE, KEY_EVENT_STARTTIME, KEY_EVENT_DURATION}, new int[] {
				R.id.servicename, R.id.eventtitle, R.id.starttime, R.id.eventlefttime});

		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// getting values from selected ListItem
				String eventdescriptionextended = menuEvents.get(position).get("e2eventdescriptionextended");
				String e2eventservicereference = menuEvents.get(position).get("e2eventservicereference");

				// Starting new intent
				Intent in = new Intent(getApplicationContext(), SingleMenuItemActivity.class);
				in.putExtra(KEY_EVENT_DESCRIPTIONEXTENDED, eventdescriptionextended);
				in.putExtra(KEY_EVENT_SERVICEREFERENCE, e2eventservicereference);
				startActivity(in);

			}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_androidxmlparsingactivity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Wir prüfen, ob Menü-Element mit der ID "action_daten_aktualisieren"
		// ausgewählt wurde und geben eine Meldung aus
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}