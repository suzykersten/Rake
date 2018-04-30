package suzykersten.csci.rake;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
    private Vector<Official> officialsVector;
    private Vector<ImageView> officialImageViewVector;
    private Vector<Drawable> officialDrawableVector;

    private GetPhotoFromURLTask getPhotoFromURLTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_representative);
        Log.i(TAG_REP_ACT, "onCreate");

        // setup ListView variables to null or empty for safety
        setupVariables();

        // activate the get reps by addr button
        findViewById(R.id.button_get_reps_for_addr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                officialDrawableVector.clear();
                officialsVector.clear();
                officialImageViewVector.clear();

                if ( getPhotoFromURLTask != null ){
                    getPhotoFromURLTask.cancel(true);
                }

                ( (TextView) findViewById(R.id.textView_rep)).setText("Loading...");

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
        officialImageViewVector = new Vector<>();
        officialDrawableVector = new Vector<>();
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
//            rawTextTextView.setText(response.toString());
            rawTextTextView.setText("Found Address \"" + address + "\" \n Loading...");

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

                    // add email if exists
                    String email;
                    try {
                        email = jsonArrayOfficials.getJSONObject(i).get("emails").toString().trim().split("[\\[,\\] ]")[1];
                    } catch (JSONException e){
                        Log.e(TAG_REP_ACT, "no val for emails");
                        email = "";
                    }
                    Log.i(TAG_REP_ACT, "email = " + email);

                    // add phone if exists
                    String phone;
                    try {
                        phone = jsonArrayOfficials.getJSONObject(i).get("phones").toString().trim().split("[\\[,\\]]")[1];
                    } catch (JSONException e){
                        Log.e(TAG_REP_ACT, "no val for emails");
                        phone = "";
                    }
                    Log.i(TAG_REP_ACT, "phone = " + phone);

                    officialsVector.add( new Official(name, photoUrl, email, phone) );

//                    if (!photoUrl.equals(null) || !photoUrl.equals("")){
//                        Drawable officalPhoto = getPhotoFromUrl(photoUrl);
//                        officialDrawableVector.add(officalPhoto);
//                    } else {
//                        officialDrawableVector.add(null);
//                    }

                    Log.i(TAG_REP_ACT, "officialDrawableVector = " + officialDrawableVector);
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

//                officialsListView.setAdapter(new RepresentativeListAdapter(getApplicationContext(), R.layout.rep_row_item, R.id.listView_reps, officialsVector));

                GetPhotoFromURLTask getPhotoFromURLTask = new GetPhotoFromURLTask(officialsVector, R.drawable.def_person_photo);
                getPhotoFromURLTask.execute();

                Log.i(TAG_REP_ACT, "officialImageViewVector = "  + officialImageViewVector.toString());
                // set the OnItemClickListener for handle user 'taps' on the items in the list
//                officialsListView.setOnItemClickListener(new OfficialsItemClickedExample());
            } catch (JSONException e) {
                Log.e(TAG_REP_ACT, "Error in listening to representative response");
                e.printStackTrace();

            }

        }
    }

//    private String JSONObjectToString(JSONObject jsonObject){
//        String strJSONObject =
////        officeName = jsonObject.get("name").toString();
////        jsonArrayOfficalIndices = jsonObject.get("officialIndices").toString().trim();
////        strings = jsonArrayOfficalIndices.split("[\\[,\\] ]");
//    }

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

            final Official official = officials.get(position);
            Log.i(TAG_REP_ACT, "official = " + official);

            // build list item
            View listViewItem = LayoutInflater.from(mContext).inflate(R.layout.rep_row_item, parent, false);

            // set the name
            ( (TextView) listViewItem.findViewById(R.id.textview_official_name)).setText(official.getName());

            // set the position
            ( (TextView) listViewItem.findViewById(R.id.textview_official_position)).setText(official.getPosition());

            // get and set the photo
//            Log.i(TAG_REP_ACT, "official.getPhotoUrl() = " + official.getPhotoUrl());
//            if (!official.getPhotoUrl().equals(null) || !official.getPhotoUrl().equals("")){
//                Drawable officalPhoto = getPhotoFromUrl(official.getPhotoUrl());
//                ((ImageView) listViewItem.findViewById(R.id.imageview_official_photo)).setBackground(officalPhoto);
//            }

            // add the ImageView HASH to a queue so that an asyncTask can handle filling it off the main UI Thread
            ImageView imageView = listViewItem.findViewById(R.id.imageview_official_photo);
            if (officialDrawableVector.size() > 0){
                try{
                    imageView.setBackground(officialDrawableVector.get(position));
                } catch (Exception e){
                    Log.e(TAG_REP_ACT, "Something went real bad!");
                    e.printStackTrace();
                }
            }

            // set the button
            ( (Button) listViewItem.findViewById(R.id.button_email_official)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MessageHelper messageHelper = new MessageHelper(getApplicationContext());
                    messageHelper.startEmailActivity(official.getEmailAddress(), "Email to " + official.getName(), "");
                }
            });

