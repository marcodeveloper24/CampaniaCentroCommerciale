package com.developer.marcocicala.centrocampania;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    WebView webView;

    //variabili meteo
    Typeface weatherFont;
    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView weatherIcon;
    Handler handler;

    //ImageView apertura
    ImageView imageShapeApCh;
    //TextView data
    TextView textData;

    private static String FACEBOOK_URL = "https://www.facebook.com/CampaniaCentroCommercialeApp/";
    private static String FACEBOOK_URL_CCC = "https://www.facebook.com/CentroCommercialeCampania";
    private static String FACEBOOK_PAGE_ID = "CampaniaCentroCommercialeApp";
    private static String FACEBOOK_PAGE_ID_CCC = "CentroCommercialeCampania";
    private static String PAYPAL_ME_URL = "https://paypal.me/MarcoCicala";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        webView = (WebView) findViewById(R.id.web_view);

        //metodi e dichiarazioni meteo
        handler = new Handler();
        weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weather.ttf");
        cityField = (TextView) findViewById(R.id.city_field);
        updatedField = (TextView) findViewById(R.id.updated_field);
        detailsField = (TextView) findViewById(R.id.details_field);
        currentTemperatureField = (TextView) findViewById(R.id.current_temperature_field);
        weatherIcon = (TextView) findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);
        updateWeatherData(new CityPreference(this).getCity());

        //imageShapeApCh dichiarazione
        imageShapeApCh = (ImageView) findViewById(R.id.image_apch);
        //TextView Data dichiarazione
        textData = (TextView) findViewById(R.id.text_data);

        //Data e ApCh
        getDataTime(imageShapeApCh, textData);

        webView.loadUrl("http://marcocicala.altervista.org/newCampania/new_create_eventi.php");


    }
    public void showAlertInfo(){

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("Informazioni Campania Centro Commerciale");

        // Setting Dialog Message
        alertDialog.setMessage("Versione: " + version +"\nSviluppatore: Marco Cicala");

        // On pressing Settings button
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {

            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            showAlertInfo();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_parcheggio) {
            startActivity(new Intent(MainActivity.this, MapsActivity.class));
        } else if (id == R.id.nav_negozi) {
            startActivity(new Intent(MainActivity.this, NegoziActivity.class));
        } else if (id == R.id.nav_eventi) {
            startActivity(new Intent(MainActivity.this, EventiActivity.class));
        } else if (id == R.id.nav_mappa) {
            startActivity(new Intent(MainActivity.this, MappaActivity.class));
        } else if (id == R.id.nav_fbapp) {
            Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
            String facebookUrl = getFacebookPageURL(this);
            facebookIntent.setData(Uri.parse(facebookUrl));
            startActivity(facebookIntent);
        } else if (id == R.id.nav_fbc) {
            Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
            String facebookUrl = getFacebookPageURLCCC(this);
            facebookIntent.setData(Uri.parse(facebookUrl));
            startActivity(facebookIntent);
        } else if (id == R.id.nav_dona){
            Intent paypalIntent = new Intent(Intent.ACTION_VIEW);
            paypalIntent.setData(Uri.parse(PAYPAL_ME_URL));
            startActivity(paypalIntent);
        } else if (id == R.id.nav_cinema){
            startActivity(new Intent(MainActivity.this, CinemaActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //metodi meteo
    private void updateWeatherData(final String city){
        new Thread(){
            public void run(){
                final JSONObject json = RemoteFetch.getJSON(getApplicationContext(), city);
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getApplicationContext(),
                                    getApplicationContext().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void renderWeather(JSONObject json){
        try {
            cityField.setText(json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            detailsField.setText(
                    details.getString("description").toUpperCase(Locale.US) +
                            "\n" + "Humidity: " + main.getString("humidity") + "%" +
                            "\n" + "Pressure: " + main.getString("pressure") + " hPa");

            currentTemperatureField.setText(
                    String.format("%.2f", main.getDouble("temp"))+ " ℃");

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt")*1000));
            updatedField.setText("Last update: " + updatedOn);

            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        }catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = getString(R.string.weather_sunny);
            } else {
                icon = getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
                case 2 : icon = getString(R.string.weather_thunder);
                    break;
                case 3 : icon = getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = getString(R.string.weather_foggy);
                    break;
                case 8 : icon = getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = getString(R.string.weather_snowy);
                    break;
                case 5 : icon = getString(R.string.weather_rainy);
                    break;
            }
        }
        weatherIcon.setText(icon);
    }

    public void getDataTime(ImageView imageShapeApCh, TextView textData){
        DateFormat df = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.ITALIAN);
        String data = df.format(Calendar.getInstance().getTime());
        textData.setText(data);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >=9 && hour <= 24){
            imageShapeApCh.setImageDrawable(getResources().getDrawable(R.drawable.shape_ap));
        } else {
            imageShapeApCh.setImageDrawable(getResources().getDrawable(R.drawable.shape_ch));
        }
    }

    //method to get the right URL to use in the intent
    public String getFacebookPageURL(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }

    public String getFacebookPageURLCCC(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL_CCC;
            } else { //older versions of fb app
                return "fb://page/" + FACEBOOK_PAGE_ID_CCC;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }

}
