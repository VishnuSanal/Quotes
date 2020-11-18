package phone.vishnu.quotes.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.data.QuoteData;
import phone.vishnu.quotes.data.QuoteListAsyncResponse;
import phone.vishnu.quotes.fragment.AboutFragment;
import phone.vishnu.quotes.fragment.BottomSheetFragment;
import phone.vishnu.quotes.fragment.ColorFragment;
import phone.vishnu.quotes.fragment.FavoriteFragment;
import phone.vishnu.quotes.fragment.FontFragment;
import phone.vishnu.quotes.fragment.PickFragment;
import phone.vishnu.quotes.helper.ExportHelper;
import phone.vishnu.quotes.helper.QuoteViewPagerAdapter;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;
import phone.vishnu.quotes.model.Quote;
import phone.vishnu.quotes.receiver.NotificationReceiver;

public class MainActivity extends AppCompatActivity implements BottomSheetFragment.BottomSheetListener {

    public static ProgressDialog bgDialog, fontDialog;
    private final int PICK_IMAGE_ID = 36;
    private SharedPreferenceHelper sharedPreferenceHelper;
    private ExportHelper exportHelper;

    private QuoteViewPagerAdapter adapter;
    private List<Quote> allQuotesList;

    private ConstraintLayout constraintLayout;
    private ViewPager viewPager;
    private SearchView searchView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferenceHelper = new SharedPreferenceHelper(this);
        exportHelper = new ExportHelper(this);

        if (savedInstanceState == null) {
            final Bundle extras = getIntent().getExtras();
            if (extras != null && extras.getBoolean("NotificationClick")) {

                if (extras.getBoolean("ShareButton")) {

                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                exportHelper.shareScreenshot(MainActivity.this, new Quote(extras.getString("quote"), extras.getString("author")));
                            } catch (Exception e) {
                                FirebaseCrashlytics.getInstance().recordException(e);
                                e.printStackTrace();
                            }
                        }
                    });
                } else if (extras.getBoolean("FavButton")) {


                    Gson gson = new Gson();
                    String jsonSaved = sharedPreferenceHelper.getFavoriteArrayString();
                    String jsonNewProductToAdd = gson.toJson(new Quote(extras.getString("quote"), extras.getString("author")));

                    Type type = new TypeToken<ArrayList<Quote>>() {
                    }.getType();
                    ArrayList<Quote> productFromShared = gson.fromJson(jsonSaved, type);

                    sharedPreferenceHelper.setFavoriteArrayString(String.valueOf(addFavorite(jsonSaved, jsonNewProductToAdd, productFromShared, extras.getString("quote"))));
                }
            }
        }

        if (null != getIntent() && null != getIntent().getAction()) {
            if ("phone.vishnu.quotes.openMainActivity".equals(getIntent().getAction())) {

                //Do Nothing

            } else if ("phone.vishnu.quotes.openFavouriteFragment".equals(getIntent().getAction())) {

                FavoriteFragment fragment = FavoriteFragment.newInstance();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.constraintLayout, fragment)
                        .addToBackStack(null)
                        .commit();

            } else if ("phone.vishnu.quotes.shareRandomQuote".equals(getIntent().getAction())) {
                if (isNetworkAvailable()) shareRandomQuote();
            }
        }

        setContentView(R.layout.activity_main);
        constraintLayout = findViewById(R.id.constraintLayout);
        viewPager = findViewById(R.id.viewPager);
        allQuotesList = getQuotes();

        /*MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                AdView adView = new AdView(MainActivity.this);
                adView.setAdUnitId(getString(R.string.banner_ad_unit_id));

                constraintLayout.addView(adView);
                adView.setAdSize(getAdSize());

                Bundle extras = new Bundle();
                extras.putString("max_ad_content_rating", "G");

                AdRequest adRequest = new AdRequest.Builder()
                        .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                        .tagForChildDirectedTreatment(true)
                        .build();

                adView.loadAd(adRequest);
            }
        });*/

        if (!isNetworkAvailable())
            Toast.makeText(this, "Please Connect to the Internet...", Toast.LENGTH_SHORT).show();

        final String backgroundPath = sharedPreferenceHelper.getBackgroundPath();

        final File f = new File(backgroundPath);
        if (!("-1".equals(backgroundPath)) && (f.exists()))
            constraintLayout.setBackground(Drawable.createFromPath(backgroundPath));
        else {
            Toast.makeText(this, "Choose a background", Toast.LENGTH_LONG).show();

            Dexter.withContext(this)
                    .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            showBackgroundOptionChooser(false);
                        }

                        @Override
                        public void onPermissionDenied(final PermissionDeniedResponse permissionDeniedResponse) {
                            showPermissionDeniedDialog();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                            Toast.makeText(MainActivity.this, "App requires these permissions to set a background", Toast.LENGTH_SHORT).show();
                            permissionToken.continuePermissionRequest();
                        }
                    })
                    .check();
        }

        ImageView menuIcon = findViewById(R.id.homeMenuIcon);
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    BottomSheetFragment bottomSheet = new BottomSheetFragment();
                    bottomSheet.show(getSupportFragmentManager(), "bottomSheetTag");
                }
            }
        });

