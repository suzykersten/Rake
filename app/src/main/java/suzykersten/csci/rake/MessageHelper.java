package suzykersten.csci.rake;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by Owner on 4/29/2018.
 * Intened to help this app send messages: email, text, and phone call
 *
 */

public class MessageHelper {

    private static final String TAG_MESSAGE_HELPER = "MessageHelper";
    private Context context;

    public MessageHelper(Context context) {
        this.context = context;
    }

    /**
     * Some of this was taken from the Paint app last assignment
     * @param emailAddress
     * @param emailSubject
     * @param message
     */
    public void startEmailActivity(String emailAddress, String emailSubject, String message) {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        String[] formattedEmail = {emailAddress};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, formattedEmail);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        this.context.startActivity(emailIntent);
    }

    /**
     * Some of this was taken from the Paint app last assignment
     * @param emailAddress
     * @param emailSubject
     * @param message
     */
    public Intent getEmailActivity(String emailAddress, String emailSubject, String message) {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        String[] formattedEmail = {emailAddress};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, formattedEmail);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        return emailIntent;
    }

    /**
     * Inspiration - https://stackoverflow.com/questions/4967448/send-sms-in-android
     * @param phoneNumber
     * @param textMessage
     * @return
     */
    public void startTextAcitivity(String phoneNumber, String textMessage) {
        Intent phoneIntent = new Intent(Intent.ACTION_VIEW);
        phoneIntent.setData(Uri.parse("sms:"));
        phoneIntent.putExtra("address", phoneNumber);
        phoneIntent.putExtra("sms_body", textMessage);
        this.context.startActivity(phoneIntent);
    }

    public void startCallActivity(Activity activity, String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));

        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.i(TAG_MESSAGE_HELPER, "You don't have permission, sorry bud.");

            //ask for permission
            String [] permissions = {Manifest.permission.CALL_PHONE};
            ActivityCompat.requestPermissions(activity, permissions, 0);


            return;
        }
        this.context.startActivity(callIntent);
    }

    /**
     * Inspiration - https://stackoverflow.com/questions/4967448/send-sms-in-android
     * @param phoneNumber
     * @param textMessage
     * @return
     */
    public Intent getSMSIntent(String phoneNumber, String textMessage){
        Intent phoneIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
        phoneIntent.putExtra("sms_text", textMessage);
        return phoneIntent;
    }
}
