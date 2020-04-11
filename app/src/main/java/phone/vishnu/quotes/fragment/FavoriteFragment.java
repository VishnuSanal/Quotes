package phone.vishnu.quotes.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.helper.CustomDataAdapter;
import phone.vishnu.quotes.model.Quote;

import static android.content.Context.MODE_PRIVATE;

public class FavoriteFragment extends Fragment {
    private static final String PREFERENCE_NAME = "favPreference";
    private ListView lv;
    private CustomDataAdapter adapter;
    private ArrayList<Quote> productFromShared = new ArrayList<>();
    private SharedPreferences sharedPrefs;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    public static FavoriteFragment newInstance() {
        return new FavoriteFragment();
    }

    private final View.OnClickListener viewImageViewOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.animate);
            v.startAnimation(shake);
//            v.setColorFilter(Color.GREEN);
            Toast.makeText(getActivity(), "Coming Soon....", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Gson gson = new Gson();
        sharedPrefs = getContext().getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE);
        String jsonPreferences = sharedPrefs.getString(PREFERENCE_NAME, "");

        Type type = new TypeToken<ArrayList<Quote>>() {
        }.getType();

        if (0 != jsonPreferences.length()) productFromShared = gson.fromJson(jsonPreferences, type);
        else productFromShared.add(new Quote("No Favorite Quotes", ""));

        adapter = new CustomDataAdapter(getActivity().getApplicationContext(), productFromShared, viewImageViewOnClickListener, removeImageViewOnClickListener);
        lv.setAdapter(adapter);
    }

    private JSONArray removeFavorite(String jsonSaved, ArrayList<Quote> jsonList, String jsonProductToRemove) {

        JSONArray jsonArrayProduct = new JSONArray();
        try {
            jsonArrayProduct = new JSONArray(jsonSaved);
            jsonArrayProduct.remove(getIndex(jsonList, jsonProductToRemove));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArrayProduct;
    }

    private int getIndex(ArrayList<Quote> quoteList, String productToRemove) {

        int index = 0;
        for (int i = 0; i < quoteList.size(); i++) {

            if (productToRemove.toLowerCase().equals(quoteList.get(i).getQuote().toLowerCase())) {
                index = i;
                break;
            }
        }
        return index;
    }

    private final View.OnClickListener removeImageViewOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.animate);
            v.startAnimation(shake);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            Log.e("vishnu", String.valueOf(v.getTag()));
            int position = Integer.parseInt(v.getTag().toString());
            JSONArray jsonArray = removeFavorite(sharedPrefs.getString(PREFERENCE_NAME, ""), productFromShared, productFromShared.get(position).getQuote());
            editor.putString(PREFERENCE_NAME, String.valueOf(jsonArray));
            editor.apply();

            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Quote>>() {
            }.getType();
            productFromShared = gson.fromJson(String.valueOf(jsonArray), type);

            adapter = new CustomDataAdapter(getActivity().getApplicationContext(), productFromShared,viewImageViewOnClickListener,removeImageViewOnClickListener);
            lv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_favorite, container, false);
        lv = inflate.findViewById(R.id.favoriteListView);
//        ImageView viewIV = inflate.findViewById(R.id.singleItemViewImageView);
//        ImageView removeIV = inflate.findViewById(R.id.singleItemRemoveImageView);
        return inflate;
    }

}
