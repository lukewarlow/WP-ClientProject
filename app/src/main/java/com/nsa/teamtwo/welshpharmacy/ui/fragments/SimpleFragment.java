package com.nsa.teamtwo.welshpharmacy.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SimpleFragment extends Fragment {

    private int layoutID;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(layoutID, container,false);
    }

    public Fragment setLayout(int layoutID) {
        this.layoutID = layoutID;
        return this;
    }
}
