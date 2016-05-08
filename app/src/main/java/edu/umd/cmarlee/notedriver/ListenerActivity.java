package edu.umd.cmarlee.notedriver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class ListenerActivity extends Activity {
//comment
    private static final String FILE_NAME = "NotesList.txt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listener_view);
        File file = new File(getApplicationContext().getFilesDir(), FILE_NAME);
        if (!(file.exists())){
            try {
                file.createNewFile();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }

        Button notes_button = (Button) findViewById(R.id.notes_button);
        notes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NoteListActivity.class);
                startActivity(intent);
            }
        });

        Button add_note = (Button) findViewById(R.id.add_note_button);
        add_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                try {
                    File file = new File(getApplicationContext().getFilesDir(), FILE_NAME);

                    FileWriter fileWriter = new FileWriter(file, true);

                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write("This is a subject\n");
                    bufferedWriter.write("This is example text\n");
                    bufferedWriter.write(Note.FORMAT.format(new Date())+"\n");
                    bufferedWriter.close();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        });

        Button delete_button = (Button) findViewById(R.id.delete_list_button);
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    File file = new File(getApplicationContext().getFilesDir(), FILE_NAME);

                    FileWriter fileWriter = new FileWriter(file);
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
