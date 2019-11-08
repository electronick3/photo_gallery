package android.bignerdranch.com.photogallery;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FlickrFetchr {

    /* *
     * 	  Photo galery
     *
     *    Key:
     *    fe43ef2cb36dc9bf8e0b472ec98c22be
     *
     *    Secret:
     *    3f2a14b8ec56e8fa
     * */

    private static final String TAG = "FlickrFetchr";
    private static final String TAG_TEST = "TestURL";

    private static final String API_KEY = "fe43ef2cb36dc9bf8e0b472ec98c22be";

    private static final String FETCH_RECENTS_METHOD = "flickr.photos.getRecent";
    private static final String SEARCH_METHOD = "flickr.photos.search";

    private static final boolean ONLY_A_TEST = true;


    private static final Uri ENDPOINT = Uri
            .parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_s")
            .build();


    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    private List<GalleryItem> downloadGalleryItems(String url) {
        List<GalleryItem> items = new ArrayList<>();

        try {
            String jsonString = getUrlString(url);
            // Log.i(TAG, "Received JSON: " + jsonString);
            Log.i(TAG, "URL :" + url);

            //JSONObject jsonBody = new JSONObject(jsonString);
            //parseItems2(items, jsonBody);

            // Gson
            items = parseItems(jsonString);

        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }

        return items;
    }

    public List<GalleryItem> fetchRecentPhotos(String page) {
        String url = "";

        if (!ONLY_A_TEST) {
            url = buildUrl(FETCH_RECENTS_METHOD, null, page);
        }
        else {
            url = buildTestUrl();
        }

        return downloadGalleryItems(url);
    }

    public List<GalleryItem> searchPhotos(String query, String page) {
        String url = "";

        if (!ONLY_A_TEST) {
            url = buildUrl(SEARCH_METHOD, query, page);
        }
        else {
            url = buildTestUrl();
        }

        return downloadGalleryItems(url);
    }

    private String buildUrl(String method, String query, String page) {
        Uri.Builder uriBuilder = ENDPOINT.buildUpon()
                .appendQueryParameter("method", method);
        if (query != null) { uriBuilder.appendQueryParameter("text", query);}
        if (page == null) {uriBuilder.appendQueryParameter("page", "1");}
        else {uriBuilder.appendQueryParameter("page", page);}

        return uriBuilder.build().toString();
    }

    private String buildTestUrl() {
        Uri.Builder uriBuilder = ENDPOINT.buildUpon().
                appendQueryParameter("method", SEARCH_METHOD);
        uriBuilder.appendQueryParameter("text", "Busness");
        uriBuilder.appendQueryParameter("page", "1");

        String rr = uriBuilder.build().toString();

        Log.i(TAG_TEST, "Test URL = " + rr);
        return rr;
    }

    private List<GalleryItem> parseItems(String jsonString) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        Gallery gallery = gson.fromJson(jsonString, Gallery.class);

        return gallery.getPhotos().getPhoto();
    }
}