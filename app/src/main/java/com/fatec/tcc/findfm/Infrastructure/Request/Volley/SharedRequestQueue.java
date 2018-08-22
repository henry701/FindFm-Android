package com.fatec.tcc.findfm.Infrastructure.Request.Volley;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

public class SharedRequestQueue
{
    private static RequestQueue requestQueue;

    private static RequestQueue getRequestQueue(Context context) {
        if(requestQueue == null) {
            // Instantiate the cache
            Cache cache = new DiskBasedCache(context.getCacheDir(), (1024 * 1024) * 50); // 50MB capped cache
            // Set up the network to use HttpURLConnection as the HTTP client.
            Network network = new BasicNetwork(new HurlStack());
            // Create the Request Queue
            requestQueue = new RequestQueue(cache, network);
            requestQueue.start();
        }
        return requestQueue;
    }

    public static <T> void addToRequestQueue(Context context, Request<T> req) {
        getRequestQueue(context).add(req);
    }
}
