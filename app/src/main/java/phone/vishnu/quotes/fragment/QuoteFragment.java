package phone.vishnu.quotes.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.model.Quote;

import static android.content.Context.MODE_PRIVATE;

public class QuoteFragment extends Fragment {

    private static final String PREFERENCE_NAME = "favPreference";
    ImageView shareIcon, favIcon;
    TextView quoteText, authorText;

    public QuoteFragment() {
    }

    public static final QuoteFragment newInstance(String quote, String author) {

        QuoteFragment fragment = new QuoteFragment();

        Bundle bundle = new Bundle();
        bundle.putString("quote", quote);
        bundle.putString("author", author);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View quoteView = inflater.inflate(R.layout.fragment_quote, container, false);

        quoteText = quoteView.findViewById(R.id.quoteTextView);
        authorText = quoteView.findViewById(R.id.authorTextView);
        shareIcon = quoteView.findViewById(R.id.shareImageView);
        favIcon = quoteView.findViewById(R.id.favoriteImageView);

        final String quote = getArguments().getString("quote");
        String author = getArguments().getString("author");

        quoteText.setText(quote);
        authorText.setText("-" + author);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    SharedPreferences sharedPref = getContext().getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE);
                    Gson gson = new Gson();
                    String jsonSaved = sharedPref.getString(PREFERENCE_NAME, "");
                    Type type = new TypeToken<ArrayList<Quote>>() {
                    }.getType();
                    ArrayList<Quote> productFromShared = gson.fromJson(jsonSaved, type);
                    for (Quote tempQuote : productFromShared) {
                        if (Objects.equals(quote, tempQuote.getQuote()))
                            favIcon.setColorFilter(Color.RED);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return quoteView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, quoteText.getText().toString() + "\n" + authorText.getText().toString());
                startActivity(Intent.createChooser(intent, "Share Using"));
            }
        });

        favIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.animate);
                favIcon.startAnimation(shake);
                favIcon.setColorFilter(Color.RED);

                SharedPreferences sharedPref = getContext().getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                Gson gson = new Gson();
                String jsonSaved = sharedPref.getString(PREFERENCE_NAME, "");
                String jsonNewProductToAdd = gson.toJson(new Quote(quoteText.getText().toString(), authorText.getText().toString()));
                JSONArray jsonArrayProduct = new JSONArray();

                Type type = new TypeToken<ArrayList<Quote>>() {
                }.getType();
                ArrayList<Quote> productFromShared = gson.fromJson(jsonSaved, type);

                addFavorite(jsonSaved, jsonArrayProduct, jsonNewProductToAdd, productFromShared);
                editor.putString(PREFERENCE_NAME, String.valueOf(jsonArrayProduct));
                editor.apply();
            }

            private void addFavorite(String jsonSaved, JSONArray jsonArrayProduct, String jsonNewProductToAdd, ArrayList<Quote> productFromShared) {

                try {
                    if (jsonSaved.length() != 0 && checkPresence(productFromShared)) {
                        jsonArrayProduct = new JSONArray(jsonSaved);
                    }
                    jsonArrayProduct.put(new JSONObject(jsonNewProductToAdd));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            private boolean checkPresence(ArrayList<Quote> productFromShared) {
                boolean isPresent = false;
                for (int i = 0; i < productFromShared.size(); i++) {
                    if (productFromShared.get(i).getQuote().trim().toLowerCase().equals(quoteText.getText().toString().trim().toLowerCase())) {
                        isPresent = true;
                    }
                }
                return isPresent;
            }

        });
    }
}
//TODO:Remove Color