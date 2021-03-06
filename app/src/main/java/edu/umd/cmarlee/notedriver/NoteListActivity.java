package edu.umd.cmarlee.notedriver;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Date;


public class NoteListActivity extends ListActivity {

    private static final int ADD_NOTE_REQUEST = 0;
    private static final String FILE_NAME = "NotesList.txt";
    private static final String TAG = "NoteDriver";

    private static final int ADD_REQUEST_CODE = 1;

    // IDs for menu items
    private static final int MENU_DELETE = Menu.FIRST;
    private static final int MENU_DUMP = Menu.FIRST + 1;

    NoteAdapter mAdapter;
    private ListView lv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lv = getListView();

        mAdapter = new NoteAdapter(getApplicationContext());

        getListView().setAdapter(mAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position , long id) {

                Note note = (Note) adapter.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), NoteView.class);
                intent.putExtra("pos" , position);
                Note.packageIntent(intent,note.getText(),note.getDate());
                startActivityForResult(intent, ADD_NOTE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(TAG, "Entered onActivityResult()");

        switch (resultCode) {
            case RESULT_CANCELED:
                Log.i(TAG, "Returned after canceling");
                break;
            case RESULT_OK:
                int position = data.getIntExtra("pos" , 0);
                Note note = (Note) mAdapter.getItem(position);
                note.setText(data.getStringExtra(Note.TEXT));
                getListView().setAdapter(mAdapter);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter.getCount() == 0)
            loadItems();
    }

    @Override
    protected void onPause() {
        super.onPause();

        saveItems();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(Menu.NONE, MENU_DELETE, Menu.NONE, "Delete all");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_DELETE:
                mAdapter.clear();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void loadItems() {
        BufferedReader reader = null;
        try {
            FileInputStream fis = openFileInput(FILE_NAME);
            reader = new BufferedReader(new InputStreamReader(fis));
            String text = null;
            Date date = null;

            while (null != (text = reader.readLine())) {
                Log.i(TAG, "Text is :" + text);
                date = Note.FORMAT.parse(reader.readLine());
                Note note = new Note(text,date);
                Log.i(TAG, "Date is :" + date);

                mAdapter.add(note);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveItems() {
        PrintWriter writer = null;
        try {
            FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    fos)));
            for (int idx = 0; idx < mAdapter.getCount(); idx++) {

                writer.println(mAdapter.getItem(idx));

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != writer) {
                writer.close();
            }
        }
    }
}