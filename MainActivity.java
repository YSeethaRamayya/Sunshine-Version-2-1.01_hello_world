package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.IDN;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity {


    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment()).commit();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
//            startActivity(new Intent(this,SettingsActivity.class));
            Intent setting=new Intent(this,SettingsActivity.class);
            startActivity(setting);
            return true;
        }
        if (id == R.id.action_map) {
                        openPreferredLocationInMap();
                        return true;
                   }

        return super.onOptionsItemSelected(item);
    }
    private void openPreferredLocationInMap(){
        SharedPreferences sharedPrefs =
                                PreferenceManager.getDefaultSharedPreferences(this);
                String location = sharedPrefs.getString(
                                getString(R.string.pref_location_key),
                                getString(R.string.pref_location_default));
        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                                .appendQueryParameter("q", location)
                                .build();

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(geoLocation);

                   if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Log.d(LOG_TAG, "Couldn't call " + location + ", no receiving apps installed!");
                    }

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.android.sunshine.app/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.android.sunshine.app/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public static class ForecastFragment extends Fragment {
        private ArrayAdapter<String> mForecastAdapter;

        public ForecastFragment() {
        }
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
            inflater.inflate(R.menu.forecastfragment,menu);
        }
        public boolean onOptionsItemSelected(MenuItem item){
            int id = item.getItemId();
            if(id==R.id.action_refresh){
                updateWeather();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }




        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            String[] Example = {
                    "Mon 7/27-Sunny-32/18",
                    "Tue 7/28-Cloudy-27/15",
                    "Wed 7/29-Rainy-16/10",
                    "Thu 7/30-Sunny-33/20",
                    "Fri 7/31-Sunny-36/22",
                    "Sat 8/1-Rainy-20/15",
                    "Sun 8/2-Rainy-19/12"
            };
            List<String> weekForecast = new ArrayList<String>(Arrays.asList(Example));
            mForecastAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, weekForecast);
            ListView ls = (ListView) rootView.findViewById(R.id.list_item_forecast);
            ls.setAdapter(mForecastAdapter);
            ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String forecast= mForecastAdapter.getItem(i);
                    //Toast.makeText(getActivity(), "forecast", Toast.LENGTH_SHORT).show();
                    Intent DA = new Intent(getActivity(),DetailActivity.class).putExtra(Intent.EXTRA_TEXT,forecast);
                    startActivity(DA);
                }

                                   });// These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
            return rootView;
        }

        private void updateWeather() {
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String location = prefs.getString(getString(R.string.pref_location_key),
                    getString(R.string.pref_location_default));
            weatherTask.execute(location);
        }
        public void onStart(){
            super.onStart();
            updateWeather();
        }
           public  class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

                private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
               private String getReadableDateString(long time){
                   // Because the API returns a unix timestamp (measured in seconds),
                   // it must be converted to milliseconds in order to be converted to valid date.
                   SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
                   return shortenedDateFormat.format(time);
               }

               /**
                * Prepare the weather high/lows for presentation.
                */
                                  // For presentation, assume the user doesn't care about tenths of a degree.
                   private String formatHighLows(double high, double low, String unitType) {

                                          if (unitType.equals(getString(R.string.pref_units_imperial))) {
                                           high = (high * 1.8) + 32;
                                           low = (low * 1.8) + 32;
                                       } else if (!unitType.equals(getString(R.string.pref_units_metric))) {
                                           Log.d(LOG_TAG, "Unit type not found: " + unitType);
                                       }


                   long roundedHigh = Math.round(high);
                   long roundedLow = Math.round(low);

                   String highLowStr = roundedHigh + "/" + roundedLow;
                   return highLowStr;
               }

               /**
                * Take the String representing the complete forecast in JSON Format and
                * pull out the data we need to construct the Strings needed for the wireframes.
                *
                * Fortunately parsing is easy:  constructor takes the JSON string and converts it
                * into an Object hierarchy for us.
                */
               private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                       throws JSONException {

                   // These are the names of the JSON objects that need to be extracted.
                   final String OWM_LIST = "list";
                   final String OWM_WEATHER = "weather";
                   final String OWM_TEMPERATURE = "temp";
                   final String OWM_MAX = "max";
                   final String OWM_MIN = "min";
                   final String OWM_DESCRIPTION = "main";

                   JSONObject forecastJson = new JSONObject(forecastJsonStr);
                   JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

                   // OWM returns daily forecasts based upon the local time of the city that is being
                   // asked for, which means that we need to know the GMT offset to translate this data
                   // properly.

                   // Since this data is also sent in-order and the first day is always the
                   // current day, we're going to take advantage of that to get a nice
                   // normalized UTC date for all of our weather.

                   Time dayTime = new Time();
                   dayTime.setToNow();

                   // we start at the day returned by local time. Otherwise this is a mess.
                   int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

                   // now we work exclusively in UTC
                   dayTime = new Time();

                   String[] resultStrs = new String[numDays];
                   SharedPreferences sharedPrefs =
                                               PreferenceManager.getDefaultSharedPreferences(getActivity());
                              String unitType = sharedPrefs.getString(
                                               getString(R.string.pref_units_key),
                                               getString(R.string.pref_units_metric));
                   for(int i = 0; i < weatherArray.length(); i++) {
                       // For now, using the format "Day, description, hi/low"
                       String day;
                       String description;
                       String highAndLow;

                       // Get the JSON object representing the day
                       JSONObject dayForecast = weatherArray.getJSONObject(i);

                       // The date/time is returned as a long.  We need to convert that
                       // into something human-readable, since most people won't read "1400356800" as
                       // "this saturday".
                       long dateTime;
                       // Cheating to convert this to UTC time, which is what we want anyhow
                       dateTime = dayTime.setJulianDay(julianStartDay+i);
                       day = getReadableDateString(dateTime);

                       // description is in a child array called "weather", which is 1 element long.
                       JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                       description = weatherObject.getString(OWM_DESCRIPTION);

                       // Temperatures are in a child object called "temp".  Try not to name variables
                       // "temp" when working with temperature.  It confuses everybody.
                       JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                       double high = temperatureObject.getDouble(OWM_MAX);
                       double low = temperatureObject.getDouble(OWM_MIN);

                       highAndLow = formatHighLows(high, low,unitType);
                       resultStrs[i] = day + " - " + description + " - " + highAndLow;
                   }

                   return resultStrs;

               }
                @Override
                protected String[] doInBackground(String... params) {
                    if(params.length==0){
                        return null;
                    }
                    HttpURLConnection urlConnection = null;
                    BufferedReader reader = null;

                    // Will contain the raw JSON response as a string.
                    String forecastJsonStr = null;
                    String format="Json";
                    String Units="metric";
                    int numDays=7;
                    try

                    {
                        // Construct the URL for the OpenWeatherMap query
                        // Possible parameters are available at OWM's forecast API page, at
                        // http://openweathermap.org/API#forecast
                       // URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=62704 &mode=json&units=metric&cnt=7");
                        final String FORECAST_BASE_URL="http://api.openweathermap.org/data/2.5/forecast/daily?";
                        final String QUERY_PARAM="q";
                        final String FORMAT_PARAM="mode";
                        final String UNITS_PARAM="units";
                        final String DAYS_PARAM="cnt";

                        Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                                                        .appendQueryParameter(QUERY_PARAM, params[0])
                                                    .appendQueryParameter(FORMAT_PARAM, format)
                                                    .appendQueryParameter(UNITS_PARAM, Units)
                                                    .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                                                    .build();
                        URL url = new URL(builtUri.toString());
                        //Log.v(LOG_TAG, "Built URI " + builtUri.toString());
                        // Create the request to OpenWeatherMap, and open the connection
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();

                        // Read the input stream into a String
                        InputStream inputStream = urlConnection.getInputStream();
                        StringBuffer buffer = new StringBuffer();
                        if (inputStream == null) {
                            // Nothing to do.
                            return null;
                        }
                        reader = new BufferedReader(new InputStreamReader(inputStream));

                        String line;
                        while ((line = reader.readLine()) != null) {
                            // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                            // But it does make debugging a *lot* easier if you print out the completed
                            // buffer for debugging.
                            buffer.append(line + "\n");
                        }

                        if (buffer.length() == 0) {
                            // Stream was empty.  No point in parsing.
                            return null;
                        }
                        forecastJsonStr = buffer.toString();
                        //Log.v(LOG_TAG,"Forecast Jon String: "+ forecastJsonStr);
                    } catch (IOException e) {
                        //Log.e("ForecastFragment", "Error ", e);
                        // If the code didn't successfully get the weather data, there's no point in attempting
                        // to parse it.
                        return null;
                    } finally

                    {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (final IOException e) {
                                //Log.e("Forecast", "Error closing stream", e);
                            }
                        }
                    }
                    try {
                              return getWeatherDataFromJson(forecastJsonStr, numDays);
                                    } catch (JSONException e) {
                                        //Log.e(LOG_TAG, e.getMessage(), e);
                                        e.printStackTrace();
                                    }
                    return null;
                }

               @Override
               protected void onPostExecute(String[] result) {
                   if(result!=null){
                       mForecastAdapter.clear();
                       for(String dayForecastStr : result){
                           mForecastAdapter.add(dayForecastStr);
                       }
                   }
               }
           }
        }
    }
