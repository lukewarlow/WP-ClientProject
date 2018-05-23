package com.nsa.teamtwo.welshpharmacy.handlers.network;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.nsa.teamtwo.welshpharmacy.R;
import com.nsa.teamtwo.welshpharmacy.util.UtilCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class NetworkHandler {
    private static final String TAG = "Network Handler";

    private static String hasInternet;

    public static void getServices(final Activity activity, final VolleyCallback callback) {
        getServices(activity, callback, false);
    }

    private static void getServices(final Activity activity, final VolleyCallback callback, boolean skipConnection) {
        final Context context = activity.getApplicationContext();
        //Adapted from https://developer.android.com/training/volley/requestqueue.html#java
        RequestQueue queue = VolleySingleton.getInstance(context.getApplicationContext()).getRequestQueue();

        //Adapted from https://developer.android.com/training/volley/index.html
        String url = context.getString(R.string.serverUrl) + "/services";
//        String url = "https://pastebin.com/raw/ufRPehSc";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (!(error instanceof TimeoutError || error instanceof NoConnectionError)) {
                            Log.e(TAG, "Network request error!");
                            error.printStackTrace();
                        }

                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null) {
                            Log.e(TAG, "Status code: " + String.valueOf(networkResponse.statusCode));
                        }
                        hasInternet = "false";
                        getServices(activity, callback, true);
                    }
                });

        //https://www.captechconsulting.com/blogs/android-volley-library-tutorial
        Cache.Entry cache = queue.getCache().get(url);

        if (hasInternet(context) && !skipConnection) {
            Log.d(TAG, "Requesting services.");
            VolleySingleton.getInstance(context).addToRequestQueue(request);
        } else {
            if (cache != null) {
                try {
                    Log.d(TAG, "No network, using cached services.");
                    callback.onSuccess(new JSONObject(new String(cache.data)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, "No Internet and no cache!");
                noInternetAndNoCacheDialog(activity, new UtilCallback() {
                    @Override
                    public void onSuccess(Object... obj) {
                        getServices(activity, callback);
                    }

                    @Override
                    public void onFail(Object... obj) {
                        activity.finish();
                        System.exit(0);
                    }
                });
            }
        }
    }

    public static void getPharmacies(final Activity activity, final VolleyCallback callback) {
        getPharmacies(activity, callback, false);
    }

    private static void getPharmacies(final Activity activity, final VolleyCallback callback, boolean skipConnection) {
        final Context context = activity.getApplicationContext();
        //Adapted from https://developer.android.com/training/volley/requestqueue.html#java
        RequestQueue queue = VolleySingleton.getInstance(context.getApplicationContext()).getRequestQueue();

        //Adapted from https://developer.android.com/training/volley/index.html
        String url = context.getString(R.string.serverUrl) + "/pharmacies";
//        String url = "https://pastebin.com/raw/zpWKxsAx";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (!(error instanceof TimeoutError || error instanceof NoConnectionError)) {
                            Log.e(TAG, "Network request error!");
                            error.printStackTrace();
                        }

                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null) {
                            Log.e(TAG, "Status code: " + String.valueOf(networkResponse.statusCode));
                        }
                        hasInternet = "false";
                        getPharmacies(activity, callback, true);
                    }
                });

        //https://www.captechconsulting.com/blogs/android-volley-library-tutorial
        Cache.Entry cache = queue.getCache().get(url);

        if (hasInternet(context) && !skipConnection) {
            Log.d(TAG, "Requesting pharmacies.");
            VolleySingleton.getInstance(context).addToRequestQueue(request);
        } else {
            if (cache != null) {
                try {
                    Log.d(TAG, "No network, using cached pharmacies.");
                    callback.onSuccess(new JSONObject(new String(cache.data)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, "No Internet and no cache!");
                noInternetAndNoCacheDialog(activity, new UtilCallback() {
                    @Override
                    public void onSuccess(Object... obj) {
                        getPharmacies(activity, callback);
                    }

                    @Override
                    public void onFail(Object... obj) {
                        activity.finishAndRemoveTask();
                        System.exit(0);
                    }
                });
            }
        }
    }

    public static void noInternetAndNoCacheDialog(final Activity activity, final UtilCallback callback) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle(R.string.dialog_title_no_internet);
        dialog.setMessage(R.string.dialog_message_no_cache_internet);
        dialog.setNegativeButton(R.string.dialog_negative, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onFail();
            }
        });
        dialog.setPositiveButton(R.string.dialog_positive, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onSuccess();
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                activity.finishAndRemoveTask();
                System.exit(0);
            }
        });
        dialog.show();
    }

    private static boolean hasInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null &&
                activeNetworkInfo.isConnectedOrConnecting();
    }

    public static boolean hasInternet(Context context) {
        if (hasInternet == null) return hasInternetConnection(context);
        else return Boolean.valueOf(hasInternet);
    }
}
