/**
 * Copyright 2014 Magnus Woxblom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rspn.plainnotetaker;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rspn.plainnotetaker.data.NoteItem;
import com.rspn.plainnotetaker.data.NotesDataSource;
import com.woxthebox.draglistview.DragItemAdapter;

import java.util.ArrayList;

public class ItemAdapter extends DragItemAdapter<Pair<Long, NoteItem>, ItemAdapter.ViewHolder> {

    private static final boolean dragOnLongPress = false;
    private int mLayoutId;
    private int mGrabHandleId;
    private Activity activity;

    public ItemAdapter(ArrayList<Pair<Long, NoteItem>> list, int layoutId, int grabHandleId, Activity activity) {
        super(dragOnLongPress);
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        this.activity = activity;
        setHasStableIds(true);
        setItemList(list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        String text = mItemList.get(position).second.getText();
        holder.mText.setText(text);
        holder.itemView.setTag(text);
    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).first;
    }

    public class ViewHolder extends DragItemAdapter<Pair<Long, NoteItem>, ViewHolder>.ViewHolder {
        public TextView mText;
        private NotesDataSource notesDataSource;

        public ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId);
            mText = (TextView) itemView.findViewById(R.id.text);
            notesDataSource = new NotesDataSource(mText.getContext());
        }

        @Override
        public void onItemClicked(View view) {
            NoteItem noteItem = notesDataSource.findById(getItemId());
            Intent intent = new Intent(view.getContext(), NoteEditorActivity.class);
            intent.putExtra("key", noteItem.getKey());
            intent.putExtra("text", noteItem.getText());
            activity.startActivityForResult(intent, MainActivity.EDITOR_ACTIVITY_REQUEST);
        }

        @Override
        public boolean onItemLongClicked(View view) {
            Toast.makeText(view.getContext(), "Item long clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}
