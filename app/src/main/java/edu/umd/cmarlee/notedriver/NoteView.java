package edu.umd.cmarlee.notedriver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by mbrown on 4/28/2016.
 */
public class NoteView extends Activity {

    final String TAG = "NoteDriver";

    protected void OnCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_view);

        Intent intent = getIntent();

        String subject = intent.getStringExtra(Note.SUBJECT);

        Log.i(TAG, "Subject is: " + subject);

        String text = intent.getStringExtra(Note.TEXT);
        Date date;

        try {
            date = Note.FORMAT.parse(intent.getStringExtra(Note.DATE));
        } catch (ParseException e) {
            date = new Date();
        }


        TextView subjectView = (TextView) findViewById(R.id.noteSubjectView);
        TextView textView = (TextView) findViewById(R.id.noteTextView);
        TextView dateView = (TextView) findViewById(R.id.noteDateView);

        subjectView.setText(subject);
        textView.setText(text);
        dateView.setText(date.toString());
    }
    


}
