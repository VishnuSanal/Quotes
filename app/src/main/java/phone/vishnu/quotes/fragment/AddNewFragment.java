package phone.vishnu.quotes.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;
import phone.vishnu.quotes.model.Quote;

public class AddNewFragment extends Fragment {

    private SharedPreferenceHelper sharedPreferenceHelper;
    private TextInputEditText quoteTIE, authorTIE;
    private Button saveButton, cancelButton;

    public AddNewFragment() {
    }

    public static AddNewFragment newInstance() {
        return new AddNewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_add_new, container, false);

        quoteTIE = inflate.findViewById(R.id.addQuoteTIE);
        authorTIE = inflate.findViewById(R.id.addAuthorTIE);

        saveButton = inflate.findViewById(R.id.buttonAdd);
        cancelButton = inflate.findViewById(R.id.buttonCancel);

        sharedPreferenceHelper = new SharedPreferenceHelper(requireContext());

        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String quote = quoteTIE.getText().toString().trim();
                String author = authorTIE.getText().toString().trim();

                if (quote.isEmpty() || author.isEmpty()) {
                    if (quote.isEmpty()) {
                        quoteTIE.setError("Field Empty");
                        quoteTIE.requestFocus();
                    } else if (author.isEmpty()) {
                        authorTIE.setError("Field Empty");
                        authorTIE.requestFocus();
                    }
                } else {


                    Gson gson = new Gson();
                    String jsonSaved = sharedPreferenceHelper.getFavoriteArrayString();
                    String jsonNewProductToAdd = gson.toJson(new Quote(quote, author));

                    Type type = new TypeToken<ArrayList<Quote>>() {
                    }.getType();
                    ArrayList<Quote> productFromShared = gson.fromJson(jsonSaved, type);

                    sharedPreferenceHelper.setFavoriteArrayString(String.valueOf(addFavorite(jsonSaved, jsonNewProductToAdd, productFromShared)));

                    Toast.makeText(requireContext(), "Quote Added to Favourites...", Toast.LENGTH_SHORT).show();

                    requireActivity().onBackPressed();
                }
            }
        });
    }

    private JSONArray addFavorite(String jsonSaved, String jsonNewProductToAdd, ArrayList<Quote> productFromShared) {
        JSONArray jsonArrayProduct = new JSONArray();
        try {
            if (jsonSaved.length() != 0) {
                jsonArrayProduct = new JSONArray(jsonSaved);
                jsonArrayProduct.put(new JSONObject(jsonNewProductToAdd));
            } else {
                productFromShared = new ArrayList<>();
            }
        } catch (JSONException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return jsonArrayProduct;
    }
}