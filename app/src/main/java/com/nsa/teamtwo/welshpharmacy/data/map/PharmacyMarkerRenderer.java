package com.nsa.teamtwo.welshpharmacy.data.map;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.nsa.teamtwo.welshpharmacy.data.map.PharmacyMarker;

//Adapted from https://developers.google.com/maps/documentation/android-api/utility/marker-clustering
//Accessed: 16/04/2018
public class PharmacyMarkerRenderer extends DefaultClusterRenderer<PharmacyMarker> {
    public PharmacyMarkerRenderer(Context context, GoogleMap map, ClusterManager<PharmacyMarker> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(PharmacyMarker item, MarkerOptions markerOptions) {
        markerOptions.icon(item.getIcon());
    }

    @Override
    protected void onClusterItemRendered(PharmacyMarker clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
        marker.setTag(clusterItem.getPharmacy());
    }
}
