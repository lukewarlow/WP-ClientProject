package com.nsa.teamtwo.welshpharmacy.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nsa.teamtwo.welshpharmacy.R;

/**
 * Created by c1716791 on 25/04/2018.
 */

public class HelpFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View loadView = inflater.inflate(R.layout.fragment_help, container, false);

        return loadView;
    }
}
