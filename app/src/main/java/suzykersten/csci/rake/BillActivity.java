package suzykersten.csci.rake;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class BillActivity extends Activity {
    public static final String TAG_BILL_ACT = "BILL_ACT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        findViewById(R.id.button_getBill).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBillData();
            }
        });
    }


    private void getBillData(){
        String url = "https://www.govinfo.gov/bulkdata/json/BILLSTATUS/115/s";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new BillActivity.JsonRepResListener(), new BillActivity.JsonRepResErrListener())
        {
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                headers.put("accept", "application/json");
                //headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        //jsonObjectRequest.setTag(TAG_BILL_ACT);
//        requestQueue.add(jsonObjectRequest);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);

/*
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        try {
            TextView tv = findViewById(R.id.textView_billOutput);

            URL url = new URL("https://www.govinfo.gov/content/pkg/BILLS-115sres99ats/xml/BILLS-115sres99ats.xml");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;

            while ( (line = br.readLine()) != null){
                tv.setText(tv.getText() +"\n" + line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        */


    }


    private class JsonRepResListener implements Response.Listener<JSONObject>{
        @Override
        public void onResponse(JSONObject response) {
            Log.i(TAG_BILL_ACT, "onResponse, response = " + response);
            ((TextView) findViewById(R.id.textView_billOutput)).setText(response.toString());

            // fill a vector with officials
            try {
                JSONArray jsonArray = response.getJSONArray("files");
//                Vector<JSONObject> vecOfficials = new Vector<>();
                Vector<String> vecOfficialsNames = new Vector<>();
                for (int i = 0; i < jsonArray.length(); i++){
//                    vecOfficials.add(jsonArray.getJSONObject(i));
                    Log.i(TAG_BILL_ACT, "jsonArray.getJSONObject(i).get(\"name\").toString() = " + jsonArray.getJSONObject(i).get("name").toString());
                    vecOfficialsNames.add(jsonArray.getJSONObject(i).get("name").toString());
                }
                ListView listView = findViewById(R.id.listView_bills);
                listView.setAdapter(new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1, vecOfficialsNames));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private class JsonRepResErrListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.i(TAG_BILL_ACT, "onErrorResponse, error = " + error);
            ((TextView) findViewById(R.id.textView_billOutput)).setText("Address not found. Sorry!");
        }
    }
}