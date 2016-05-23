package com.rspn.plainnotetaker;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.rspn.plainnotetaker.database.NoteItemDataSource;


public class NoteItemContextMenu extends DialogFragment implements View.OnClickListener {

    private static NoteItemDataSource notesDataSource;
    private LinearLayout delete_ll;
    private LinearLayout share_ll;

    public NoteItemContextMenu() {
    }

    public static NoteItemContextMenu newInstance(long request) {
        NoteItemContextMenu fragment = new NoteItemContextMenu();
        Bundle args = new Bundle();
        args.putLong("noteId", request);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.note_item_context_menu, null, false);
        delete_ll = (LinearLayout) view.findViewById(R.id.delete_LinearLayout);
        share_ll = (LinearLayout) view.findViewById(R.id.share_LinearLayout);
        delete_ll.setOnClickListener(this);
        share_ll.setOnClickListener(this);
        notesDataSource = new NoteItemDataSource(getActivity());
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
                notesDataSource.deleteNoteItem(noteId);
                ((MainActivity) getActivity()).refreshDisplay();
                dismiss();
                break;
            default:
                dismiss();
        }
    }

    public String getNoteText(long noteId) {
        return notesDataSource.getNoteItemText(noteId).getText();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        notesDataSource.close();
    }
}
