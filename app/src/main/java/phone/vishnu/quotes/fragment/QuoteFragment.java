package phone.vishnu.quotes.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.model.Quote;

import static android.content.Context.MODE_PRIVATE;

public class QuoteFragment extends Fragment {

    private static final String PREFERENCE_NAME = "favPreference";
    private static final int PERMISSION_REQ_CODE = 2222;
    private ImageView shareIcon, favIcon;
    private TextView quoteText, authorText;

    public QuoteFragment() {
    }

    public static QuoteFragment newInstance(String quote, String author) {

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
        authorText.setText(String.format("-%s", author));
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
                final Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.animate);
                shareIcon.startAnimation(shake);
                shareIcon.setColorFilter(Color.GREEN);
                if (isPermissionGranted()) {
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            shareScreenshot();
                        }
                    });
                } else {
                    isPermissionGranted();
                }


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
                String jsonSaved = sharedPref.getString(PREFERENCE_NAME, "");
                String jsonNewProductToAdd = gson.toJson(new Quote(quoteText.getText().toString(), authorText.getText().toString()));

                Type type = new TypeToken<ArrayList<Quote>>() {
                }.getType();
                ArrayList<Quote> productFromShared = gson.fromJson(jsonSaved, type);

                editor.putString(PREFERENCE_NAME, String.valueOf(addFavorite(jsonSaved, jsonNewProductToAdd, productFromShared)));
                editor.apply();
            }


        });
    }

    private JSONArray addFavorite(String jsonSaved, String jsonNewProductToAdd, ArrayList<Quote> productFromShared) {
        JSONArray jsonArrayProduct = new JSONArray();
        try {

            if (jsonSaved.length() != 0) {
                if (!isPresent(productFromShared)) {
                    jsonArrayProduct = new JSONArray(jsonSaved);
                    jsonArrayProduct.put(new JSONObject(jsonNewProductToAdd));
                    favIcon.setColorFilter(Color.RED);
                } else {

                    favIcon.setColorFilter(Color.WHITE);
                    jsonArrayProduct = removeFavorite(jsonSaved, productFromShared, quoteText.getText().toString());
                }
            } else {
                productFromShared = new ArrayList<>();
//                addFavorite(jsonSaved,jsonNewProductToAdd,productFromShared);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArrayProduct;
    }

    private boolean isPresent(ArrayList<Quote> productFromShared) {
        boolean isPresent = false;
        for (int i = 0; i < productFromShared.size(); i++) {
            if (productFromShared.get(i).getQuote().trim().toLowerCase().equals(quoteText.getText().toString().trim().toLowerCase())) {
                isPresent = true;
            }
        }
        return isPresent;
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

    private boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 22) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showPermissionDeniedDialog();
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQ_CODE);
                }
            } else {
                return true;
            }
        }
        return false;
    }

    private void shareScreenshot() {

        View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        rootView.setDrawingCacheEnabled(true);
        Bitmap bitmap = rootView.getDrawingCache();

        File imagePath = new File(Environment.getExternalStorageDirectory() + "/Documents/.screenshot.png");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Uri uri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", imagePath);
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image/*");
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(sharingIntent, "Share via"));


    }

    private void showPermissionDeniedDialog() {

        final AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Permission Denied");
        builder.setMessage("Please Accept Permission to Capture Screenshot of the Screen");
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQ_CODE);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }


}
