package suzykersten.csci.rake;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.Vector;

public class RepresentativeActivity extends Activity {
    public static final String TAG_REP_ACT = "REP_ACT";

    // Volley stuff
//    RequestQueue requestQueue;
//    Cache cache = new DiskBasedCache( getApplicationContext().getCacheDir(), 1024*1024);
//    Network network = new BasicNetwork(new HurlStack());

//    String url = "http://ip.jsontest.com/";
    private String key = "AIzaSyBm7xKa2dCeg3nvNzWr_FLWr6PsD3d-U3A";
    private String googleApiRepByAddr = "https://www.googleapis.com/civicinfo/v2/representatives";
    private String address = "1237 Rossview Rd";
    //https://www.googleapis.com/civicinfo/v2/representatives?address=1237 Rossview Rd&key=AIzaSyBm7xKa2dCeg3nvNzWr_FLWr6PsD3d-U3A works
    private ListView officialsListView;
    private TextView rawTextTextView;
//    private Vector<String> officialsVector;
    private Vector<Official> officialsVector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_representative);
        Log.i(TAG_REP_ACT, "onCreate");

        // setup ListView variables
        setupVariables();

        // activate the get reps by addr button
        findViewById(R.id.button_get_reps_for_addr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get address
                address = ((EditText) findViewById(R.id.editText_address)).getText().toString();

                // build url from this address
                String url = googleApiRepByAddr + "?address="+address+"&key="+key;

                // build the json request
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new JsonRepResListener(), new JsonRepResErrListener());

                // set ID tag for the library stuff
                jsonObjectRequest.setTag(TAG_REP_ACT);

                // start a request queue for processing the response(s)
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                // add my specific request to the queues
                requestQueue.add(jsonObjectRequest);        // NOTE: THIS RUNS BACKGROUND
            }
        });
    }

    /**
     * Set variables to new objects
     */
    public void setupVariables(){
        // list for reps of your location
        officialsListView = findViewById(R.id.listView_reps);
        rawTextTextView = findViewById(R.id.textView_rep);
        officialsVector = new Vector<>();
    }

    /**
     * Setup a listener class for getting the JSON response of the address-based Representatives
     */
    private class JsonRepResListener implements Response.Listener<JSONObject>{

        /**
         * List to the response of the JSON request
         * @param response
         */
        @Override
        public void onResponse(JSONObject response) {
            Log.i(TAG_REP_ACT, "onResponse, response = " + response);

            // set the upper textview to the string (RAW) JSON response
            rawTextTextView.setText(response.toString());

            // fill a vector with officials
            try {
                JSONArray jsonArrayOfficials = response.getJSONArray("officials");
                JSONArray jsonArrayOffices = response.getJSONArray("offices");

                // array for the Indices positions
                String name = "";
                String position = "";
                String photoUrl = "";

                // for each entry in the JSONArray
                for (int i = 0; i < jsonArrayOfficials.length(); i++){

                    // Log for debugging the list adds
                    Log.i(TAG_REP_ACT, "jsonArray.getJSONObject(i).get(\"name\").toString() = " + jsonArrayOfficials.getJSONObject(i).get("name").toString());

                    // add to the array
                    name = jsonArrayOfficials.getJSONObject(i).get("name").toString();
                    try {
                        photoUrl = jsonArrayOfficials.getJSONObject(i).get("photoUrl").toString();
                    } catch (JSONException e){
                        Log.e(TAG_REP_ACT, "no val for photo URL");
                        photoUrl = "";
                    }

                    officialsVector.add( new Official(name, photoUrl) );
                }

                // Now we modify the officials array to 'append' the position
                JSONObject jsonObject;
                String officeName;
                int index;
                String str;
                String jsonArrayOfficalIndices;
                String[] strings;
                Log.i(TAG_REP_ACT, "jsonArrayOffices.length() = " + jsonArrayOffices.length());

                for (int i = 0; i < jsonArrayOffices.length(); i++){
                    jsonObject = jsonArrayOffices.getJSONObject(i);
                    officeName = jsonObject.get("name").toString();
                    jsonArrayOfficalIndices = jsonObject.get("officialIndices").toString().trim();
                    strings = jsonArrayOfficalIndices.split("[\\[,\\] ]");
                    Log.i(TAG_REP_ACT, "jsonArrayOfficalIndices = " + jsonArrayOfficalIndices);

                    for (int j = 1; j < strings.length; j++){
                        Log.i(TAG_REP_ACT, "strings = '" + strings[j] + "'");

//                        str =.toString();
//                        Log.i(TAG_REP_ACT, "str = " + str);
                        index = Integer.parseInt(strings[j]);
                        Log.i(TAG_REP_ACT, "index = " + index);
                        officialsVector.get(index).setPosition(officeName);
                    }
                }

                Log.i(TAG_REP_ACT, "officialsVector = " + officialsVector);

                // set the adapter for the officers
//                officialsListView.setAdapter(new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1, officialsVector));
//        public RepresentativeListAdapter(@NonNull Context context, int resource, int textViewResourceId, Vector<Official> officials ) {

                officialsListView.setAdapter(new RepresentativeListAdapter(getApplicationContext(), R.layout.rep_row_item, R.id.listView_reps, officialsVector));

                // set the OnItemClickListener for handle user 'taps' on the items in the list
//                officialsListView.setOnItemClickListener(new OfficialsItemClickedExample());
            } catch (JSONException e) {
                Log.e(TAG_REP_ACT, "Error in listening to representative response");
                e.printStackTrace();

            }

        }
    }

    private class JsonRepResErrListener implements Response.ErrorListener {

        /**
         * Something went busted
         * @param error
         */
        @Override
        public void onErrorResponse(VolleyError error) {

            // Log error to the Logcat
            Log.e(TAG_REP_ACT, "onErrorResponse, error = " + error);

            // let the user know we can't use this address
            rawTextTextView.setText("Address not found. Please try a different address.");
        }
    }

    /**
     * Handle the user clicking their representative's name
     */
    private class OfficialsItemClickedExample implements AdapterView.OnItemClickListener{

        /**
         * What happens when the use clicks the item in the listview
         *      google that person's name (temp?)
         * @param parent
         * @param view
         * @param position
         * @param id
         */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            // get the string
//            String itemInOfficialsVector = officialsVector.get(position);

            // log the to console the name
//            Log.d(TAG_REP_ACT, "itemInOfficialsVector = " + itemInOfficialsVector);

            // search it!
//            searchWeb(itemInOfficialsVector);

        }

        private void searchWeb(String query){

            // build Intent for search
            Intent searchIntent = new Intent(Intent.ACTION_WEB_SEARCH);
            searchIntent.putExtra(SearchManager.QUERY, query);
            startActivity(searchIntent);
        }
    }

    // ============================
    // === CUSTOM ADAPTER ITEMS ===
    // ============================

    /**
     * Source: https://www.journaldev.com/10416/android-listview-with-custom-adapter-example-tutorial
     *
     */
    private class RepresentativeListAdapter extends ArrayAdapter<Official>{

        private Vector<Official> officials;
        private Context mContext;

        public RepresentativeListAdapter(@NonNull Context context, int resource, int textViewResourceId, Vector<Official> officials ) {
            super(context, resource, textViewResourceId, officials); //https://stackoverflow.com/questions/9730328/the-getview-method-of-arrayadapter-is-not-getting-called
            this.officials = officials;
            this.mContext = context;
            Log.i(TAG_REP_ACT, "officials = " + officials);
            Log.i(TAG_REP_ACT, "context = " + context);
            Log.i(TAG_REP_ACT, "resource = " + resource);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            Official official = officials.get(position);
            Log.i(TAG_REP_ACT, "official = " + official);

            // build list item
            View listViewItem = LayoutInflater.from(mContext).inflate(R.layout.rep_row_item, parent, false);

            // set the name
            ( (TextView) listViewItem.findViewById(R.id.textview_official_name)).setText(official.getName());

            // set the position
            ( (TextView) listViewItem.findViewById(R.id.textview_official_name)).setText(official.getPosition());

            return listViewItem;
        }
    }

    /**
     * Java model of an official for the user, such as President of US or Comptroller of county
     */
    private class Official{
        private String name;
        private String position;
        private String photoUrl;

        public Official(String name, String photoUrl, String position){
            this.name = name;
            this.position = position;
            this.photoUrl = photoUrl;
        }

        public Official(String name, String photoUrl){
            this.name = name;
            this.photoUrl = photoUrl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }

        @Override
        public String toString() {
            return "name = " + this.name + ", pos = " + this.position + ", photoUrl = " + this.photoUrl;
        }
    }
}
