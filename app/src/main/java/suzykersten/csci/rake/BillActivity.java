package suzykersten.csci.rake;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

public class BillActivity extends Activity {
    public static final String TAG_BILL_ACT = "BILL_ACT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        //Populate the Listview with new data
        ListView listView = findViewById(R.id.listView_bills);
        DownloadBills dlBills = new DownloadBills("https://sskersten.bitbucket.io/json/hr.json", listView);

        dlBills.execute();
    }
}


class DownloadBills extends AsyncTask<String, Integer, Bill[]>{
    private String stringURL;
    private ListView listView;

    public DownloadBills(String stringURL, ListView listView) {
        this.stringURL = stringURL;
        this.listView = listView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    private String readStream(InputStream in){
        Scanner s = new Scanner(in).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @Override
    protected Bill[] doInBackground(String... strings) {
        String result = "";

        //make a url
        URL url = null;
        try {
            url = new URL(stringURL);
        } catch (MalformedURLException e) {
            Log.e("ReadBill", "Bad URL given.");
            e.printStackTrace();
        }

        //connect to the internet
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            Log.e("ReadBill", "Couldn't open http connection.");
            e.printStackTrace();
        }

        //read in data from the internet
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            result = readStream(in);
        } catch (IOException e) {
            Log.e("ReadBill", "Couldn't read data from URL connection");
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        //convert gotten json to Bills objects
        Gson gson = new Gson();
        return gson.fromJson(result, Bill[].class);
    }

    //update the layout with the requested set of bills
    @Override
    protected void onPostExecute(Bill[] bills) {
        listView.setAdapter(new ArrayAdapter<>(listView.getContext(),android.R.layout.simple_list_item_1, bills));
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }
}

