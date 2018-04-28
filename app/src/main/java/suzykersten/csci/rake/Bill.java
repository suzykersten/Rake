package suzykersten.csci.rake;

/**
 * Created by Suzanne on 4/4/2018.
 */

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//holds the most basic information about a bill that we'll need.
public class Bill implements Comparable<Bill>{


    private String title;
    private String stage;
    private String actionDate;
    private String chamber;
    private String linkToFull;

    public Bill() {
    }

    public Bill(String title, String stage, String actionDate, String chamber, String linkToFull) {
        this.title = title;
        this.stage = stage;
        this.actionDate = actionDate;
        this.chamber = chamber;
        this.linkToFull = linkToFull;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getActionDate() {
        return actionDate;
    }

    public void setActionDate(String actionDate) {
        //check that an actual date was input
        DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
        try {
            Date date = format.parse(actionDate);   //if this succeeds, we have a valid date!
        } catch (ParseException parseException) {
            //System.out.println("failed with bill " + linkToFull);

            //try and parse as a YYYYMMDD string and toss in actionDate
            DateFormat format2 = new SimpleDateFormat("YYYYMMDD");
            try {
                Date date = format.parse(actionDate);

                actionDate = format.format(date);

                //actionDate = format.format(new Date());
            } catch (ParseException ex) {
                //System.out.println("----------- SUPER failed with bill " + linkToFull);
                //Logger.getLogger(Bill.class.getName()).log(Level.SEVERE, null, ex);

                actionDate = format.format(new Date());

            }
        }

        this.actionDate = actionDate;
    }

    public String getChamber() {
        return chamber;
    }

    public void setChamber(String chamber) {
        this.chamber = chamber;
    }

    public String getLinkToFull() {
        return linkToFull;
    }

    public void setLinkToFull(String linkToFull) {
        this.linkToFull = linkToFull;
    }

    public String toString(){
        return title + " : "  + actionDate;
    }

    @Override
    public int compareTo(Bill o) {

        if (actionDate == "" && o.actionDate == ""){
            return 0;
        } else if (actionDate == ""){
            return 1;
        } else if (o.actionDate == ""){
            return -1;
        }

       /*
        if (actionDate == "__________" && o.actionDate == "__________"){
            return 0;
        } else if (actionDate == "__________"){
            return 1;
        } else if (o.actionDate == "__________"){
            return -1;
        }*/

        DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);

        Date date = null;
        Date dateRight = null;

        //try to parse the left date
        try {
            date = format.parse(actionDate);
        } catch (ParseException parseException) {
            parseException.printStackTrace();
            /*
            actionDate = format.format(new Date());
            try {
                date = format.parse(actionDate);
            } catch (ParseException ex) {
                Logger.getLogger(Bill.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("------------------ had to convert Date to now: " + date);
            *///return 0;
        }

        //try to parse the right date
        try {
            dateRight = format.parse(o.actionDate);
        } catch (ParseException parseException) {
            parseException.printStackTrace();
            /*
                o.actionDate = format.format(new Date());
               try {
                dateRight = format.parse(o.actionDate);

                System.out.println("-------- had to convert rightDate to now: " + dateRight);
                return 0;
            } catch (ParseException ex) {
                Logger.getLogger(Bill.class.getName()).log(Level.SEVERE, null, ex);
            } */
        }

        return date.compareTo(dateRight);
    }
}