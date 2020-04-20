package com.example.submerge.models;

import android.content.Intent;
import android.util.Log;

import com.example.submerge.R;
import org.bson.types.ObjectId;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Subscription {
    public static Subscription decode_intent(Intent intent) {
        //Guareenteed to be there
        int image = intent.getIntExtra("sub_image", R.drawable.netflix);
        String title = intent.getStringExtra("sub_name");
        double cost = intent.getDoubleExtra("sub_cost", 0.00);
        Date renewal = renewalFromString(Objects.requireNonNull(intent.getStringExtra("sub_renewal")));
        int recurrence = recurrenceFromString(Objects.requireNonNull(intent.getStringExtra("sub_recurrence")));
        boolean trial = intent.getBooleanExtra("sub_trial", false);
        double change = intent.getDoubleExtra("sub_change", 0.00);
        return new Subscription(image, String.format("%s", title), trial, renewal, recurrence, cost, change);
    }

    public static void encode_intent(Intent intent, Subscription subscription) {
        intent.putExtra("sub_image", subscription.getImage());
        intent.putExtra("sub_name", subscription.getTitle());
        intent.putExtra("sub_cost", subscription.accessCost());
        intent.putExtra("sub_renewal", renewalFromDate(new Date(subscription.accessRenewal())));
        intent.putExtra("sub_recurrence", recurrenceFromInt(subscription.accessRecurrance()));
        intent.putExtra("sub_trial", subscription.accessTrial());
        intent.putExtra("sub_change", subscription.accessChange());
    }

    public static Date renewalFromString(String input) {
        Calendar calendar = Calendar.getInstance();
        int first_slash = input.indexOf('/');
        int second_slash = input.indexOf('/', first_slash + 1);
        int month = Integer.parseInt(input.substring(0, first_slash)) - 1;
        int day = Integer.parseInt(input.substring(first_slash + 1, second_slash));
        int year = Integer.parseInt(input.substring(second_slash + 1));
        calendar.set(year, month, day);
        return calendar.getTime();
    }

    public static String renewalFromDate(Date input) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(input);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);
        return String.format(Locale.ENGLISH, "%d/%d/%d", month + 1, dayOfMonth, year);
    }

    public static int recurrenceFromString(String input) {
        switch(input) {
            case "Weekly":
                return Subscription.Recurrences.WEEKLY;
            case "Bi-Weekly":
                return Subscription.Recurrences.BI_WEEKLY;
            case "Monthly":
                return Subscription.Recurrences.MONTHLY;
            case "Yearly":
                return Subscription.Recurrences.YEARLY;
            default:
                return Integer.parseInt(input);
        }
    }

    public static String recurrenceFromInt(int input) {
        switch (input) {
            case Recurrences.DAILY:
                return "Daily";
            case Recurrences.BI_DAILY:
                return "Bi-Daily";
            case Recurrences.WEEKLY:
                return "Weekly";
            case Recurrences.BI_WEEKLY:
                return "Bi-Weekly";
            case Recurrences.MONTHLY:
                return "Monthly";
            case Recurrences.YEARLY:
                return "Yearly";
            case Recurrences.BI_YEARLY:
                return "Bi-Yearly";
            default:
                return Integer.toString(input);
        }
    }

    public static final class Types {
        static final int MAIN = 0;
        static final int SEARCH = 1;
        static final int DATABASE = 2;
    }

    public static final class Recurrences {
        public static final int DAILY = 1;
        public static final int BI_DAILY = 3;
        public static final int WEEKLY = 7;
        public static final int BI_WEEKLY = 14;
        public static final int MONTHLY = 31;
        public static final int YEARLY = 365;
        public static final int BI_YEARLY = YEARLY * 2;
    }

    public static final String[] MONTHS = {"January", "February",
            "March", "April", "May", "June", "July",
            "August", "September", "October", "November",
            "December"};

    private final ObjectId _id;
    private final String owner_id;
    private final int type;
    private final int image;
    private final int change_image;

    private final String title;
    private final double cost;
    private final boolean trial;
    private final Date renewal;
    private final int recurrance;
    private final double change;



    public Subscription(int image, String title, boolean trial, Date renewal, int recurrance, double cost, double change) {
        this.type = Types.MAIN;
        this._id = new ObjectId();
        this.owner_id = "";
        this.image = image;

        if (title.length() > 18)
            Log.e("SubMerge", "Message length is too long!");

        this.title = title;
        this.trial = trial;
        this.renewal = renewal;
        this.recurrance = recurrance;
        this.cost = cost;
        this.change = change;

        if (this.change == 0.00)
            this.change_image = R.drawable.cost_equal;
        else if (this.change > 0.00)
            this.change_image = R.drawable.cost_up;
        else
            this.change_image = R.drawable.cost_down;
    }

    public Subscription(int image, String title, double cost) {
        this.type = Types.SEARCH;
        this._id = new ObjectId();
        this.owner_id = "";
        this.image = image;
        this.title = title;
        this.trial = false;
        this.renewal = null;
        this.recurrance = -1;
        this.cost = cost;
        this.change = 0.00;

        if (this.change == 0.00)
            this.change_image = R.drawable.cost_equal;
        else if (this.change > 0.00)
            this.change_image = R.drawable.cost_up;
        else
            this.change_image = R.drawable.cost_down;
    }

    public Subscription(ObjectId _id, String owner_id, int image, String title, boolean trial, long renewal, int recurrance, double cost, double change) {
        this.type = Types.DATABASE;
        this._id = _id;
        this.owner_id = owner_id;
        this.image = image;
        this.title = title;
        this.trial = trial;
        this.renewal = new Date(renewal);
        this.recurrance = recurrance;
        this.cost = cost;
        this.change = change;

        if (this.change == 0.00)
            this.change_image = R.drawable.cost_equal;
        else if (this.change > 0.00)
            this.change_image = R.drawable.cost_up;
        else
            this.change_image = R.drawable.cost_down;
    }

    /*
    Android View getter methods
     */
    public int getType() {
        return this.type;
    }

    public int getImage() {
        return this.image;
    }

    public int getChangeImage() {
        return this.change_image;
    }

    public String getTitle() {
        return this.title;
    }

    public String getRenewal() {
        return renewalFromDate(this.renewal);
    }

    public String getRecurrence() {
        return recurrenceFromInt(this.recurrance);
    }

    public String getMessage() {
        String message = "";
        if (this.trial)
            message += "Expires ";
        else
            message += "Renews ";

        Date current = new Date();
        int days_until;
        if (this.renewal.after(current)) {
            days_until = (int)((this.renewal.getTime() - current.getTime()) / (1000 * 60 * 60 * 24)) + 1;
        } else {
            int days_since = (int)((current.getTime() - this.renewal.getTime()) / (1000 * 60 * 60 * 24)) % this.recurrance;
            if (days_since != 0) {
                days_until = (days_since + 1) + this.recurrance;
            } else days_until = days_since;
        }

        if (days_until == 0)
            message += "Today";
        else {
            message += String.format(Locale.ENGLISH, "in %d days", days_until);
        }
        return message;
    }

    public String getCost() {
        return String.format(Locale.ENGLISH, "$%.2f", this.cost);
    }

    public String getChange() {
        return String.format(Locale.ENGLISH, "$%.2f", Math.abs(this.change));
    }

    /*
    Database getter methods
     */
    public ObjectId accessId() {
        return this._id;
    }

    public String accessOwnerId() {
        return this.owner_id;
    }

    public int accessImage() {
        return this.image;
    }

    public String accessTitle() {
        return this.title;
    }

    public boolean accessTrial() {
        return this.trial;
    }

    public long accessRenewal() {
        return this.renewal.getTime();
    }

    public int accessRecurrance() {
        return this.recurrance;
    }

    public double accessCost() {
        return this.cost;
    }

    public double accessChange() {
        return this.change;
    }

}
