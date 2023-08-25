/*
 * Copyright (C) 2019 - 2023 Vishnu Sanal. T
 *
 * This file is part of Quotes Status Creator.
 *
 * Quotes Status Creator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package phone.vishnu.quotes.repository;

import android.net.Uri;
import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import phone.vishnu.quotes.InputStreamVolleyRequest;
import phone.vishnu.quotes.controller.AppController;
import phone.vishnu.quotes.response.FontItemAsyncResponse;
import phone.vishnu.quotes.response.FontListAsyncResponse;

public class FontsRepository {

    public static final String fontPrefix =
            "https://github.com/VishnuSanal/Quotes/blob/master/fonts/";

    private final ArrayList<Uri> fontPathArrayList = new ArrayList<>();

    public void getFontPaths(
            final FontListAsyncResponse callBack,
            final FontItemAsyncResponse itemResponse,
            File filesDir) {

        final String url =
                "https://raw.githubusercontent.com/VishnuSanal/Quotes/master/fonts/fonts.json";

        Thread thread =
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {

                                final JsonArrayRequest jsonArrayRequest =
                                        new JsonArrayRequest(
                                                Request.Method.GET,
                                                url,
                                                response -> {
                                                    for (int i = 0; i < response.length(); i++) {
                                                        try {

                                                            Uri uri =
                                                                    Uri.parse(
                                                                            response.getString(i));
                                                            fontPathArrayList.add(uri);

                                                            downloadFont(
                                                                    uri.getLastPathSegment(),
                                                                    itemResponse,
                                                                    filesDir);
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                    if (null != callBack) {
                                                        callBack.processFinished(fontPathArrayList);
                                                    }
                                                },
                                                Throwable::printStackTrace) {
                                            @Override
                                            protected Response<JSONArray> parseNetworkResponse(
                                                    NetworkResponse response) {
                                                try {
                                                    Cache.Entry cacheEntry =
                                                            HttpHeaderParser.parseCacheHeaders(
                                                                    response);
                                                    if (cacheEntry == null) {
                                                        cacheEntry = new Cache.Entry();
                                                    }
                                                    final long cacheHitButRefreshed = 1000;
                                                    final long cacheExpired =
                                                            7 * 24 * 60 * 60 * 1000;
                                                    long now = System.currentTimeMillis();
                                                    final long softExpire =
                                                            now + cacheHitButRefreshed;
                                                    final long ttl = now + cacheExpired;
                                                    cacheEntry.data = response.data;
                                                    cacheEntry.softTtl = softExpire;
                                                    cacheEntry.ttl = ttl;
                                                    String headerValue;
                                                    headerValue = response.headers.get("Date");
                                                    if (headerValue != null) {
                                                        cacheEntry.serverDate =
                                                                HttpHeaderParser.parseDateAsEpoch(
                                                                        headerValue);
                                                    }
                                                    headerValue =
                                                            response.headers.get("Last-Modified");
                                                    if (headerValue != null) {
                                                        cacheEntry.lastModified =
                                                                HttpHeaderParser.parseDateAsEpoch(
                                                                        headerValue);
                                                    }
                                                    cacheEntry.responseHeaders = response.headers;
                                                    final String jsonString =
                                                            new String(
                                                                    response.data,
                                                                    HttpHeaderParser.parseCharset(
                                                                            response.headers));
                                                    return Response.success(
                                                            new JSONArray(jsonString), cacheEntry);
                                                } catch (UnsupportedEncodingException
                                                        | JSONException e) {
                                                    return Response.error(new ParseError(e));
                                                }
                                            }

                                            @Override
                                            protected void deliverResponse(JSONArray response) {
                                                super.deliverResponse(response);
                                            }

                                            @Override
                                            public void deliverError(VolleyError error) {
                                                super.deliverError(error);
                                            }

                                            @Override
                                            protected VolleyError parseNetworkError(
                                                    VolleyError volleyError) {
                                                return super.parseNetworkError(volleyError);
                                            }
                                        };
                                AppController.getInstance().addToRequestQueue(jsonArrayRequest);
                            }
                        });

        thread.start();
    }

    private void downloadFont(
            String fontString, FontItemAsyncResponse itemResponse, File filesDir) {

        final File f = new File(filesDir, fontString);

        File otfFile = new File(filesDir, fontString.replace(".ttf", ".otf"));

        if (f.exists() || otfFile.exists()) return;

        String fontURL = fontPrefix + fontString + "?raw=true";

        AppController.getInstance()
                .addToRequestQueue(
                        new InputStreamVolleyRequest(
                                Request.Method.GET,
                                fontURL,
                                (Response.Listener<byte[]>)
                                        response -> {
                                            try {

                                                File file = new File(f.toString());

                                                FileOutputStream fileOutputStream =
                                                        new FileOutputStream(file);

                                                fileOutputStream.write(response);
                                                fileOutputStream.flush();
                                                fileOutputStream.close();

                                                itemResponse.reportProgress();

                                            } catch (IOException | NullPointerException e) {
                                                e.printStackTrace();
                                            }
                                        },
                                Throwable::printStackTrace,
                                null));
    }
}
