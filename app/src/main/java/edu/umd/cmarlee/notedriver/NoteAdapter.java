package edu.umd.cmarlee.notedriver;

import android.content.Context;
import android.widget.BaseAdapter;

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

    public int getSize(){
        return mNotes.size();
    }

    public Object getItem(int pos){
        return mNotes.get(pos);

    }








}
