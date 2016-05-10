package edu.umd.cmarlee.notedriver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by mbrown on 4/28/2016.
 */
public class NoteView extends Activity {

    final String TAG = "NoteDriver";
    private EditText textView;
    private TextView dateView;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_view);

        Intent intent = getIntent();

        String text = intent.getStringExtra(Note.TEXT);
        Log.i(TAG, "Text is: " + text);
        Date date;

        try {
            date = Note.FORMAT.parse(intent.getStringExtra(Note.DATE));
        } catch (ParseException e) {
            date = new Date();
        }

        Log.i(TAG, "Date is: " + date.toString());

        //TextView subjectView = (TextView) findViewById(R.id.note);
        textView = (EditText) findViewById(R.id.note_text_view);
        dateView = (TextView) findViewById(R.id.note_date_view);

        //subjectView.setText(subject);
        textView.setText(text);
        dateView.setText(date.toString());
        final int position = intent.getIntExtra("pos" , 0);

        final Button submitButton = (Button) findViewById(R.id.save);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date dat;
                String textChange = textView.getText().toString();
                try {
                    dat = Note.FORMAT.parse(dateView.getText().toString());
                }catch(ParseException e) {

                    dat = new Date();
                }
                Intent data = new Intent();
                data.putExtra("pos", position);
                Note.packageIntent(data, textChange, dat);

                


                NoteView.this.setResult(RESULT_OK, data);
                finish();


            }
        });




    }
    


}
