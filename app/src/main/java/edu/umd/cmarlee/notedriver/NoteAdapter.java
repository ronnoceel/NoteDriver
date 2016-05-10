package edu.umd.cmarlee.notedriver;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mbrown on 4/27/2016.
 */
public class NoteAdapter extends BaseAdapter {

    private final List<Note> mNotes = new ArrayList<Note>();
    private final Context mContext;

    public NoteAdapter(Context context){
        mContext = context;
    }
    public void add(Note note){

        mNotes.add(note);
        notifyDataSetChanged();
    }

    public void clear(){
        mNotes.clear();
        notifyDataSetChanged();
    }

    public int getCount(){
        return mNotes.size();
    }

    public Object getItem(int pos){
        return mNotes.get(pos);

    }
    public long getItemId(int pos){
        return pos;
    }
    public View getView(int pos, View convertView, ViewGroup parent){
        final Note note = (Note) getItem(pos);
        RelativeLayout noteLayout = (RelativeLayout) LayoutInflater.from(mContext).inflate(
                R.layout.note_layout, null);
        final TextView note_preview= (TextView) noteLayout.findViewById(R.id.note_preview);

        note_preview.setText(note.getText());

        final TextView dateView = (TextView) noteLayout.findViewById(R.id.dateView);
        dateView.setText(Note.FORMAT.format(note.getDate()));


        return noteLayout;

    }

}
