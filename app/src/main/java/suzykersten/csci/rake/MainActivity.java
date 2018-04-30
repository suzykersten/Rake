package suzykersten.csci.rake;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button_reps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Transfer to Rep Activity for listing reps
                Intent intent = new Intent(getApplicationContext(), RepresentativeActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.button_bills).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Transfer to Rep Activity for listing reps
                Intent intent = new Intent(getApplicationContext(), BillActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.button_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Transfer to Rep Activity for listing reps
                Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
                startActivity(intent);
            }
        });
    }




}