//        viewPager.setSaveFromParentEnabled(false);
//        viewPager.setSaveEnabled(false);
        adapter = new QuoteViewPagerAdapter(getSupportFragmentManager(), allQuotesList);
        viewPager.setAdapter(adapter);

        String s = sharedPreferenceHelper.getAlarmString();

//        if (true) {
        if ("At 08:30 Daily".equals(s)) {
            myAlarm();
        }

        searchView = findViewById(R.id.homeSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getFilter().filter(newText);
                return false;
            }
        });
    }

    private Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<Quote> filteredResults = new ArrayList<>();

                if (constraint.toString().isEmpty())
                    filteredResults.addAll(allQuotesList);
                else
                    for (Quote quote : allQuotesList) {
                        if (quote.getQuote().toLowerCase().contains(constraint.toString().toLowerCase())
                                || quote.getAuthor().toLowerCase().contains(constraint.toString().toLowerCase()))
                            filteredResults.add(quote);
                    }

                FilterResults filterResult = new FilterResults();
                filterResult.values = filteredResults;

                return filterResult;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //noinspection unchecked
                adapter.setQuoteList((List<Quote>) results.values);
                adapter.notifyDataSetChanged();
                viewPager.setCurrentItem(0);
            }
        };
    }

    private void myAlarm() {

        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 30);

        if (calendar.getTime().compareTo(new Date()) < 0)
            calendar.add(Calendar.DAY_OF_MONTH, 1);

        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    private ArrayList<Quote> getQuotes() {
        final ArrayList<Quote> quoteArrayList = new ArrayList<>();
        new QuoteData().getQuotes(new QuoteListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Quote> quotes) {
                Collections.shuffle(quotes);
                quoteArrayList.addAll(quotes);
                notifyViewPagerDataSetChanged();
            }
        });
        return quoteArrayList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {

            String file = exportHelper.getBGPath();

            if (requestCode == PICK_IMAGE_ID)

                UCrop.of(data.getData(), Uri.fromFile(new File(file)))
                        .withAspectRatio(9, 16)
                        .withMaxResultSize(1080, 1920)
                        .start(this);

            else if (requestCode == UCrop.REQUEST_CROP) {
                constraintLayout.setBackground(Drawable.createFromPath(file));
                sharedPreferenceHelper.setBackgroundPath(file);
            }
        } else Toast.makeText(this, "Error...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBottomSheetButtonClicked(int id) {
        if (id == R.id.bottomSheetFav) {
            FavoriteFragment fragment = FavoriteFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.constraintLayout, fragment)
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.bottomSheetAbout) {
            AboutFragment fragment = AboutFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.constraintLayout, fragment)
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.bottomSheetImageChooser) {

            Dexter.withContext(this)
                    .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            showBackgroundOptionChooser(true);
                        }

                        @Override
                        public void onPermissionDenied(final PermissionDeniedResponse permissionDeniedResponse) {
                            showPermissionDeniedDialog();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                            Toast.makeText(MainActivity.this, "App requires these permissions to set a background", Toast.LENGTH_SHORT).show();
                            permissionToken.continuePermissionRequest();
                        }
                    })
                    .check();

        } else if (id == R.id.bottomSheetColorChooser) {
            getSupportFragmentManager().beginTransaction().add(R.id.constraintLayout, ColorFragment.newInstance(1)).addToBackStack(null).commit();
        } else if (id == R.id.bottomSheetFont) {
            fontDialog = ProgressDialog.show(MainActivity.this, "", "Please Wait....");
            fontDialog.setCancelable(false);
            getSupportFragmentManager().beginTransaction().add(R.id.constraintLayout, FontFragment.newInstance()).addToBackStack(null).commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            viewPager.requestFocus();
        } else
            super.onBackPressed();
    }

    private void showPermissionDeniedDialog() {
        final androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Permission Denied");
        builder.setMessage("Please Accept Necessary Permissions");
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface imageDialog, int which) {
                imageDialog.cancel();
                startActivity(
                        new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                .setData(Uri.fromParts("package", getPackageName(), null))
                );
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface imageDialog, int which) {
                imageDialog.cancel();
                Toast.makeText(MainActivity.this, "App requires these permissions to run properly", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();

    }

    private void showBackgroundOptionChooser(boolean isCancellable) {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this, R.style.AlertDialogTheme);

        builder.setTitle("Choose a Background");
        builder.setCancelable(isCancellable);

        final String[] items = {"Plain Colour", "Image From Gallery", "Default Images"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: {
                        getSupportFragmentManager().beginTransaction().add(R.id.constraintLayout, ColorFragment.newInstance(0)).addToBackStack(null).commit();
                        break;
                    }
                    case 1: {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, PICK_IMAGE_ID);
                        break;
                    }
                    case 2: {
                        bgDialog = ProgressDialog.show(MainActivity.this, "", "Please Wait....");
                        bgDialog.setCancelable(false);
                        getSupportFragmentManager().beginTransaction().add(R.id.constraintLayout, PickFragment.newInstance()).addToBackStack(null).commit();
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(isCancellable);

        alertDialog.getListView().setOnHierarchyChangeListener(
                new ViewGroup.OnHierarchyChangeListener() {
                    @Override
                    public void onChildViewAdded(View parent, View child) {
                        CharSequence text = ((TextView) child).getText();
                        int itemIndex = Arrays.asList(items).indexOf(text);
                        if ((itemIndex == 2) && !isNetworkAvailable()) {
                            child.setEnabled(false);
                            child.setOnClickListener(null);
                        }
                    }

                    @Override
                    public void onChildViewRemoved(View view, View view1) {
                    }
                });
        alertDialog.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public QuoteViewPagerAdapter getQuoteViewPagerAdapter() {
        return adapter;
    }

    private JSONArray addFavorite(String jsonSaved, String jsonNewProductToAdd, ArrayList<Quote> productFromShared, String quote) {
        JSONArray jsonArrayProduct = new JSONArray();
        try {
            if (jsonSaved.length() != 0) {
                if (!isPresent(productFromShared, quote)) {
                    jsonArrayProduct = new JSONArray(jsonSaved);
                    jsonArrayProduct.put(new JSONObject(jsonNewProductToAdd));
                }
            } else {
                productFromShared = new ArrayList<>();
            }
        } catch (JSONException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return jsonArrayProduct;
    }

    private boolean isPresent(ArrayList<Quote> productFromShared, String quote) {
        boolean isPresent = false;
        for (int i = 0; i < productFromShared.size(); i++) {
            if (productFromShared.get(i).getQuote().trim().toLowerCase().equals(quote.trim().toLowerCase())) {
                isPresent = true;
            }
        }
        return isPresent;
    }

    public void notifyViewPagerDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    public void setConstraintLayoutBackground(Drawable drawable) {
        constraintLayout.setBackground(drawable);
    }

    private void shareRandomQuote() {

        final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "", "Please Wait....");
        progressDialog.setCancelable(false);

        new QuoteData().getQuotes(new QuoteListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Quote> quotes) {
                Collections.shuffle(quotes);
                Quote quote = quotes.get(0);

                new ExportHelper(MainActivity.this).shareScreenshot(MainActivity.this, quote);
                progressDialog.dismiss();
            }
        });

    }

    /*private AdSize getAdSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }*/
}
