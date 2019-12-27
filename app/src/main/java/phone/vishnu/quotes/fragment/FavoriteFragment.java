package phone.vishnu.quotes.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.helper.CustomDataAdapter;
import phone.vishnu.quotes.model.Quote;

import static android.content.Context.MODE_PRIVATE;

public class FavoriteFragment extends Fragment {
    private static final String PREFERENCE_NAME = "favPreference";
    private ListView lv;
    private ArrayAdapter<Quote> mAdapter;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    public static FavoriteFragment newInstance() {
        return new FavoriteFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_favorite, container, false);
        lv = inflate.findViewById(R.id.favoriteListView);
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       /* SharedPreferences sharedPrefs = getContext().getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPrefs.getString(PREFERENCE_NAME, "");
        ArrayList<Quote> arrayList = gson.fromJson(json,new TypeToken<List<Quote>>() {}.getType() );
        Log.e("vishnu",String.valueOf(arrayList));
        CustomDataAdapter adapter = new CustomDataAdapter(getActivity().getApplicationContext(), arrayList);

       lv.setAdapter(adapter);*/

        Gson gson = new Gson();
        ArrayList<Quote> productFromShared;
        SharedPreferences sharedPrefs = getContext().getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE);
        String jsonPreferences = sharedPrefs.getString(PREFERENCE_NAME, "");
        Log.e("vishnu",String.valueOf(jsonPreferences));
        Type type = new TypeToken<ArrayList<Quote>>() {}.getType();
        productFromShared = gson.fromJson(jsonPreferences, type);
        CustomDataAdapter adapter = new CustomDataAdapter(getActivity().getApplicationContext(), productFromShared);

        lv.setAdapter(adapter);

    }
}
