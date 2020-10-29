package phone.vishnu.quotes.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.helper.ExportHelper;
import phone.vishnu.quotes.helper.FavoritesDataAdapter;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;
import phone.vishnu.quotes.model.Quote;

public class FavoriteFragment extends Fragment {

    private SharedPreferenceHelper sharedPreferenceHelper;
    private ExportHelper exportHelper;
    private ListView lv;
    private FavoritesDataAdapter adapter;
    private ImageView addImageView;
    private ArrayList<Quote> productFromShared = new ArrayList<>();

    private final View.OnClickListener viewImageViewOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            {
                final Animation shake = AnimationUtils.loadAnimation(requireContext(), R.anim.animate);
                v.startAnimation(shake);

                Dexter.withContext(requireContext())
                        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        int position = Integer.parseInt(v.getTag().toString());
                                        exportHelper.shareScreenshot(requireContext(), productFromShared.get(position).getQuote(), productFromShared.get(position).getAuthor());
                                    }
                                });
                            }

                            @Override
                            public void onPermissionDenied(final PermissionDeniedResponse permissionDeniedResponse) {
                                showPermissionDeniedDialog();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                Toast.makeText(requireContext(), "App requires these permissions to run properly", Toast.LENGTH_SHORT).show();
                                permissionToken.continuePermissionRequest();
                            }
                        })
                        .check();

            }

        }
    };

    private final View.OnClickListener removeImageViewOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            {
                final Animation shake = AnimationUtils.loadAnimation(requireContext(), R.anim.animate);
                v.startAnimation(shake);

                int position = Integer.parseInt(v.getTag().toString());

                JSONArray jsonArray = removeFavorite(sharedPreferenceHelper.getFavoriteArrayString(), productFromShared, productFromShared.get(position).getQuote());
                sharedPreferenceHelper.setFavoriteArrayString(String.valueOf(jsonArray));

                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<Quote>>() {
                }.getType();

                productFromShared = gson.fromJson(sharedPreferenceHelper.getFavoriteArrayString(), type);

                adapter = new FavoritesDataAdapter(requireContext().getApplicationContext(), productFromShared, viewImageViewOnClickListener, removeImageViewOnClickListener);
                lv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }
    };

    public FavoriteFragment() {
        // Required empty public constructor
    }

    public static FavoriteFragment newInstance() {
        return new FavoriteFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Gson gson = new Gson();

        String jsonPreferences = sharedPreferenceHelper.getFavoriteArrayString();

        Type type = new TypeToken<ArrayList<Quote>>() {
        }.getType();

        if (0 != jsonPreferences.length()) productFromShared = gson.fromJson(jsonPreferences, type);
        else productFromShared.add(new Quote("No Favorite Quotes", ""));

        adapter = new FavoritesDataAdapter(requireContext().getApplicationContext(), productFromShared, viewImageViewOnClickListener, removeImageViewOnClickListener);
        lv.setAdapter(adapter);

        addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.favoriteConstraintLayout, AddNewFragment.newInstance()).commit();
            }
        });

    }

    private JSONArray removeFavorite(String jsonSaved, ArrayList<Quote> jsonList, String jsonProductToRemove) {

        JSONArray jsonArrayProduct = new JSONArray();
        try {
            jsonArrayProduct = new JSONArray(jsonSaved);
            jsonArrayProduct.remove(getIndex(jsonList, jsonProductToRemove));
        } catch (JSONException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_favorite, container, false);
        lv = inflate.findViewById(R.id.favoriteListView);
        addImageView = inflate.findViewById(R.id.favoriteAddImageView);
        sharedPreferenceHelper = new SharedPreferenceHelper(requireContext());
        exportHelper = new ExportHelper(requireContext());
        return inflate;
    }

    private void showPermissionDeniedDialog() {
        final androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Permission Denied");
        builder.setMessage("Please Accept Necessary Permissions");
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface imageDialog, int which) {
                imageDialog.cancel();
                startActivity(
                        new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                .setData(Uri.fromParts("package", requireContext().getPackageName(), null))
                );
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface imageDialog, int which) {
                imageDialog.cancel();
                Toast.makeText(requireContext(), "App requires these permissions to run properly", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();

    }
}
