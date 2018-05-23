package com.nsa.teamtwo.welshpharmacy.ui.fragments;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.nsa.teamtwo.welshpharmacy.R;
import com.nsa.teamtwo.welshpharmacy.data.notes.NotesDB;
import com.nsa.teamtwo.welshpharmacy.util.Util;

public class NotesFragment extends DialogFragment {
    private NotesDB notesDB;
    private String originalNote = "";
    private int noteID = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View loadView = inflater.inflate(R.layout.fragment_note, container);
        notesDB = new NotesDB(getActivity());
        final EditText editNote = loadView.findViewById(R.id.text_note);
        editNote.setText(originalNote);
        editNote.setSelection(originalNote.length());

        this.setCancelable(false);
        Button saveButton = loadView.findViewById(R.id.button_save);
        Button discardButton = loadView.findViewById(R.id.button_cancel);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String note = editNote.getText().toString();
                if(!note.isEmpty()){
                    if (noteID == -1) {
                        addNote(note);
                    } else {
                        editData(originalNote, note);
                    }
                } else {
                    editNote.setError(getString(R.string.must_enter_something));
                }
            }
        });

        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (originalNote != null) {
                    if (editNote.getText().toString().equals(originalNote)) {
                        dismiss();
                    } else {
                        showUnsavedChangesDialog();
                    }
                } else {
                    showUnsavedChangesDialog();
                }
            }
        });
        return loadView;
    }

    private void showUnsavedChangesDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        builder.setNegativeButton(R.string.keep_editing, null);

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        //TODO run AllNotesFragment populate list view
        AllNotesFragment allNotesFragment = (AllNotesFragment) getTargetFragment();
        allNotesFragment.populateListView(allNotesFragment.getView());
    }

    public NotesFragment setOriginalNote(String originalNote) {
        this.originalNote = originalNote;
        return this;
    }

    public NotesFragment setNoteID(int noteID) {
        this.noteID = noteID;
        return this;
    }

    public void addNote(String newEntry){
        boolean insertData = notesDB.addNote(newEntry);

        if(insertData){
            Util.shortToast(getActivity(), R.string.note_saved_successfully);
            dismiss();
        } else {
            Util.shortToast(getActivity(), R.string.something_went_wrong);
        }
    }

    public void editData(String oldNote, String editedNote){
        notesDB.updateNote(editedNote, noteID, oldNote);
        Util.shortToast(getActivity(), R.string.note_successfully_edited);
        dismiss();
    }
}
