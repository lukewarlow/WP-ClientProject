package com.nsa.teamtwo.welshpharmacy.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nsa.teamtwo.welshpharmacy.R;
import com.nsa.teamtwo.welshpharmacy.ui.activities.SetupActivity;
import com.nsa.teamtwo.welshpharmacy.handlers.LocaleHandler;


public class WelcomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View loadView = inflater.inflate(R.layout.fragment_welcome, container, false);

        SwitchCompat languageSwitch = loadView.findViewById(R.id.language_switch);
        String locale = LocaleHandler.getLanguage(getActivity());
        if (locale.equals("cy")) {
            languageSwitch.setChecked(true);
        }

        languageSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String locale = LocaleHandler.getLanguage(getActivity());
                if (locale.equals("en")) {
                    setNewLocale("cy");
                } else {
                    setNewLocale("en");
                }
            }
        });
        return loadView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final FloatingActionButton nextButton = getActivity().findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetupActivity setupActivity = (SetupActivity) getActivity();
                setupActivity.incrementPageNumber();
            }
        });
    }

    private void setNewLocale(String language) {
        LocaleHandler.setNewLocale(getActivity(), language);
        getActivity().recreate();
    }
}
