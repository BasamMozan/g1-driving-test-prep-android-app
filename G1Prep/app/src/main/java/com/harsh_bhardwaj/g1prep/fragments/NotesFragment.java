package com.harsh_bhardwaj.g1prep.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.harsh_bhardwaj.g1prep.models.QuestionModel;
import com.harsh_bhardwaj.g1prep.R;
import com.harsh_bhardwaj.g1prep.adapters.DatabaseAdapter;


public class NotesFragment extends Fragment {

    public NotesFragment() {
    }

    public static NotesFragment newInstance(QuestionModel questionModel) {
        NotesFragment fragment = new NotesFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("questionModel", questionModel);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View row = inflater.inflate(R.layout.fragment_notes, container, false);
        Bundle bundle = getArguments();
        final DatabaseAdapter databaseAdapter = new DatabaseAdapter(getContext());
        final QuestionModel questionModel = bundle.getParcelable("questionModel");
        final EditText notesEditText = row.findViewById(R.id.notesEditText);
        String notes = questionModel.getNotes();
        if (TextUtils.isEmpty(notes)) {
            notesEditText.setHint("Type notes here");
        } else {
            notesEditText.setText(notes);
        }
        notesEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String newText = editable.toString();
                databaseAdapter.updateNotes(questionModel.getId(), newText);
            }
        });
        return row;
    }

}
