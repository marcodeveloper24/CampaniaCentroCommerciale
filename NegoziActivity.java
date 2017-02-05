package com.developer.marcocicala.centrocampania;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.WindowManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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

public class NegoziActivity extends AppCompatActivity {

    InterstitialAd mInterstitialAd;

    RVAdapterNegozi adapterNegozi;
    CoordinatorLayout coordinatorLayout;
    //searchview
    private SearchView mSearchView;
    //ListView lv_negozi;
    RecyclerView rvNegozi;
    // Progress Dialog
    private ProgressDialog pDialog;
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    ArrayList<HashMap<String, String>> negoziList;
    // url to get all products list
    private static String url_all_negozi = "http://marcocicala.altervista.org/newCampania/new_get_negozi.php";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "negozi";
    private static final String TAG_PID = "id";
    private static final String TAG_NAME = "nome";
    private static final String TAG_LINK = "link";
    // products JSONArray
    JSONArray products = null;

    ArrayList<Negozi> shopList;
    DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_negozi);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        dbHandler = new DBHandler(this);
        shopList = new ArrayList<>();

        //mSearchView = (SearchView) findViewById(R.id.search_view);
        negoziList = new ArrayList<HashMap<String, String>>();
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.content_negozi);
        rvNegozi = (RecyclerView) findViewById(R.id.rv_negozi);
        rvNegozi.setHasFixedSize(true);

        //lv_negozi = (ListView) findViewById(R.id.list_negozi);

        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvNegozi.setLayoutManager(llm);
        //lv_negozi.setTextFilterEnabled(true);

        //lv_negozi.setOnItemClickListener(this);
        new LoadAllProducts().execute();

        //RecyclerView OnItemTouchListener
        rvNegozi.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), rvNegozi ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever
                        for (int i=0; i<rvNegozi.getAdapter().getItemCount(); i++){
                            if (i == position){
                                String link = negoziList.get(i).get("link").toLowerCase();
                                if (!link.startsWith("http://") && !link.startsWith("https://")){
                                    link = "http://" + link;
                                }
                                startIntent(getApplicationContext(), link);
                            } else {

                            }
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        for (int i=0; i<rvNegozi.getAdapter().getItemCount(); i++){
                            if (i==position){
                                setShopList(dbHandler, negoziList, i);
                            }
                        }
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

    public void setShopList(DBHandler dbHandler, ArrayList<HashMap<String, String>> negoziList, int position){
        String nome = negoziList.get(position).get("nome").toUpperCase();
        Negozi negozi = new Negozi();
        negozi.setId(position);
        negozi.setNome(nome);
        showAlertShopping(negozi, dbHandler);

    }

    public void showAlertShopping(final Negozi negozi, final DBHandler dbHandler){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(NegoziActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("Itinerario di shopping");
        // Setting Dialog Message
        alertDialog.setMessage("Aggiungere " + negozi.getNome() + " al tuo itinerario?");
        // On pressing Settings button
        alertDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                dbHandler.addShop(negozi);
            }
        });
        // Showing Alert Message
        alertDialog.show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_negozi, menu);
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
            List<Negozi> negozis = dbHandler.getAllShops();
            for (Negozi negozi : negozis){
                String log = "ID = " + negozi.getId() + " NOME = " + negozi.getNome();
                Log.d("NEGOZIO::", log);
            }
        }

        return super.onOptionsItemSelected(item);
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
            pDialog = new ProgressDialog(NegoziActivity.this);
            pDialog.setMessage("Caricamento negozi...");
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

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    products = json.getJSONArray(TAG_PRODUCTS);

                    // looping through All Products
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_PID);
                        String name = c.getString(TAG_NAME);
                        String link = c.getString(TAG_LINK);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_PID, id);
                        map.put(TAG_NAME, name.toUpperCase());
                        map.put(TAG_LINK, link.toLowerCase());

                        // adding HashList to ArrayList
                        negoziList.add(map);

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

            //SystemClock.sleep(5000);

            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    RVAdapterNegozi rvAdapterNegozi = new RVAdapterNegozi(negoziList, getApplicationContext());
                    rvNegozi.setAdapter(rvAdapterNegozi);
                    rvAdapterNegozi.notifyDataSetChanged();
                }
            });

        }

    }

}
