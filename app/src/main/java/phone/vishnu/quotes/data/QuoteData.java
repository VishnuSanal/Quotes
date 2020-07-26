package phone.vishnu.quotes.data;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import phone.vishnu.quotes.controller.AppController;
import phone.vishnu.quotes.model.Quote;

public class QuoteData {
    private final ArrayList<Quote> quoteArrayList = new ArrayList<>();

    public void getQuotes(final QuoteListAsyncResponse callBack) {

        final String url = "https://raw.githubusercontent.com/VishnuSanal/Quotes/master/Quotes.json";

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                        Request.Method.GET,
                        url,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                for (int i = 0; i < response.length(); i++) {
                                    try {
                                        JSONObject quoteObject = response.getJSONObject(i);

                                        Quote quote = new Quote();
                                        quote.setQuote(quoteObject.getString("quote"));
                                        quote.setAuthor(quoteObject.getString("name"));
                                        quoteArrayList.add(quote);
//                                        Log.e("vishnu", quote.getQuote() + "  -  " + quote.getAuthor());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (null != callBack) {
                                    callBack.processFinished(quoteArrayList);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                            }
                        }
                );
                AppController.getInstance().addToRequestQueue(jsonArrayRequest);
            }
        });
        thread.start();
    }
}
