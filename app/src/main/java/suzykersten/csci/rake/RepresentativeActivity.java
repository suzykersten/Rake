package suzykersten.csci.rake;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.VolleyError;

import org.json.JSONObject;

public class RepresentativeActivity extends Activity {
    public static final String TAG_REP_ACT = "REP_ACT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_representative);
//        JSONObject
        String url = "http://ip.jsontest.com/";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                ((TextView) findViewById(R.id.textView)).setText("Res: " + response.toString());
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    // TODO: Handle error
                    Log.i(TAG_REP_ACT, "onErrorResponse");
                }
            }
        });
    }
}
