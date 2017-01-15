package com.gerardoaugusto.myweatherviewer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public List<Weather>weatherList=new ArrayList<>();
    private WeatherArrayAdapter weatherArrayAdapter;
    private ListView weatherListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);

        weatherListView= (ListView) findViewById(R.id.weatherListView);
        weatherArrayAdapter=new WeatherArrayAdapter(this,R.layout.list_item,weatherList);
        weatherListView.setAdapter(weatherArrayAdapter);
        FloatingActionButton fab= (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText locationEditText= (EditText) findViewById(R.id.locationEditText);
                URL url=createURL(locationEditText.getText().toString());
                if (url!=null){
                    dismissKeyBoard(locationEditText);
                    GetWeatherTask getLocalWeatherTask = new GetWeatherTask();
                    getLocalWeatherTask.execute(url);
                }else{
                    Snackbar.make(findViewById(R.id.coordinatorLayout),R.string.invalid_url,Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }

    private void dismissKeyBoard(EditText locationEditText) {
        InputMethodManager imm= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(locationEditText.getWindowToken(),0);
    }

    private URL createURL(String city) {
        String apiKey=getString(R.string.api_key);
        String baseUrl=getString(R.string.web_service_url);
        try{
            String UrlString=baseUrl+ URLEncoder.encode(city,"UTF-8")+"&units=imperial&cnt=16&APPID="+apiKey;
            return new URL(UrlString);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    private class GetWeatherTask extends AsyncTask<URL,Void,JSONObject>{

        @Override
        protected JSONObject doInBackground(URL... urls) {
            HttpURLConnection conection=null;
            try {
                conection= (HttpURLConnection) urls[0].openConnection();
                int responseCode=conection.getResponseCode();
                if (responseCode==HttpURLConnection.HTTP_OK){
                    StringBuilder builder=new StringBuilder();
                    try(BufferedReader reader=new BufferedReader(new InputStreamReader(conection.getInputStream()))){
                        String line=new String();
                        while((line=reader.readLine())!=null){
                            builder.append(line);
                        }
                        return new JSONObject(builder.toString());
                    }
                    catch (Exception e){
                        Snackbar.make(findViewById(R.id.coordinatorLayout),R.string.read_error,Snackbar.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }else{
                    Snackbar.make(findViewById(R.id.coordinatorLayout),R.string.connect_error,Snackbar.LENGTH_LONG).show();
                }
            }
            catch (Exception e){
                Snackbar.make(findViewById(R.id.coordinatorLayout),R.string.connect_error,Snackbar.LENGTH_LONG).show();
                e.printStackTrace();
            }
            finally {
                conection.disconnect(); // close the HttpURLConnection
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            convertJSONToArrayList(jsonObject);
            weatherArrayAdapter.notifyDataSetChanged();
            weatherListView.smoothScrollToPosition(0);
        }

        private void convertJSONToArrayList(JSONObject jsonObject) {
            weatherList.clear();
            try{
                JSONArray list=jsonObject.getJSONArray("list");
                for (int i=0;i<list.length();i++){
                    JSONObject day=list.getJSONObject(i);
                    JSONObject temp=day.getJSONObject("temp");
                    JSONObject weather=day.getJSONArray("weather").getJSONObject(0);
                    weatherList.add(new Weather(day.getLong("dt"),temp.getDouble("min"),temp.getDouble("max"),day.getDouble("humidity"),weather.getString("description"),weather.getString("icon")));
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

}
