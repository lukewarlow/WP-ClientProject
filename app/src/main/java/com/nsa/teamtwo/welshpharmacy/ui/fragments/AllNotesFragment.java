package com.nsa.teamtwo.welshpharmacy.ui.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nsa.teamtwo.welshpharmacy.R;
import com.nsa.teamtwo.welshpharmacy.data.notes.NotesDB;
import com.nsa.teamtwo.welshpharmacy.handlers.DialogHandler;
import com.nsa.teamtwo.welshpharmacy.ui.SwipeDetector;
import com.nsa.teamtwo.welshpharmacy.util.UtilCallback;

import java.util.ArrayList;

/**
 * Created by c1716791 on 21/04/2018.
 */


public class AllNotesFragment extends Fragment {

    private static final String TAG = "All Notes Fragment";

    private ListView listView;
    private NotesDB notesDB;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View loadView = inflater.inflate(R.layout.fragment_all_notes, container, false);
        notesDB = new NotesDB(getActivity());
        final SwipeDetector swipeDetector = new SwipeDetector();
        listView = loadView.findViewById(R.id.list_notes);
        listView.setOnTouchListener(swipeDetector);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (swipeDetector.swipeDetected()) {
                    if (swipeDetector.getAction() == SwipeDetector.Action.RL) {
                        final String note = listView.getItemAtPosition(position).toString();
                        Cursor data = notesDB.getItemID(note);
                        int itemID = -1;
                        while (data.moveToNext()) {
                            itemID = data.getInt(0);
                        }
                        final int noteID = itemID;
                        DialogHandler.showConfirmationDialog(getActivity(), R.string.delete_note_dialog_msg, R.string.delete, R.string.cancel, new UtilCallback() {
                            @Override
                            public void onSuccess(Object... o) {
                                // User clicked the "Delete" button, so delete the reminder.
                                notesDB.deleteNote(noteID, note);
                                populateListView(getView());
                            }


                            @Override
                            public void onFail(Object... o) {
                            }
                        });
                    }
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String note = listView.getItemAtPosition(position).toString();
                Cursor data = notesDB.getItemID(note);
                int itemID = -1;
                while (data.moveToNext()) {
                    itemID = data.getInt(0);
                }
                NotesFragment noteFragment = new NotesFragment().setOriginalNote(note).setNoteID(itemID);
                noteFragment.setTargetFragment(AllNotesFragment.this, 0);
                noteFragment.show(getActivity().getFragmentManager(), "note fragment");
                return false;
            }
        });

        populateListView(loadView);

        FloatingActionButton fab = loadView.findViewById(R.id.fab_notes);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotesFragment noteFragment = new NotesFragment();
                noteFragment.setTargetFragment(AllNotesFragment.this, 0);
                noteFragment.show(getActivity().getFragmentManager(), "note fragment");
            }
        });
        return loadView;
    }

    @Override
    public void onResume() {
        super.onResume();
        populateListView(getView());
    }

    public void populateListView(View view) {
        Log.d(TAG, "populateListView: Display data in the ListView");

        NotesDB databaseHelper = new NotesDB(getActivity());
        // collecting the data and sending it to a list
        Cursor data = databaseHelper.getData();
        ArrayList<String> listData = new ArrayList<>();
        while (data.moveToNext()) {
            // collect value from column 1 of the database
            // then add it to the ArrayList
            listData.add(data.getString(1));
        }

        TextView noNotesText = view.findViewById(R.id.text_no_notes);
        if (listData.size() > 0) {
            noNotesText.setVisibility(View.INVISIBLE);
        } else {
            noNotesText.setVisibility(View.VISIBLE);
        }
        // creating a list adapter to and set the adapter
        ListAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listData);
        listView.setAdapter(adapter);
    }
}
