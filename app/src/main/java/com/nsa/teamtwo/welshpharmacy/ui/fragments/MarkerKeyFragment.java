package com.nsa.teamtwo.welshpharmacy.ui.fragments;

import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.nsa.teamtwo.welshpharmacy.R;
import com.nsa.teamtwo.welshpharmacy.ui.activities.NavigationActivity;
import com.nsa.teamtwo.welshpharmacy.ui.activities.SetupActivity;

public class MarkerKeyFragment extends Fragment {
    private LatLng location;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        location = ((SetupActivity) getActivity()).getLocation();
        return inflater.inflate(R.layout.fragment_key, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final FloatingActionButton nextButton = getActivity().findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NavigationActivity.class);
                intent.putExtra("location", location);
                ActivityOptions options =
                        ActivityOptions.makeCustomAnimation(getActivity(), R.anim.fade_in, R.anim.fade_out);
                startActivity(intent, options.toBundle());
                getActivity().finish();
            }
        });
    }
}
