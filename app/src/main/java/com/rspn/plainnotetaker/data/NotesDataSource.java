package com.rspn.plainnotetaker.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import android.content.Context;
import android.content.SharedPreferences;

public class NotesDataSource {

	private static final String PREFKEY = "notes";
	private SharedPreferences notePrefs;
	
	public NotesDataSource(Context context) {
		notePrefs = context.getSharedPreferences(PREFKEY, Context.MODE_PRIVATE);
	}
	
	public List<NoteItem> findAll() {
		
		Map<String, ?> notesMap = notePrefs.getAll();
		
		SortedSet<String> keys = new TreeSet<>(notesMap.keySet());
		
		List<NoteItem> noteList = new ArrayList<>();
		for (String key : keys) {
			NoteItem note = new NoteItem();
			note.setKey(Long.valueOf(key));
			note.setText((String) notesMap.get(key));
			noteList.add(note);
		}
		
		return noteList;
	}

	public boolean isEmpty() {
		return notePrefs.getAll().isEmpty();
	}
	
	public boolean update(NoteItem note) {
		
		SharedPreferences.Editor editor = notePrefs.edit();
		editor.putString(Long.toString(note.getKey()), note.getText());
		editor.commit();
		return true;
	}
	
	public boolean remove(NoteItem note) {
		String key = Long.toString(note.getKey());
		if (notePrefs.contains(key)) {
			SharedPreferences.Editor editor = notePrefs.edit();
			editor.remove(key);
			editor.commit();
		}
		return true;
	}

}
