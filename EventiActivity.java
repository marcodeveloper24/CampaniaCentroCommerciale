package com.developer.marcocicala.centrocampania;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventiActivity extends AppCompatActivity {

    InterstitialAd mInterstitialAd;

    WebView webView;
    RecyclerView rvEventi;
    private ProgressDialog pDialog;
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    ArrayList<HashMap<String, String>> eventiList;
    // url to get all products list
    private static String url_all_negozi = "http://marcocicala.altervista.org/newCampania/new_get_eventi.php";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "eventi";
    private static final String TAG_PID = "id";
    private static final String TAG_TITOLO = "titolo";
    private static final String TAG_DESC = "descrizione";
    private static final String TAG_LINK = "link";
    private static final String TAG_LOGO = "image";
    private static final String URL_EVENTI = "http://marcocicala.altervista.org/newCampania/new_create_eventi.php";
    // products JSONArray
    JSONArray products = null;

    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventi);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView = (WebView) findViewById(R.id.web_view);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.content_eventi);

        eventiList = new ArrayList<HashMap<String, String>>();
        rvEventi = (RecyclerView) findViewById(R.id.rv_eventi);
        rvEventi.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        rvEventi.setLayoutManager(llm);

        webView.loadUrl("http://marcocicala.altervista.org/newCampania/new_create_eventi.php");

        new LoadAllProducts().execute();

        rvEventi.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), rvEventi ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever
                        for (int i=0; i<rvEventi.getAdapter().getItemCount(); i++){
                            if (i == position){
                                String link = eventiList.get(i).get("link").toLowerCase();
                                startIntent(getApplicationContext(), link);
                            } else {

                            }
                        }
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        //AdMob
        mInterstitialAd = new InterstitialAd(this);
        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });

    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    public void startIntent(Context context, final String link){
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Aprire il link nel browser?", Snackbar.LENGTH_LONG)
                .setAction("Apri", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(link));
                        startActivity(intent);
                    }
                });

        snackbar.show();

    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllProducts extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EventiActivity.this);
            pDialog.setMessage("Caricamento eventi...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_negozi, "GET", params);

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
                eventiList.clear();

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    products = json.getJSONArray(TAG_PRODUCTS);

                    // looping through All Products
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_PID);
                        String titolo = c.getString(TAG_TITOLO);
                        String descrizione = c.getString(TAG_DESC);
                        String link = c.getString(TAG_LINK);
                        String image = c.getString(TAG_LOGO);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_PID, id);
                        map.put(TAG_TITOLO, titolo.toUpperCase());
                        map.put(TAG_DESC, descrizione);
                        map.put(TAG_LINK, link.toLowerCase());
                        map.put(TAG_LOGO, image);

                        // adding HashList to ArrayList
                        eventiList.add(map);
                    }
                } else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    RVAdapterEventi adapter = new RVAdapterEventi(eventiList, getApplicationContext());
                    rvEventi.setAdapter(adapter);

                }
            });

        }

    }

}