//            officialImageViewVector.add(imageView);
//            Log.i(TAG_REP_ACT, "2 officialImageViewVector = " + officialImageViewVector);

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
        private String emailAddress;
        private String phoneNumber;

        public Official(String name, String photoUrl, String position){
            this.name = name;
            this.position = position;
            this.photoUrl = photoUrl;
        }

        public Official(String name, String photoUrl){
            this.name = name;
            this.photoUrl = photoUrl;
        }

        public Official(String name, String photoUrl, String emailAddress, String phoneNumber){
            this.name = name;
            this.photoUrl = photoUrl;
            this.emailAddress = emailAddress;
            this.phoneNumber = phoneNumber;
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

        public String getEmailAddress() {
            return emailAddress;
        }

        public void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        @Override
        public String toString() {
            return "name = " + this.name + ", pos = " + this.position + ", photoUrl = " + this.photoUrl + ", emailAddress = " + this.emailAddress + ", phoneNumber = " + this.phoneNumber;
        }
    }

    /**
     * Avoid -> NetworkOnMainThreadException
     */
    private class GetPhotoFromURLTask extends AsyncTask<Void, Integer, Void>{

        private Vector<Official> officials;
        Drawable drawable;
        int defaultImageRes;
        Drawable defaultDrawable;

        @Override
        protected void onPreExecute() {
            drawable = getDrawable(defaultImageRes);
            super.onPreExecute();
        }

        public GetPhotoFromURLTask(Vector<Official> officials, int defaultImageRes){
            this.officials = officials;
            this.defaultImageRes = defaultImageRes;
            this.defaultDrawable = getDrawable(defaultImageRes);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String photoUrl = "";
            final int REQUIRED_STRING_SIZE = 5;
            for (int i = 0; i < officials.size(); i++){
                Log.i(TAG_REP_ACT, "officials.get(i).getPhotoUrl() = " + officials.get(i).getPhotoUrl());
                Log.i(TAG_REP_ACT, "officials.get(i).getPhotoUrl().equals(\"\") = " + officials.get(i).getPhotoUrl().equals(""));

                photoUrl = officials.get(i).getPhotoUrl();

                if ( !photoUrl.equals("") || photoUrl.length() > REQUIRED_STRING_SIZE){
                    drawable = scaleDownDrawable(getPhotoFromUrl(officials.get(i).getPhotoUrl()));
//                    drawable = getPhotoFromUrl(officials.get(i).getPhotoUrl());
                    officialDrawableVector.add(drawable);
                } else {
                    officialDrawableVector.add(defaultDrawable);
                    Log.i(TAG_REP_ACT, "[YES] add to the Drawable vector");
                }
//                else {
//                    Log.i(TAG_REP_ACT, "[NO]  Did not add to the Drawable vector");
//                }

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
//            int index = values[0];
//            Log.i(TAG_REP_ACT, "index = " + index);
//            Log.i(TAG_REP_ACT, "officialDrawableVector.size() = " + officialDrawableVector.size());
//            Log.i(TAG_REP_ACT, "officialImageViewVector.size() = " + officialImageViewVector.size());
//            if ( officialDrawableVector.get(index) != null ){
//                officialImageViewVector.get(index).setBackground(officialDrawableVector.get(index));
//            }
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            officialsListView.setAdapter(new RepresentativeListAdapter(getApplicationContext(), R.layout.rep_row_item, R.id.listView_reps, officialsVector));

            rawTextTextView.setText("Found Address \"" + address + "\"");
            super.onPostExecute(aVoid);
        }

        /**
         * Take Url; grab photo; return photo
         * @param url
         * @return
         */
        private Drawable getPhotoFromUrl(String url){
            Log.i(TAG_REP_ACT, "getPhotoFromUrl(String url); url = " + url);
            try {
                InputStream is = (InputStream) new URL(url).getContent();
                drawable = Drawable.createFromStream(is, "PhotoFromUrl");
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (drawable == null) {
                Log.i(TAG_REP_ACT, "drawable is null!");
            }
            return drawable;
        }

        /**
         * Scale down an image
         * Inspiration: https://stackoverflow.com/questions/7021578/resize-drawable-in-android
         * @param convertDrawable
         * @return the new Drawable
         */
        private Drawable scaleDownDrawable(Drawable convertDrawable){
            Bitmap convertedBitmap = ((BitmapDrawable)convertDrawable).getBitmap();
            Bitmap bitmapResized = Bitmap.createScaledBitmap(convertedBitmap, 50, 50, false);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmapResized);
            return bitmapDrawable;
        }
    }



}
