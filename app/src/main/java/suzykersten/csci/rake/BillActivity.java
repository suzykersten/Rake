package suzykersten.csci.rake;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

public class BillActivity extends Activity {
    private SparseArray<String> urls;
    private DownloadBills dlBills = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        //set up every button with the url that should work for them
        urls = new SparseArray<>();
        urls.append(    R.id.button_hconres,    "https://sskersten.bitbucket.io/json/hconres.json"  );
        urls.append(    R.id.button_hjres,      "https://sskersten.bitbucket.io/json/hjres.json"    );
        urls.append(    R.id.button_hr,         "https://sskersten.bitbucket.io/json/hr.json"       );
        urls.append(    R.id.button_hres,       "https://sskersten.bitbucket.io/json/hres.json"     );
        urls.append(    R.id.button_s,          "https://sskersten.bitbucket.io/json/s.json"        );
        urls.append(    R.id.button_sconres,    "https://sskersten.bitbucket.io/json/sconres.json"  );
        urls.append(    R.id.button_sjres,      "https://sskersten.bitbucket.io/json/sjres.json"    );
        urls.append(    R.id.button_sres,       "https://sskersten.bitbucket.io/json/sres.json"     );

        //setup the onclicks for every button to work for the url needed on each one
        LinearLayout billUrlButtons = findViewById(R.id.linearLayout_billUrlButtons);

        //for each child of the billURLButtons layout, set the button on click listener
        for (int i = 0; i < billUrlButtons.getChildCount(); i++){
            billUrlButtons.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateListViewWithDataFromUrl(view.getId());
                }
            });
        }

        //default by putting out hconres bills
        updateListViewWithDataFromUrl(R.id.button_hconres);

    }

    private void updateListViewWithDataFromUrl(int buttonId){
        //Log.i("dlbills", "Trying to access data from " + )

        //if we're trying to download data, stop that and exit.
        if (dlBills != null){
            dlBills.cancel(true);
            dlBills = null;
        }

        ListView listView = findViewById(R.id.listView_bills);
        dlBills = new DownloadBills(urls.get(buttonId), listView);
        dlBills.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.global, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.help:
                startActivity(new Intent(this, HelpActivity.class));
                return true;

        }

        return super.onOptionsItemSelected(item);
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
        listView.setAdapter(new BillAdapter(listView.getContext(), Arrays.asList(bills)));
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }
}


class BillAdapter extends ArrayAdapter<Bill>{
    private List<Bill> data;

    BillAdapter(Context context, List<Bill> data){
        super(context, R.layout.activity_bill_listrow, data);
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_bill_listrow, parent, false);
        }

        final Bill bill = data.get(position);
        setTextOfTextView(convertView, R.id.textView_title, bill.getTitle());
        setTextOfTextView(convertView, R.id.textView_date, bill.getActionDate());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(bill.getLinkToFull()));
                getContext().startActivity(intent);

            }
        });

        return convertView;
    }

    private void setTextOfTextView(View view, int id, String text){
        ((TextView) view.findViewById(id)).setText(text);
    }
}
