package com.nsa.teamtwo.welshpharmacy.data.pharmacy;

import android.util.Log;

import com.nsa.teamtwo.welshpharmacy.data.DayOfTheWeek;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParser {
    private static final String TAG = "JSON Parser";

    public static List<Service> parseServiceData(JSONObject json) throws IllegalArgumentException {
        List<Service> services = new ArrayList<>();
        if (json == null) {
            Log.d(TAG, "Json is null");
            throw new IllegalArgumentException();
        }
        try {
            for (int i = 0; i < json.length(); i++) {
                JSONObject serviceJSON = json.getJSONObject("service" + i);
                String name = serviceJSON.getString("name");
                String welshName = serviceJSON.getString("welshName");
                String description = serviceJSON.getString("description");
                String welshDescription = serviceJSON.getString("welshDescription");
                Service service = new Service(name, welshName, description, welshDescription);
                services.add(service);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return services;
    }

    public static List<Pharmacy> parsePharmacyData(JSONObject json, List<Service> services) throws IllegalArgumentException {
        List<Pharmacy> pharmacies = new ArrayList<>();
        if (json == null) {
            Log.d(TAG, "JSON is null");
            throw new IllegalArgumentException();
        }
        try {
            for (int i = 0; i < json.length(); i++) {
                JSONObject pharmacyJson = json.getJSONObject("pharmacy" + i);
                String name = pharmacyJson.getString("name");
                double lat = (double) pharmacyJson.get("lat");
                double lng = (double) pharmacyJson.get("long");
                String phoneNumber = pharmacyJson.getString("phoneNumber");
                HashMap<DayOfTheWeek, OpeningTime> openingTimes = new HashMap<>();
                JSONArray openingTimesJSON = pharmacyJson.getJSONArray("openingTimes");
                for (int j = 0; j < openingTimesJSON.length(); j++) {
                    double openTime = ((JSONObject) openingTimesJSON.get(j)).getDouble("open");
                    double closeTime = ((JSONObject) openingTimesJSON.get(j)).getDouble("close");
                    OpeningTime openingTime = new OpeningTime(openTime, closeTime);
                    openingTimes.put(DayOfTheWeek.values()[j], openingTime);
                }
                Boolean welshAvailable = pharmacyJson.getBoolean("welshAvailable");

                Pharmacy pharmacy = new Pharmacy(name, lat, lng, openingTimes, phoneNumber, welshAvailable);
                String encodedServiceString = pharmacyJson.getString("services");
                String[] encodedServices = encodedServiceString.split(",");
                for (String stringService : encodedServices) {
                    String serviceName = stringService.replaceAll("_", " ").toLowerCase();
                    for (Service service : services) {
                        if (service.equals(serviceName)) {
                            pharmacy.addService(service);
                            break;
                        }
                    }
                }
                if (pharmacy.getServices().size() > 0) {
                    pharmacies.add(pharmacy);
                } else {
                    Log.d(TAG, "Pharmacy data has invalid services in.");
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pharmacies;
    }
}
