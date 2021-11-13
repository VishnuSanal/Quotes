/*
 * Copyright (C) 2019 - 2019-2021 Vishnu Sanal. T
 *
 * This file is part of Quotes Status Creator.
 *
 * Quotes Status Creator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package phone.vishnu.quotes.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.GestureDetector;
import android.view.MotionEvent;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.yalantis.ucrop.UCrop;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.adapter.QuoteViewPagerAdapter;
import phone.vishnu.quotes.fragment.AboutFragment;
import phone.vishnu.quotes.fragment.BGOptionPickFragment;
import phone.vishnu.quotes.fragment.ColorPickFragment;
import phone.vishnu.quotes.fragment.FavoriteFragment;
import phone.vishnu.quotes.fragment.FontOptionPickFragment;
import phone.vishnu.quotes.fragment.SettingsFragment;
import phone.vishnu.quotes.fragment.ShareOptionPickFragment;
import phone.vishnu.quotes.helper.AlarmHelper;
import phone.vishnu.quotes.helper.ExportHelper;
import phone.vishnu.quotes.helper.ShareHelper;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;
import phone.vishnu.quotes.model.Quote;
import phone.vishnu.quotes.repository.FavRepository;
import phone.vishnu.quotes.repository.QuotesRepository;
import phone.vishnu.quotes.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int PICK_IMAGE_ID = 36;

    public static ProgressDialog bgDialog, fontDialog;

    private SharedPreferenceHelper sharedPreferenceHelper;
    private ExportHelper exportHelper;

    private QuoteViewPagerAdapter adapter;

    private ConstraintLayout constraintLayout;

    private ViewPager viewPager;
    private MainViewModel viewModel;
    private ArrayList<Quote> allQuotesList;

    private FloatingActionButton fontFAB, aboutFAB, bgFAB, colorFAB, favFAB, settingsFAB, homeFAB;

    private CircularProgressIndicator progressIndicator;
    private ChipGroup chipGroup;
    private SwipeListener swipeListener;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferenceHelper = new SharedPreferenceHelper(this);
        exportHelper = new ExportHelper(this);

        viewModel =
                new ViewModelProvider(
                                this,
                                new ViewModelProvider.AndroidViewModelFactory(
                                        (Application) getApplicationContext()))
                        .get(MainViewModel.class);

        setIntentListeners(savedInstanceState);

        setContentView(R.layout.activity_main);

        constraintLayout = findViewById(R.id.constraintLayout);
        progressIndicator = findViewById(R.id.mainProgressIndicator);
        chipGroup = findViewById(R.id.homeChipGroup);
        initViewPager();
        runInitChecks();
        initFABs();
        initAnimations();
        setUpSearchView();
        setUpChipGroup();
        swipeListener = new SwipeListener(homeFAB);
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
                FavoriteFragment.newInstance()
                        .show(getSupportFragmentManager(), "FavoriteFragment");
            } else if (id == R.id.aboutFAB) {
                AboutFragment.newInstance().show(getSupportFragmentManager(), "AboutFragment");
            } else if (id == R.id.bgFAB) {

                showBackgroundOptionChooser(true);

            } else if (id == R.id.colorFAB) {

                ColorPickFragment.newInstance(ColorPickFragment.PICK_CARD_COLOR_REQ_CODE)
                        .show(getSupportFragmentManager(), "ColorPickBottomSheetDialogFragment");

            } else if (id == R.id.fontFAB) {

                FontOptionPickFragment.newInstance()
                        .show(getSupportFragmentManager(), "FontOptionPickFragment");

            } else if (id == R.id.settingsFAB) {
                SettingsFragment.newInstance()
                        .show(getSupportFragmentManager(), "SettingsFragment");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            String file = exportHelper.getBGPath();

            if (requestCode == PICK_IMAGE_ID
                    && data != null
                    && (null != data.getData()
                            || (null != data.getExtras()
                                    && data.getExtras().containsKey("data")))) {

                Uri uri = data.getData();

                if (data.getData() == null
                        && (null != data.getExtras() && data.getExtras().containsKey("data"))) {

                    uri = Uri.fromFile(new File(file));

                    exportHelper.exportBackgroundImage((Bitmap) data.getExtras().get("data"));
                }

                UCrop.of(uri, Uri.fromFile(new File(file)))
                        .withAspectRatio(9, 16)
                        .withMaxResultSize(1080, 1920)
                        .start(this);

            } else if (requestCode == UCrop.REQUEST_CROP) {

                constraintLayout.setBackground(Drawable.createFromPath(file));
                sharedPreferenceHelper.setBackgroundPath(file);
            }

        } else {
            if (resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(this, "Action Cancelled!", Toast.LENGTH_SHORT).show();
            else Toast.makeText(this, "Oops! Something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        resetHomeFAB();
        super.onBackPressed();
    }

    private void setIntentListeners(Bundle savedInstanceState) {

        if (savedInstanceState == null) {
            final Bundle extras = getIntent().getExtras();
            if (extras != null && extras.getBoolean("NotificationClick")) {

                if (extras.getBoolean("ShareButton")) {

                    showShareActionPicker(
                            new Quote(
                                    Objects.requireNonNull(extras.getString("quote")),
                                    Objects.requireNonNull(extras.getString("author"))));

                } else if (extras.getBoolean("FavButton")) {

                    addFavourite(
                            new Quote(
                                    Objects.requireNonNull(extras.getString("quote")),
                                    Objects.requireNonNull(extras.getString("author"))));
                }
            }
        }

        if (null != getIntent() && null != getIntent().getAction()) {
            // Shortcut
            if ("phone.vishnu.quotes.openFavouriteFragment".equals(getIntent().getAction())) {

                FavoriteFragment.newInstance()
                        .show(getSupportFragmentManager(), "FavoriteFragment");

            } else if ("phone.vishnu.quotes.shareRandomQuote".equals(getIntent().getAction())) {
                shareRandomQuote();
            }
            // Widget
            else if ("phone.vishnu.quotes.widgetShareClicked".equals(getIntent().getAction())) {
                Quote q = (sharedPreferenceHelper.getWidgetQuote());
                if (q != null)
                    shareButtonClicked(
                            this, new SharedPreferenceHelper(this).getShareButtonAction(), q);

            } else if ("phone.vishnu.quotes.widgetFavClicked".equals(getIntent().getAction())) {
                Quote q = (sharedPreferenceHelper.getWidgetQuote());
                if (q != null) {
                    addFavourite(q);
                }
            }
        }
    }

    private void setUpSearchView() {
        SearchView searchView = findViewById(R.id.homeSearchView);

        try {
            EditText searchEditText =
                    searchView.findViewById(androidx.appcompat.R.id.search_src_text);
            searchEditText.setTextColor(getResources().getColor(R.color.colorWhite));
            searchEditText.setHintTextColor(getResources().getColor(R.color.colorWhite));
        } catch (Exception e) {
            e.printStackTrace();
        }

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        getFilter().filter(query);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        getFilter().filter(newText);

                        chipGroup.clearCheck();

                        for (int i = 0; i < chipGroup.getChildCount(); i++)
                            chipGroup
                                    .getChildAt(i)
                                    .setEnabled(!((newText == null) || (!newText.equals(""))));

                        return false;
                    }
                });
    }

    private void setUpChipGroup() {

        String[] tags = {
            "Life",
            "Success",
            "Love",
            "Action",
            "Dream",
            "Fail",
            "Thought",
            "Heart",
            "Mistake",
            "Wisdom",
            "Fear",
            "Courage",
            "Friend",
            "Attitude",
            "Perseverance",
            "Motivation",
            "Inspiration"
        };

        for (String string : tags) {

            Chip chip =
                    new Chip(new ContextThemeWrapper(chipGroup.getContext(), R.style.ChipStyle));

            chip.setText(string);
            chip.setTextColor(getResources().getColor(R.color.colorWhite));
            chip.setLetterSpacing(0.15f);

            chip.setChipBackgroundColor(
                    new ColorStateList(
                            new int[][] {
                                new int[] {android.R.attr.state_checked},
                                new int[] {-android.R.attr.state_checked},
                                new int[] {
                                    -android.R.attr.state_checked, -android.R.attr.state_focused
                                }
                            },
                            new int[] {
                                Color.parseColor("#424242"),
                                Color.parseColor("#2A2A2A"),
                                Color.parseColor("#2A2A2A")
                            }));

            chip.setCheckable(true);
            chip.setCheckedIconVisible(true);

            chipGroup.addView(chip);

            chip.setOnCheckedChangeListener(
                    (buttonView, isChecked) ->
                            getFilter().filter(isChecked ? buttonView.getText() : ""));
        }
    }

    private void runInitChecks() {

        progressIndicator.setIndicatorColor(
                Color.parseColor(sharedPreferenceHelper.getCardColorPreference()));

        new Handler()
                .postDelayed(
                        () -> {
                            if (progressIndicator.getVisibility() == View.VISIBLE
                                    && !isNetworkAvailable())
                                Toast.makeText(
                                                this,
                                                "Please Connect to the Internet",
                                                Toast.LENGTH_LONG)
                                        .show();
                        },
                        2000);

        checkBackground();

        if ("At 08:30 Daily".equals(sharedPreferenceHelper.getAlarmString()))
            AlarmHelper.setDefaultAlarm(this);
    }

    private void checkBackground() {
        final String backgroundPath = sharedPreferenceHelper.getBackgroundPath();

        final File f = new File(backgroundPath);

        if (!("-1".equals(backgroundPath)) && (f.exists()))
            constraintLayout.setBackground(Drawable.createFromPath(backgroundPath));
        else {
            Toast.makeText(this, "Choose a background", Toast.LENGTH_LONG).show();

            showBackgroundOptionChooser(false);
        }
    }

    private void initFABs() {
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
    }

    private void resetHomeFAB() {
        homeFAB.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_menu));
        homeFAB.setTag(null);
    }

    private boolean isFABMenuHidden() {
        return fontFAB.getVisibility() == View.GONE
                && aboutFAB.getVisibility() == View.GONE
                && settingsFAB.getVisibility() == View.GONE
                && favFAB.getVisibility() == View.GONE
                && colorFAB.getVisibility() == View.GONE
                && bgFAB.getVisibility() == View.GONE;
    }

    private void closeFABMenu() {

        float homeFABX = homeFAB.getX();
        float homeFABY = homeFAB.getY();

        favFAB.animate()
                .x(homeFABX)
                .y(homeFABY)
                .rotation(360)
                .alpha(0f)
                .setInterpolator(new AccelerateInterpolator());
        aboutFAB.animate()
                .x(homeFABX)
                .y(homeFABY)
                .rotation(360)
                .alpha(0f)
                .setInterpolator(new AccelerateInterpolator());
        colorFAB.animate()
                .x(homeFABX)
                .y(homeFABY)
                .rotation(360)
                .alpha(0f)
                .setInterpolator(new AccelerateInterpolator());
        settingsFAB
                .animate()
                .x(homeFABX)
                .y(homeFABY)
                .rotation(360)
                .alpha(0f)
                .setInterpolator(new AccelerateInterpolator());
        bgFAB.animate()
                .x(homeFABX)
                .y(homeFABY)
                .rotation(360)
                .alpha(0f)
                .setInterpolator(new AccelerateInterpolator());
        fontFAB.animate()
                .x(homeFABX)
                .y(homeFABY)
                .rotation(360)
                .alpha(0f)
                .setInterpolator(new AccelerateInterpolator());

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

        favFAB.animate()
                .translationX(DPtoPX(i))
                .rotationBy(360)
                .alpha(1f)
                .setInterpolator(new AccelerateInterpolator());
        aboutFAB.animate()
                .translationX(-DPtoPX(i))
                .rotationBy(360)
                .alpha(1f)
                .setInterpolator(new AccelerateInterpolator());

        colorFAB.animate()
                .translationX(DPtoPX((int) (i * cos36)))
                .translationY(-DPtoPX((int) (i * sin36)))
                .rotationBy(360)
                .alpha(1f)
                .setInterpolator(new AccelerateInterpolator());
        settingsFAB
                .animate()
                .translationX(-DPtoPX((int) (i * cos36)))
                .translationY(-DPtoPX((int) (i * sin36)))
                .rotationBy(360)
                .alpha(1f)
                .setInterpolator(new AccelerateInterpolator());

        bgFAB.animate()
                .translationX(DPtoPX((int) (i * cos72)))
                .translationY(-DPtoPX((int) (i * sin72)))
                .rotationBy(360)
                .alpha(1f)
                .setInterpolator(new AccelerateInterpolator());
        fontFAB.animate()
                .translationX(-DPtoPX((int) (i * cos72)))
                .translationY(-DPtoPX((int) (i * sin72)))
                .rotationBy(360)
                .alpha(1f)
                .setInterpolator(new AccelerateInterpolator());

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

    private int DPtoPX(int DP) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(DP * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private void initAnimations() {
        ScaleAnimation scaleAnimation =
                new ScaleAnimation(
                        0,
                        1f,
                        0,
                        1f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f);
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

                if (constraint.toString().isEmpty()) filteredResults.addAll(allQuotesList);
                else
                    for (Quote quote : allQuotesList) {
                        if (quote.getQuote()
                                        .toLowerCase()
                                        .contains(constraint.toString().toLowerCase())
                                || quote.getAuthor()
                                        .toLowerCase()
                                        .contains(constraint.toString().toLowerCase()))
                            filteredResults.add(quote);
                    }

                FilterResults filterResult = new FilterResults();
                filterResult.values = filteredResults;

                return filterResult;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (adapter == null) return;

                // noinspection unchecked
                adapter.setQuoteList((List<Quote>) results.values);
                adapter.notifyDataSetChanged();
                viewPager.setCurrentItem(0);

                ((TextView) findViewById(R.id.homeSearchCountTV))
                        .setText(
                                (adapter == null
                                                || adapter.getCount()
                                                        == sharedPreferenceHelper
                                                                .getTotalQuotesCount())
                                        ? ""
                                        : String.valueOf(adapter.getCount()));
            }
        };
    }

    private void initViewPager() {
        viewPager = findViewById(R.id.viewPager);

        adapter = new QuoteViewPagerAdapter(getSupportFragmentManager(), new ArrayList<>());

        viewPager.setAdapter(adapter);

        viewModel
                .getQuotes()
                .observe(
                        this,
                        quotes -> {
                            sharedPreferenceHelper.setTotalQuotesCount(quotes.size());

                            allQuotesList = new ArrayList<>(quotes);

                            adapter.setQuoteList(quotes);

                            adapter.notifyDataSetChanged();

                            progressIndicator.setVisibility(View.GONE);
                        });
    }

    private void showShareActionPicker(Quote q) {
        ShareOptionPickFragment.newInstance(q)
                .show(getSupportFragmentManager(), "ShareActionPicker");
    }

    private void showBackgroundOptionChooser(boolean isCancellable) {

        BGOptionPickFragment.newInstance(isCancellable)
                .show(getSupportFragmentManager(), "BackgroundOptionPick");
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission")
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public QuoteViewPagerAdapter getQuoteViewPagerAdapter() {
        return adapter;
    }

    private void addFavourite(Quote quote) {

        long l = new FavRepository(getApplication()).insertFav(quote);

        if (l == -1)
            Toast.makeText(this, "Already Present in Favorites", Toast.LENGTH_SHORT).show();
        else Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show();
    }

    public void updateViewPager() {
        adapter.notifyDataSetChanged();
    }

    public void setConstraintLayoutBackground(Drawable drawable) {
        constraintLayout.setBackground(drawable);
    }

    private void shareRandomQuote() {

        final ProgressDialog progressDialog =
                ProgressDialog.show(MainActivity.this, "", "Please Wait....");
        progressDialog.setCancelable(false);

        new QuotesRepository()
                .getRandomQuote(
                        quote -> {
                            exportHelper.shareImage(MainActivity.this, quote);
                            progressDialog.dismiss();
                        });
    }

    private void shareButtonClicked(Context context, int i, Quote q) {
        if (i == 0) {
            ShareHelper.copyQuote(context, q);
        } else if (i == 1) {
            ShareHelper.shareQuote(context, q);
        } else if (i == 2) {
            ShareHelper.saveQuote(context, q);
        } else if (i == 3) {
            showShareActionPicker(q);
        }
    }

    private class SwipeListener implements View.OnTouchListener {
        GestureDetector gestureDetector;

        SwipeListener(View view) {
            GestureDetector.SimpleOnGestureListener listener =
                    new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onDown(MotionEvent e) {

                            homeFAB.startAnimation(
                                    AnimationUtils.loadAnimation(
                                            MainActivity.this, R.anim.animate));
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
                            return true;
                        }
                    };
            gestureDetector = new GestureDetector(listener);
            view.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }
    }
}
