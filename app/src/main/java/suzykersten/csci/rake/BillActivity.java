package suzykersten.csci.rake;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class BillActivity extends Activity {

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


    }
}
