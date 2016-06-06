package com.rspn.plainnotetaker.uihelper;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.rspn.plainnotetaker.MainActivity;
import com.rspn.plainnotetaker.R;
import com.rspn.plainnotetaker.database.NoteDataSource;


public class NoteContextMenu extends DialogFragment implements View.OnClickListener {

    private static NoteDataSource notesDataSource;

    public NoteContextMenu() {
    }

    public static NoteContextMenu newInstance(long request) {
        NoteContextMenu fragment = new NoteContextMenu();
        Bundle args = new Bundle();
        args.putLong("noteId", request);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.note_item_context_menu, null, false);
        LinearLayout delete_ll = (LinearLayout) view.findViewById(R.id.delete_LinearLayout);
        LinearLayout share_ll = (LinearLayout) view.findViewById(R.id.share_LinearLayout);
        delete_ll.setOnClickListener(this);
        share_ll.setOnClickListener(this);
        notesDataSource = new NoteDataSource(getActivity());
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onClick(View view) {
        notesDataSource.open();
        int id = view.getId();
        long noteId = getArguments().getLong("noteId");

        switch (id) {
            case R.id.share_LinearLayout:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getNoteText(noteId));
                startActivity(intent);
                dismiss();
                break;

            case R.id.delete_LinearLayout:
                notesDataSource.deleteNote(noteId);
                ((MainActivity) getActivity()).refreshDisplay();
                dismiss();
                break;
            default:
                dismiss();
        }
    }

    private String getNoteText(long noteId) {
        return notesDataSource.getNoteById(noteId).getText();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        notesDataSource.close();
    }

    /*Work around to dismiss the dialog when clicking outside of its boundaries.
     Otherwise it will only put it in the back of the stack*/
    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        notesDataSource.close();
        dismiss();
    }
}
