package edu.umd.cmarlee.notedriver;

import android.content.Intent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by mbrown on 4/27/2016.
 */
public class Note {

    public final static String TEXT = "text";
    public final static String SUBJECT = "subject";
    public final static String DATE = "date";

    public static final String ITEM_SEP = System.getProperty("line.separator");
    public final static SimpleDateFormat FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.US);

    private String mText = new String();
    private Date mDate = new Date();

    Note(String text, Date date){
        this.mText = text;
        this.mDate = date;
    }

    Note(Intent intent){
        mText = intent.getStringExtra(Note.TEXT);
        try {
            mDate = Note.FORMAT.parse(intent.getStringExtra(Note.DATE));
        } catch (ParseException e) {
            mDate = new Date();
        }

    }

    public String getText(){
        return mText;
    }
    public void setText(String text){
        mText = text;
    }
    public Date getDate(){
        return mDate;
    }
    public void setDate(Date date){
        mDate = date;
    }

    public static void packageIntent(Intent intent, String text,
                                     Date date){
        intent.putExtra(Note.TEXT, text);
        intent.putExtra(Note.DATE, FORMAT.format(date));

    }

    public String toString() {
        return mText + ITEM_SEP + FORMAT.format(mDate);
    }

    public String toLog() {
        return "Text:" + mText + ITEM_SEP + "Date:" + FORMAT.format(mDate) + "\n";
    }


}
