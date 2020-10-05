package com.ozarychta.bebetter.utils;

import android.content.Context;
import android.net.ConnectivityManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ServerRequestUtil {

    private static ServerRequestUtil INSTANCE = null;
    private static boolean connectedToNetwork = false;
    private static RequestQueue requestQueue = null;
    private static Context ctx;

    private ServerRequestUtil(Context context) {
        ctx = context;
        requestQueue =  Volley.newRequestQueue(ctx.getApplicationContext());
    }

    public static synchronized ServerRequestUtil getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ServerRequestUtil(context);
        }
        return INSTANCE;
    }

    public static boolean isConnectedToNetwork(ConnectivityManager cm) {
        connectedToNetwork = cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected();

        return connectedToNetwork;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}
