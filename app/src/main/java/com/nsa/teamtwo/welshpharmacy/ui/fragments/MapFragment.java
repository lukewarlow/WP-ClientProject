package com.nsa.teamtwo.welshpharmacy.ui.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.maps.android.clustering.ClusterManager;
import com.nsa.teamtwo.welshpharmacy.R;
import com.nsa.teamtwo.welshpharmacy.data.FilterCallback;
import com.nsa.teamtwo.welshpharmacy.data.map.PharmacyMarker;
import com.nsa.teamtwo.welshpharmacy.data.pharmacy.Pharmacy;
import com.nsa.teamtwo.welshpharmacy.data.pharmacy.Service;
import com.nsa.teamtwo.welshpharmacy.handlers.MapHandler;
import com.nsa.teamtwo.welshpharmacy.util.KeyValueHelper;
import com.nsa.teamtwo.welshpharmacy.util.Util;

import java.util.List;

public class MapFragment extends Fragment {

    private MapHandler mapHandler;
    private FilterFragment selectorFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mapHandler = new MapHandler(getActivity());
        getFragmentManager().beginTransaction().add(R.id.content_frame, mapHandler.getMapFragment()).commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_filter, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.filter) {
            selectService(getView());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("unchecked")
    public void selectService(View v) {
        if (selectorFragment == null) {
            selectorFragment = new FilterFragment();
            selectorFragment.setCallback(new FilterCallback() {
                @Override
                public void onFilterSelected(boolean welshAvailableOnly, boolean openOnly, List<Service> selectedServices) {
                    if (selectedServices == null) {
                        Util.shortToast(getActivity(), R.string.filters_cleared);
                    } else {
                        Util.shortToast(getActivity(), R.string.filter_applied);
                    }
                    ClusterManager clusterManager = mapHandler.getClusterManager();
                    clusterManager.clearItems();
                    if (selectedServices != null) {
                        for (PharmacyMarker marker : mapHandler.getMarkers()) {
                            Pharmacy pharmacy = marker.getPharmacy();
                            if (welshAvailableOnly) {
                                if (!pharmacy.isWelshAvailable()) {
                                    continue;
                                }
                            }
                            if (openOnly) {
                                if (!pharmacy.isOpenNow()) {
                                    continue;
                                }
                            }
                            if (selectedServices.size() > 0) {
                                if (Util.isWithinRadius(getActivity(), marker.getPharmacy().getLocation(), mapHandler.getLocation())) {
                                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(KeyValueHelper.KEY_PREFERENCES, Context.MODE_PRIVATE);
                                    String filterLogic = sharedPreferences.getString(KeyValueHelper.KEY_FILTER_LOGIC, KeyValueHelper.DEFAULT_FILTER_LOGIC);
                                    if (filterLogic.equals("OR")) {
                                        for (Service service : selectedServices) {
                                            if (pharmacy.doesService(service)) {
                                                clusterManager.addItem(marker);
                                            }
                                            break;
                                        }
                                    } else {
                                        boolean needsRemoving = false;
                                        for (Service service : selectedServices) {
                                            if (!pharmacy.doesService(service)) {
                                                needsRemoving = true;
                                                break;
                                            }
                                        }
                                        if (!needsRemoving) clusterManager.addItem(marker);
                                    }
                                }
                            } else if (Util.isWithinRadius(getActivity(), marker.getPharmacy().getLocation(), mapHandler.getLocation())) {
                                clusterManager.addItem(marker);
                            }
                        }
                    } else {
                        for (PharmacyMarker marker : mapHandler.getMarkers()) {
                            if (Util.isWithinRadius(getActivity(), marker.getPharmacy().getLocation(), mapHandler.getLocation())) {
                                clusterManager.addItem(marker);
                            }
                        }
                    }
                    clusterManager.cluster();
                }
            });
        }
        selectorFragment.show(getFragmentManager(), "Select Service");
    }
}
