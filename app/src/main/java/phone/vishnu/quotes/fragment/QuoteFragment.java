package phone.vishnu.quotes.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

        View quoteView = inflater.inflate(R.layout.fragment_quote, container, false);

        quoteText = quoteView.findViewById(R.id.quoteTextView);
        authorText = quoteView.findViewById(R.id.authorTextView);
        shareIcon = quoteView.findViewById(R.id.shareImageView);
        favIcon = quoteView.findViewById(R.id.favoriteImageView);

        String quote = getArguments().getString("quote");
        String author = getArguments().getString("author");

        quoteText.setText(quote);
        authorText.setText("-" + author);

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

                SharedPreferences sharedPref = getContext().getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                Gson gson = new Gson();
/*
                String json_old = sharedPref.getString(PREFERENCE_NAME, "{\"author\":\"-Babe Ruth\",\"quote\":\"Every strike brings me closer to the next home run.\"}");
                ArrayList<Quote> arrayList = gson.fromJson(json_old,new TypeToken<List<Quote>>() {}.getType() );
                arrayList.add(new Quote(quoteText.getText().toString(), authorText.getText().toString()));

//                String json = gson.toJson(new Quote(quoteText.getText().toString(), authorText.getText().toString()));
                String json = gson.toJson(arrayList);
                Log.e("vishnu",String.valueOf(json));
*/
                String jsonSaved = sharedPref.getString(PREFERENCE_NAME, "");
                String jsonNewProductToAdd = gson.toJson(new Quote(quoteText.getText().toString(), authorText.getText().toString()));
                //TODO: Check for Duplication
                JSONArray jsonArrayProduct = new JSONArray();

                try {
                    if (jsonSaved.length() != 0) {
                        jsonArrayProduct = new JSONArray(jsonSaved);

                        ArrayList<String> listdata = new ArrayList<>();
                        JSONArray jArray = (JSONArray) jsonArrayProduct;
                        if (jArray != null) {
                            for (int i = 0; i < jArray.length(); i++) {
                                listdata.add(jArray.getString(i));
                            }
                        }
                        listdata.add(jsonNewProductToAdd);
                    }
                    jsonArrayProduct.put(new JSONObject(jsonNewProductToAdd));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                editor.putString(PREFERENCE_NAME, String.valueOf(jsonArrayProduct));
                Log.e("vishnu", String.valueOf(jsonArrayProduct));
                editor.apply();

            /*  RETRIEVING

              SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                 gson = new Gson();
                String json2 = sharedPrefs.getString(PREFERENCE_NAME, "");
                Type type = new TypeToken<List<Quote>>() {}.getType();
                List<Quote> arrayList = gson.fromJson(json, type);
                */

            }
        });

    }


}