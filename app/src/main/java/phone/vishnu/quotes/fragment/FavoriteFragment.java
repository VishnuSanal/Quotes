package phone.vishnu.quotes.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.helper.CustomDataAdapter;
import phone.vishnu.quotes.model.Quote;

import static android.content.Context.MODE_PRIVATE;

public class FavoriteFragment extends Fragment {
    private static final String PREFERENCE_NAME = "favPreference";
    private ListView lv;
    private ArrayAdapter<Quote> mAdapter;
    private ImageView viewIV, removeIV;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    public static FavoriteFragment newInstance() {
        return new FavoriteFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_favorite, container, false);
        lv = inflate.findViewById(R.id.favoriteListView);
        viewIV = inflate.findViewById(R.id.singleItemViewImageView);
        removeIV = inflate.findViewById(R.id.singleItemRemoveImageView);
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Gson gson = new Gson();
        ArrayList<Quote> productFromShared = new ArrayList<>();
        SharedPreferences sharedPrefs = getContext().getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE);
        String jsonPreferences = sharedPrefs.getString(PREFERENCE_NAME, "");
        Type type = new TypeToken<ArrayList<Quote>>() {
        }.getType();
        if (0 != jsonPreferences.length()) productFromShared = gson.fromJson(jsonPreferences, type);
        else productFromShared.add(new Quote("No Favorite Quotes", ""));
//        try{
        CustomDataAdapter adapter = new CustomDataAdapter(getActivity().getApplicationContext(), productFromShared);
            lv.setAdapter(adapter);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

     /*   viewIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        removeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

    }
}
