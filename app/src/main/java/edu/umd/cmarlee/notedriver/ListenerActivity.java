package edu.umd.cmarlee.notedriver;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;
import com.hound.android.sdk.VoiceSearch;
import com.hound.android.sdk.VoiceSearchInfo;
import com.hound.android.sdk.VoiceSearchListener;
import com.hound.android.sdk.VoiceSearchState;
import com.hound.android.sdk.audio.SimpleAudioByteStreamSource;
import com.hound.android.sdk.util.HoundRequestInfoFactory;
import com.hound.core.model.sdk.HoundRequestInfo;
import com.hound.core.model.sdk.HoundResponse;
import com.hound.core.model.sdk.PartialTranscript;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class ListenerActivity extends Activity {

    private VoiceSearch voiceSearch;
    private JsonNode lastConversationState;
    private TextView temp;
    private LocationManager locationManager;
    final String TAG = "NoteDriver";
    private boolean listening = false;
    private String command = "";
    private String note = "";
    private TextToSpeech speech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listener_view);
        Log.i("ListenerActivity", getApplicationContext().getFilesDir().toString());
        File file = new File(getApplicationContext().getFilesDir(), Constants.FILE_NAME);
        if (!(file.exists())) {
            try {
                file.createNewFile();
            } catch (IOException e) {
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
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        speech=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    speech.setLanguage(Locale.UK);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (voiceSearch != null) {
            voiceSearch.abort();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (voiceSearch == null) {

            Log.e(TAG, "listening");
            startSearch();
        }
        // Else stop the current search
        else {
            // voice search has already started.
            if (voiceSearch.getState() == VoiceSearchState.STATE_STARTED) {
                voiceSearch.stopRecording();
            } else {
                voiceSearch.abort();
            }

        }
    }

    private HoundRequestInfo getHoundRequestInfo() {
        final HoundRequestInfo requestInfo = HoundRequestInfoFactory.getDefault(this);

        // Client App is responsible for providing a UserId for their users which is meaningful to the client.
        requestInfo.setUserId("User ID");
        // Each request must provide a unique request ID.
        requestInfo.setRequestId(UUID.randomUUID().toString());
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            setLocation(requestInfo, locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER));
        } catch (Exception e) {
            Log.e("TAG", "A+ try catching");
        }


        // for the first search lastConversationState will be null, this is okay.  However any future
        // searches may return us a conversation state to use.  Add it to the request info when we have one.
        requestInfo.setConversationState(lastConversationState);

        return requestInfo;
    }

    public static void setLocation(final HoundRequestInfo requestInfo, final Location location) {
        if (location != null) {
            requestInfo.setLatitude(location.getLatitude());
            requestInfo.setLongitude(location.getLongitude());
            requestInfo.setPositionHorizontalAccuracy((double) location.getAccuracy());
        }
    }

    private void startSearch() {
        if (voiceSearch != null) {
            return; // We are already searching
        }
        listening = false;
        Log.e("TAG", "startSearch()");
        voiceSearch = new VoiceSearch.Builder()
                .setRequestInfo(getHoundRequestInfo())
                .setAudioSource(new SimpleAudioByteStreamSource())
                .setClientId(Constants.CLIENT_ID)
                .setClientKey(Constants.CLIENT_KEY)
                .setListener(voiceListener)
                .build();
        voiceSearch.start();
    }

    private final VoiceSearchListener voiceListener = new VoiceSearchListener() {

        /**
         * Called every time a new partial transcription is received from the Hound server.
         * This is used for providing feedback to the user of the server's interpretation of their query.
         *
         */
        @Override
        public void onTranscriptionUpdate(final PartialTranscript transcript) {
            if (listening == false)
                note = transcript.getPartialTranscript();
            else if (listening == true) {
                //note = transcript.getPartialTranscript().replaceFirst(command+" ", "");
                note = transcript.getPartialTranscript().replaceFirst(transcript.getPartialTranscript().
                        substring(0, transcript.getPartialTranscript().indexOf(command)+command.length()), "");
                Log.e("TAG NOTE", note);
            }


            if (note.contains("take notes")) {
                listening = true;
                command = "take notes";
                View view = findViewById(R.id.notes_button);
                View root = view.getRootView();
                root.setBackgroundColor(Color.parseColor("#a4c639"));
            } else if (note.contains("take note")) {
                listening = true;
                command = "take note";
                View view = findViewById(R.id.notes_button);
                View root = view.getRootView();
                root.setBackgroundColor(Color.parseColor("#a4c639"));
            } else if (note.contains("take no")) {
                listening = true;
                command = "take no";
                View view = findViewById(R.id.notes_button);
                View root = view.getRootView();
                root.setBackgroundColor(Color.parseColor("#a4c639"));
            }
        }

        @Override
        public void onResponse(final HoundResponse response, final VoiceSearchInfo info) {

            if (listening == true) {
                saveItems(note);
                speech.setSpeechRate((float) 0.66);
                speech.speak("Your note says "+note, TextToSpeech.QUEUE_FLUSH, null);
            }
            Log.e("TAG", "onResponse()");
            voiceSearch = null;
            listening = false;
            note = "";
            View view = findViewById(R.id.notes_button);
            View root = view.getRootView();
            root.setBackgroundColor(getResources().getColor(android.R.color.background_dark));
            //temp.setText("");
            startSearch();
        }

        /**
         * Called if the search fails do to some kind of error situation.
         *
         */
        @Override
        public void onError(final Exception ex, final VoiceSearchInfo info) {
            Log.e("TAG", "onError()");
            Log.e("TAG", exceptionToString(ex));
            voiceSearch = null;
        }

        /**
         * Called when the recording phase is completed.
         */
        @Override
        public void onRecordingStopped() {
            Log.e("TAG", "onRecordingStopped()");
        }

        /**
         * Called if the user aborted the search.
         *
         */
        @Override
        public void onAbort(final VoiceSearchInfo info) {
            Log.e("TAG", "onAbort()");
            voiceSearch = null;
        }
    };

    private void saveItems(String note) {
        note = note.trim();
        PrintWriter writer = null;

        try {
            File file = new File(getApplicationContext().getFilesDir(), Constants.FILE_NAME);

            FileWriter fileWriter = new FileWriter(file, true);

            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(note+"\n");
            bufferedWriter.write(Note.FORMAT.format(new Date())+"\n");
            Log.e("TAG NOTE1", note);
            bufferedWriter.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    private static String exceptionToString(final Exception ex) {
        try {
            final StringWriter sw = new StringWriter(1024);
            final PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            pw.close();
            return sw.toString();
        }
        catch (final Exception e) {
            return "";
        }
    }
}
