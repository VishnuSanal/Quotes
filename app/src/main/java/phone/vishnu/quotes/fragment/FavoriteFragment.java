package phone.vishnu.quotes.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.helper.CustomDataAdapter;
import phone.vishnu.quotes.model.Quote;

import static android.content.Context.MODE_PRIVATE;

public class FavoriteFragment extends Fragment {
    private final String PREFERENCE_NAME = "favPreference";
    private final int PERMISSION_REQ_CODE = 2222;
    private ListView lv;
    private CustomDataAdapter adapter;
    private ImageView addImageView;
    private ArrayList<Quote> productFromShared = new ArrayList<>();
    private final View.OnClickListener viewImageViewOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            {
                final Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.animate);
                v.startAnimation(shake);

                if (isPermissionGranted()) {
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            int position = Integer.parseInt(v.getTag().toString());
                            shareScreenshot(productFromShared.get(position).getQuote(), productFromShared.get(position).getAuthor());
                        }
                    });
                } else {
                    isPermissionGranted();
                }
            }

        }
    };
    private SharedPreferences sharedPrefs;
    private final View.OnClickListener removeImageViewOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            {
                final Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.animate);
                v.startAnimation(shake);
                SharedPreferences.Editor editor = sharedPrefs.edit();

                int position = Integer.parseInt(v.getTag().toString());
//            Log.e("vishnu", "onClick: " + position);

                JSONArray jsonArray = removeFavorite(sharedPrefs.getString(PREFERENCE_NAME, ""), productFromShared, productFromShared.get(position).getQuote());
                editor.putString(PREFERENCE_NAME, String.valueOf(jsonArray));
                editor.apply();

                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<Quote>>() {
                }.getType();

                productFromShared = gson.fromJson(sharedPrefs.getString(PREFERENCE_NAME, ""), type);

                adapter = new CustomDataAdapter(getActivity().getApplicationContext(), productFromShared, viewImageViewOnClickListener, removeImageViewOnClickListener);
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
        sharedPrefs = getContext().getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE);
        String jsonPreferences = sharedPrefs.getString(PREFERENCE_NAME, "");

        Type type = new TypeToken<ArrayList<Quote>>() {
        }.getType();

        if (0 != jsonPreferences.length()) productFromShared = gson.fromJson(jsonPreferences, type);
        else productFromShared.add(new Quote("No Favorite Quotes", ""));

        adapter = new CustomDataAdapter(getActivity().getApplicationContext(), productFromShared, viewImageViewOnClickListener, removeImageViewOnClickListener);
        lv.setAdapter(adapter);

        addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.favoriteConstraintLayout, AddNewFragment.newInstance()).commit();
            }
        });

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_favorite, container, false);
        lv = inflate.findViewById(R.id.favoriteListView);
        addImageView = inflate.findViewById(R.id.favoriteAddImageView);
        return inflate;
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

    private void showPermissionDeniedDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
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

    private void shareScreenshot(String quote, String author) {

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @SuppressLint("InflateParams") View shareView = inflater.inflate(R.layout.share_layout, null);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE);
        String hexColor = sharedPreferences.getString("colorPreference", "#607D8B");
        String fontPath = sharedPreferences.getString("fontPreference", "-1");

        String backgroundPath = sharedPreferences.getString("backgroundPreference", "-1");
        if (!"-1".equals(backgroundPath))
            shareView.findViewById(R.id.shareRelativeLayout).setBackground(Drawable.createFromPath(backgroundPath));

        /*shareView.findViewById(R.id.shareRelativeLayout)
                .setBackgroundColor(
                        Color.parseColor("#ff0000ff")
                );*/


        CardView cardView = shareView.findViewById(R.id.shareCardView);
        cardView.setCardBackgroundColor(Color.parseColor(hexColor));

//        ((ImageView) shareView.findViewById(R.id.shareFavoriteImageView)).setColorFilter(Color.RED);
//        ((ImageView) shareView.findViewById(R.id.shareShareImageView)).setColorFilter(Color.GREEN);

        if (!fontPath.equals("-1")) {
            Typeface face = Typeface.createFromFile(fontPath);
            ((TextView) shareView.findViewById(R.id.shareQuoteTextView)).setTypeface(face);
        }

        ((TextView) shareView.findViewById(R.id.shareQuoteTextView)).setText(quote);
        ((TextView) shareView.findViewById(R.id.shareAuthorTextView)).setText(author);

        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);

        shareView.measure(View.MeasureSpec.makeMeasureSpec(metrics.widthPixels, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(metrics.heightPixels, View.MeasureSpec.EXACTLY));

        shareView.setDrawingCacheEnabled(true);

        shareView.findViewById(R.id.shareRelativeLayout).setLayoutParams(new LinearLayout.LayoutParams(metrics.widthPixels, metrics.heightPixels));
        ConstraintLayout.LayoutParams cardParams = new ConstraintLayout.LayoutParams(300, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardParams.verticalBias = 0.5f;
        cardParams.horizontalBias = 0.5f;

        Bitmap bitmap = Bitmap.createBitmap(metrics.widthPixels, metrics.heightPixels, Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(bitmap);
        shareView.layout(0, 0, metrics.widthPixels, metrics.heightPixels);
        shareView.draw(c);

        shareView.buildDrawingCache(true);

        File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Quotes");
        if (!root.exists()) root.mkdirs();
        String imagePath = root.toString() + File.separator + ".Screenshot" + ".jpg";
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Uri uri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", new File(imagePath));
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image/*");
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }
}
