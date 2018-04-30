package suzykersten.csci.rake;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HelpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        ( (Button) findViewById(R.id.button_learn_vote)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent learnIntent = new Intent(Intent.ACTION_VIEW);
                learnIntent.setData(Uri.parse("https://www.usa.gov/register-to-vote"));
            }
        });

        ( (Button) findViewById(R.id.button_go)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent learnIntent = new Intent(Intent.ACTION_VIEW);
                learnIntent.setData(Uri.parse("https://kids-clerk.house.gov/grade-school/lesson.html?intID=17"));
            }
        });
    }
}
