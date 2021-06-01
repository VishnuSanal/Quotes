package phone.vishnu.quotes.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.adapter.QuoteViewPagerAdapter;
import phone.vishnu.quotes.data.QuoteData;
import phone.vishnu.quotes.fragment.AboutFragment;
import phone.vishnu.quotes.fragment.ColorFragment;
import phone.vishnu.quotes.fragment.FavoriteFragment;
import phone.vishnu.quotes.fragment.FontMasterFragment;
import phone.vishnu.quotes.fragment.PickFragment;
import phone.vishnu.quotes.fragment.SettingsFragment;
import phone.vishnu.quotes.helper.AlarmHelper;
import phone.vishnu.quotes.helper.ExportHelper;
import phone.vishnu.quotes.helper.FavUtils;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;
import phone.vishnu.quotes.model.Quote;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static ProgressDialog bgDialog, fontDialog;
    private final int PICK_IMAGE_ID = 36;
    private SharedPreferenceHelper sharedPreferenceHelper;
    private ExportHelper exportHelper;

    private QuoteViewPagerAdapter adapter;
    private List<Quote> allQuotesList;

    private ConstraintLayout constraintLayout;
    private ViewPager viewPager;

    private FloatingActionButton fontFAB, aboutFAB, bgFAB, colorFAB, favFAB, settingsFAB, homeFAB;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferenceHelper = new SharedPreferenceHelper(this);
        exportHelper = new ExportHelper(this);

        if (savedInstanceState == null) {
            final Bundle extras = getIntent().getExtras();
            if (extras != null && extras.getBoolean("NotificationClick")) {

                if (extras.getBoolean("ShareButton")) {

                    AsyncTask.execute(() -> {
                        try {
                            exportHelper.shareImage(MainActivity.this, new Quote(extras.getString("quote"), extras.getString("author")));
                        } catch (Exception e) {
                            FirebaseCrashlytics.getInstance().recordException(e);
                            e.printStackTrace();
                        }
                    });
                } else if (extras.getBoolean("FavButton")) {
                    addFavourite(
                            this,
                            new Quote(extras.getString("quote"), extras.getString("author")));
                }
            }
        }

        if (null != getIntent() && null != getIntent().getAction()) {
            //Shortcut
            //noinspection StatementWithEmptyBody
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
                shareRandomQuote();
            }
            //Widget
            else if ("phone.vishnu.quotes.widgetShareClicked".equals(getIntent().getAction())) {
                Quote q = (sharedPreferenceHelper.getWidgetQuote());
                if (q != null)
                    exportHelper.shareImage(this, q);

            } else if ("phone.vishnu.quotes.widgetFavClicked".equals(getIntent().getAction())) {
                Quote q = (sharedPreferenceHelper.getWidgetQuote());
                if (q != null) {
                    addFavourite(this, q);
                }
            }
        }

        setContentView(R.layout.activity_main);
        constraintLayout = findViewById(R.id.constraintLayout);
        viewPager = findViewById(R.id.viewPager);
        allQuotesList = getQuotes();

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

        fontFAB = findViewById(R.id.fontFAB);
        aboutFAB = findViewById(R.id.aboutFAB);
        bgFAB = findViewById(R.id.bgFAB);
        colorFAB = findViewById(R.id.colorFAB);
        favFAB = findViewById(R.id.favFAB);
        settingsFAB = findViewById(R.id.settingsFAB);
        homeFAB = findViewById(R.id.homeFAB);

        homeFAB.setOnClickListener(this);
        settingsFAB.setOnClickListener(this);
        favFAB.setOnClickListener(this);
        fontFAB.setOnClickListener(this);
        colorFAB.setOnClickListener(this);
        bgFAB.setOnClickListener(this);
        aboutFAB.setOnClickListener(this);

        initAnimations();

        adapter = new QuoteViewPagerAdapter(getSupportFragmentManager(), allQuotesList);
        viewPager.setAdapter(adapter);

        if ("At 08:30 Daily".equals(sharedPreferenceHelper.getAlarmString()))
            AlarmHelper.setDefaultAlarm(this);

        SearchView searchView = findViewById(R.id.homeSearchView);

        try {
            EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
            searchEditText.setTextColor(getResources().getColor(R.color.colorWhite));
            searchEditText.setHintTextColor(getResources().getColor(R.color.colorWhite));
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }

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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.homeFAB) {
            homeFAB.startAnimation(AnimationUtils.loadAnimation(this, R.anim.animate));
            if (null == homeFAB.getTag()) {
                if (isFABMenuHidden()) {
                    openFABMenu();
                } else {
                    closeFABMenu();
                }
            } else {
                onBackPressed();
                resetHomeFAB();
            }
        } else {
            closeFABMenu();
            if (id == R.id.favFAB) {
                FavoriteFragment fragment = FavoriteFragment.newInstance();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.constraintLayout, fragment)
                        .addToBackStack(null)
                        .commit();
                setHomeFABHome();
            } else if (id == R.id.aboutFAB) {
                AboutFragment fragment = AboutFragment.newInstance();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.constraintLayout, fragment)
                        .addToBackStack(null)
                        .commit();
                setHomeFABHome();
            } else if (id == R.id.bgFAB) {
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
            } else if (id == R.id.colorFAB) {
                getSupportFragmentManager().beginTransaction().add(R.id.constraintLayout, ColorFragment.newInstance(1)).addToBackStack(null).commit();
                setHomeFABHome();
            } else if (id == R.id.fontFAB) {
                fontDialog = new ProgressDialog(MainActivity.this, R.style.DialogTheme);
                fontDialog.setMessage("Please Wait....");
                fontDialog.show();
                fontDialog.setCancelable(false);
                getSupportFragmentManager().beginTransaction().add(R.id.constraintLayout, FontMasterFragment.newInstance()).addToBackStack(null).commit();
                setHomeFABHome();
            } else if (id == R.id.settingsFAB) {
                SettingsFragment fragment = SettingsFragment.newInstance();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.constraintLayout, fragment)
                        .addToBackStack(null)
                        .commit();

                setHomeFABHome();
            }
        }
    }

    private void setHomeFABHome() {
        homeFAB.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_home));
        homeFAB.setTag("Menu Opened");
    }

    private void resetHomeFAB() {
        homeFAB.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_menu));
        homeFAB.setTag(null);
    }

    private boolean isFABMenuHidden() {
        return fontFAB.getVisibility() == View.GONE &&
                aboutFAB.getVisibility() == View.GONE &&
                settingsFAB.getVisibility() == View.GONE &&
                favFAB.getVisibility() == View.GONE &&
                colorFAB.getVisibility() == View.GONE &&
                bgFAB.getVisibility() == View.GONE;
    }

    private void closeFABMenu() {

        float homeFABX = homeFAB.getX();
        float homeFABY = homeFAB.getY();

        favFAB.animate().x(homeFABX).y(homeFABY).rotation(360).alpha(0f).setInterpolator(new AccelerateInterpolator());
        aboutFAB.animate().x(homeFABX).y(homeFABY).rotation(360).alpha(0f).setInterpolator(new AccelerateInterpolator());
        colorFAB.animate().x(homeFABX).y(homeFABY).rotation(360).alpha(0f).setInterpolator(new AccelerateInterpolator());
        settingsFAB.animate().x(homeFABX).y(homeFABY).rotation(360).alpha(0f).setInterpolator(new AccelerateInterpolator());
        bgFAB.animate().x(homeFABX).y(homeFABY).rotation(360).alpha(0f).setInterpolator(new AccelerateInterpolator());
        fontFAB.animate().x(homeFABX).y(homeFABY).rotation(360).alpha(0f).setInterpolator(new AccelerateInterpolator());

        homeFAB.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_menu));

        new Handler().postDelayed(() -> setVisibility(View.GONE), 500);
    }

    private void openFABMenu() {
        setVisibility(View.VISIBLE);

        int i = 120;
        double cos36 = Math.cos(Math.toRadians(36));
        double sin36 = Math.sin(Math.toRadians(36));

        double cos72 = Math.cos(Math.toRadians(72));
        double sin72 = Math.sin(Math.toRadians(72));

        favFAB.animate().translationX(DPtoPX(i)).rotationBy(360).alpha(1f).setInterpolator(new AccelerateInterpolator());
        aboutFAB.animate().translationX(-DPtoPX(i)).rotationBy(360).alpha(1f).setInterpolator(new AccelerateInterpolator());

        colorFAB.animate().translationX(DPtoPX((int) (i * cos36))).translationY(-DPtoPX((int) (i * sin36))).rotationBy(360).alpha(1f).setInterpolator(new AccelerateInterpolator());
        settingsFAB.animate().translationX(-DPtoPX((int) (i * cos36))).translationY(-DPtoPX((int) (i * sin36))).rotationBy(360).alpha(1f).setInterpolator(new AccelerateInterpolator());

        bgFAB.animate().translationX(DPtoPX((int) (i * cos72))).translationY(-DPtoPX((int) (i * sin72))).rotationBy(360).alpha(1f).setInterpolator(new AccelerateInterpolator());
        fontFAB.animate().translationX(-DPtoPX((int) (i * cos72))).translationY(-DPtoPX((int) (i * sin72))).rotationBy(360).alpha(1f).setInterpolator(new AccelerateInterpolator());

        homeFAB.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_close));
    }

    private void setVisibility(int i) {
        favFAB.setVisibility(i);
        colorFAB.setVisibility(i);
        bgFAB.setVisibility(i);
        fontFAB.setVisibility(i);
        settingsFAB.setVisibility(i);
        aboutFAB.setVisibility(i);
    }

    public int DPtoPX(int DP) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(DP * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private void initAnimations() {
        ScaleAnimation scaleAnimation =
                new ScaleAnimation(0, 1f, 0, 1f, Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(750);
        scaleAnimation.setFillAfter(true);

        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(750);
        alphaAnimation.setInterpolator(new LinearInterpolator());
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        findViewById(R.id.openIndicatorLeft).setAnimation(scaleAnimation);
        findViewById(R.id.openIndicatorLeft).setAnimation(alphaAnimation);

        findViewById(R.id.openIndicatorRight).setAnimation(scaleAnimation);
        findViewById(R.id.openIndicatorRight).setAnimation(alphaAnimation);
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

                String s = (adapter.getCount() == sharedPreferenceHelper.getTotalQuotesCount()) ? "" : String.valueOf(adapter.getCount());
                ((TextView) findViewById(R.id.searchCountTV)).setText(s);
            }
        };
    }

    private ArrayList<Quote> getQuotes() {
        final ArrayList<Quote> quoteArrayList = new ArrayList<>();
        new QuoteData().getQuotes(quotes -> {
            sharedPreferenceHelper.setTotalQuotesCount(quotes.size());
            Collections.shuffle(quotes);
            quoteArrayList.addAll(quotes);
            updateViewPager();
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
        } else Toast.makeText(this, "Oops! Something went wrong!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        resetHomeFAB();
        super.onBackPressed();
    }

    private void showPermissionDeniedDialog() {
        final androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Permission Denied");
        builder.setMessage("Please Accept Necessary Permissions");
        builder.setCancelable(true);
        builder.setPositiveButton("OK", (imageDialog, which) -> {
            imageDialog.cancel();
            startActivity(
                    new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            .setData(Uri.fromParts("package", getPackageName(), null))
            );
        });
        builder.setNegativeButton("Cancel", (imageDialog, which) -> {
            imageDialog.cancel();
            Toast.makeText(MainActivity.this, "App requires these permissions to run properly", Toast.LENGTH_SHORT).show();
        });
        builder.show();

    }

    private void showBackgroundOptionChooser(boolean isCancellable) {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this, R.style.AlertDialogTheme);

        builder.setTitle("Choose a Background");
        builder.setCancelable(isCancellable);

        final String[] items = {"Plain Colour", "Image From Gallery", "Default Images"};
        builder.setItems(items, (dialog, which) -> {
            switch (which) {
                case 0: {
                    getSupportFragmentManager().beginTransaction().add(R.id.constraintLayout, ColorFragment.newInstance(0)).addToBackStack(null).commit();
                    setHomeFABHome();
                    break;
                }
                case 1: {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, PICK_IMAGE_ID);
                    break;
                }
                case 2: {
                    bgDialog = new ProgressDialog(MainActivity.this, R.style.DialogTheme);
                    bgDialog.setMessage("Please Wait....");
                    bgDialog.show();
                    bgDialog.setCancelable(false);
                    getSupportFragmentManager().beginTransaction().add(R.id.constraintLayout, PickFragment.newInstance()).addToBackStack(null).commit();
                    setHomeFABHome();
                    break;
                }
                default: {
                    break;
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(isCancellable);

        alertDialog.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public QuoteViewPagerAdapter getQuoteViewPagerAdapter() {
        return adapter;
    }

    private void addFavourite(Context context, Quote quote) {
        FavUtils favUtils = new FavUtils(context);

        if (favUtils.isPresent(quote)) {
            Toast.makeText(this, "Already Present in Favourites", Toast.LENGTH_SHORT).show();
        } else {
            favUtils.addFavorite(quote);
            Toast.makeText(this, "Added to Favourites", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateViewPager() {
        adapter.notifyDataSetChanged();
    }

    public void setConstraintLayoutBackground(Drawable drawable) {
        constraintLayout.setBackground(drawable);
    }

    private void shareRandomQuote() {

        final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "", "Please Wait....");
        progressDialog.setCancelable(false);

        new QuoteData().getQuotes(quotes -> {
            Collections.shuffle(quotes);
            if (quotes.size() == 0) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Loading Failed", Toast.LENGTH_SHORT).show();
                return;
            }
            Quote quote = quotes.get(0);

            new ExportHelper(MainActivity.this).shareImage(MainActivity.this, quote);
            progressDialog.dismiss();
        });

    }
}