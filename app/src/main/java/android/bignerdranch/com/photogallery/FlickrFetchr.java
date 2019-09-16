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
    private static final String API_KEY = "fe43ef2cb36dc9bf8e0b472ec98c22be";

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

    public List<GalleryItem> fetchItems(Integer page) {
        List<GalleryItem> items = new ArrayList<>();

        try {
            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .appendQueryParameter("page", (page != -1) ? String.valueOf(page) : "1")
                    .build().toString();
            String jsonString = getUrlString(url);
            // Log.i(TAG, "URL :" + url);
            // Log.i(TAG, "Received JSON: " + jsonString);

            //JSONObject jsonBody = new JSONObject(jsonString);
            //parseItems2(items, jsonBody);

            // Gson
            items = parseItems(jsonString);

        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);

        }

        return items;
    }

    private List<GalleryItem> parseItems(String jsonString) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        Gallery gallery = gson.fromJson(jsonString, Gallery.class);

        return gallery.getPhotos().getPhoto();
    }

    @Deprecated
    private void parseItems2(List<GalleryItem> items, JSONObject jsonBody)
            throws IOException, JSONException {
        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");
        for (int i = 0; i < photoJsonArray.length(); i++) {
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);
            GalleryItem item = new GalleryItem();
            item.setId(photoJsonObject.getString("id"));
            item.setCaption(photoJsonObject.getString("title"));
            if (!photoJsonObject.has("url_s")) {
                continue;
            }
            item.setUrl(photoJsonObject.getString("url_s"));
            items.add(item);
        }
    }
}