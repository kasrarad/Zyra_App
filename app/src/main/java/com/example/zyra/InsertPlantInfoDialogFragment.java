package com.example.zyra;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class InsertPlantInfoDialogFragment extends DialogFragment {

    protected EditText editTextPlantName;
    protected Button btnSaveChanges;
    protected Button btnDeletePlant;
    protected Spinner mySpinner;

    private static final String TAG = "InsertPlantInfoDialogFr";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Started");

        View view = inflater.inflate(R.layout.fragment_plant_info, container, false);

        editTextPlantName = view.findViewById(R.id.editTextOldPlantName);
        btnSaveChanges = view.findViewById(R.id.btnSave);
        btnDeletePlant = view.findViewById(R.id.btnDelete);

        btnDeletePlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Delete");
            }
        });

        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Saved");
            }
        });

        //setting the same spinner from New Plant Activity
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.plants));

        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        return view;
    }


}
