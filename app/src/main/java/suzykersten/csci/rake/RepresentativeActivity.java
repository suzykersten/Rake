package suzykersten.csci.rake;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

public class RepresentativeActivity extends Activity {
    public static final String TAG_REP_ACT = "REP_ACT";

    // Volley stuff
//    RequestQueue requestQueue;
//    Cache cache = new DiskBasedCache( getApplicationContext().getCacheDir(), 1024*1024);
//    Network network = new BasicNetwork(new HurlStack());

//    String url = "http://ip.jsontest.com/";
    String key = "AIzaSyBm7xKa2dCeg3nvNzWr_FLWr6PsD3d-U3A";
    String googleApiRepByAddr = "https://www.googleapis.com/civicinfo/v2/representatives";
    String address = "1237 Rossview Rd";
    //https://www.googleapis.com/civicinfo/v2/representatives?address=1237 Rossview Rd&key=AIzaSyBm7xKa2dCeg3nvNzWr_FLWr6PsD3d-U3A works

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_representative);
//        requestQueue = new RequestQueue(cache, network);
        Log.i(TAG_REP_ACT, "onCreate");

        // activate the get reps by addr button
        findViewById(R.id.button_get_reps_for_addr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                address = ((EditText) findViewById(R.id.editText_address)).getText().toString();
                String url = googleApiRepByAddr + "?address="+address+"&key="+key;
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new JsonRepResListener(), new JsonRepResErrListener());
                jsonObjectRequest.setTag(TAG_REP_ACT);
//        requestQueue.add(jsonObjectRequest);
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(jsonObjectRequest);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // In your activity's onStop() method, cancel all requests that have this tag.
//        if (requestQueue != null) {
//            requestQueue.cancelAll(TAG_REP_ACT);
//        }
    }

    private class JsonRepResListener implements Response.Listener<JSONObject>{
        @Override
        public void onResponse(JSONObject response) {
            Log.i(TAG_REP_ACT, "onResponse, response = " + response);
            ((TextView) findViewById(R.id.textView_rep)).setText(response.toString());

            // fill a vector with officials
            try {
                JSONArray jsonArray = response.getJSONArray("officials");
//                Vector<JSONObject> vecOfficials = new Vector<>();
                Vector<String> vecOfficialsNames = new Vector<>();
                for (int i = 0; i < jsonArray.length(); i++){
//                    vecOfficials.add(jsonArray.getJSONObject(i));
                    Log.i(TAG_REP_ACT, "jsonArray.getJSONObject(i).get(\"name\").toString() = " + jsonArray.getJSONObject(i).get("name").toString());
                    vecOfficialsNames.add(jsonArray.getJSONObject(i).get("name").toString());
                }
                ListView listView = findViewById(R.id.listView_reps);
                listView.setAdapter(new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1, vecOfficialsNames));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private class JsonRepResErrListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.i(TAG_REP_ACT, "onErrorResponse, error = " + error);
            ((TextView) findViewById(R.id.textView_rep)).setText("Address not found. Sorry!");
        }
    }
}
