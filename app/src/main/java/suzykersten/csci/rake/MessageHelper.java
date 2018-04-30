package suzykersten.csci.rake;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by Owner on 4/29/2018.
 * Intened to help this app send messages: email, text, and phone call
 *
 */

public class MessageHelper {

    private Context context;

    public MessageHelper(Context context){
        this.context = context;
    }

    /**
     * Some of this was taken from the Paint app last assignment
     * @param emailAddress
     * @param emailSubject
     * @param message
     */
    public void startEmailActivity(String emailAddress, String emailSubject, String message){
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        String [] formattedEmail = {emailAddress};
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
    public Intent getEmailActivity(String emailAddress, String emailSubject, String message){
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        String [] formattedEmail = {emailAddress};
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
    public void startTextAcitivity(String phoneNumber, String textMessage){
        Intent phoneIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
        phoneIntent.putExtra("sms_text", textMessage);
        this.context.startActivity(phoneIntent);
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
