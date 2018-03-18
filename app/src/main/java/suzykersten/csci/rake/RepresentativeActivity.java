package suzykersten.csci.rake;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONObject;

public class RepresentativeActivity extends Activity {
    public static final String TAG_REP_ACT = "REP_ACT";

    // Volley stuff
//    RequestQueue requestQueue;
//    Cache cache = new DiskBasedCache( getApplicationContext().getCacheDir(), 1024*1024);
//    Network network = new BasicNetwork(new HurlStack());

    String url = "http://ip.jsontest.com/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_representative);
//        requestQueue = new RequestQueue(cache, network);
        Log.i(TAG_REP_ACT, "onCreate");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new JsonRepResListener(), new JsonRepResErrListener());
        jsonObjectRequest.setTag(TAG_REP_ACT);
//        requestQueue.add(jsonObjectRequest);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
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
        }
    }

    private class JsonRepResErrListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.i(TAG_REP_ACT, "onErrorResponse, error = " + error);
        }
    }
}
