package phone.vishnu.quotes.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.turkialkhateeb.materialcolorpicker.ColorChooserDialog;
import com.turkialkhateeb.materialcolorpicker.ColorListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.data.QuoteData;
import phone.vishnu.quotes.data.QuoteListAsyncResponse;
import phone.vishnu.quotes.data.QuoteViewPagerAdapter;
import phone.vishnu.quotes.fragment.BlankFragment;
import phone.vishnu.quotes.fragment.BottomSheetFragment;
import phone.vishnu.quotes.fragment.FavoriteFragment;
import phone.vishnu.quotes.fragment.PickFragment;
import phone.vishnu.quotes.fragment.QuoteFragment;
import phone.vishnu.quotes.model.Quote;
import phone.vishnu.quotes.receiver.NotificationReceiver;

public class MainActivity extends AppCompatActivity implements BottomSheetFragment.BottomSheetListener {
    private static final int PICK_IMAGE_ID = 36;
    public static ProgressDialog bgDialog;
    private final String BACKGROUND_PREFERENCE_NAME = "backgroundPreference";
    private final int PERMISSION_REQ_CODE = 88;
    private ConstraintLayout constraintLayout;
    private ViewPager viewPager;
    private QuoteViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            final Bundle extras = getIntent().getExtras();
            if (extras != null && extras.getBoolean("NotificationClick")) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            shareScreenshot(extras.getString("quote"), extras.getString("author"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        }

        setContentView(R.layout.activity_main);
        constraintLayout = findViewById(R.id.constraintLayout);
        viewPager = findViewById(R.id.viewPager);

        if (!isNetworkAvailable())
            Toast.makeText(this, "Please Connect to the Internet...", Toast.LENGTH_SHORT).show();

        ImageView menuIcon = findViewById(R.id.homeMenuIcon);

        String backgroundPath = this.getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE).getString(BACKGROUND_PREFERENCE_NAME, "-1");
        if (!"-1".equals(backgroundPath))
            constraintLayout.setBackground(Drawable.createFromPath(backgroundPath));
        else {

            if (isNetworkAvailable()) {

                final ProgressDialog dialog = ProgressDialog.show(this, "", "Please Wait....");
                String fileName = ("aerial-photography-of-buildings-near-sea-3560024-min.jpg");

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images").child(fileName);

                final File localFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Quotes");

                final File f = new File(localFile + File.separator + ".Quotes_Background" + ".jpg");

                storageReference.getFile(f).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        SharedPreferences.Editor editor = MainActivity.this.getSharedPreferences("phone.vishnu.quotes.sharedPreferences", Context.MODE_PRIVATE).edit();
                        String BACKGROUND_PREFERENCE_NAME = "backgroundPreference";
                        editor.putString(BACKGROUND_PREFERENCE_NAME, f.toString());
                        editor.apply();
                        dialog.dismiss();

                        Toast.makeText(MainActivity.this, "Background Set.....", Toast.LENGTH_SHORT).show();
                        constraintLayout.setBackground(Drawable.createFromPath(f.toString()));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        exception.printStackTrace();
                        Toast.makeText(MainActivity.this, "Error.....", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            } else {

                Toast.makeText(this, "No Internet Connection. You can set a colour as background", Toast.LENGTH_SHORT).show();

                ColorChooserDialog colorChooserDialog = new ColorChooserDialog(MainActivity.this);
                colorChooserDialog.setTitle("Choose Color");
                colorChooserDialog.setColorListener(new ColorListener() {
                    @Override
                    public void OnColorClick(View v, int color) {

                        DisplayMetrics metrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(metrics);

                        Bitmap image = Bitmap.createBitmap(metrics.widthPixels, metrics.heightPixels, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(image);
                        canvas.drawColor(color);

                        String file = generateNoteOnSD(image);

                        constraintLayout.setBackground(Drawable.createFromPath(file));

                        SharedPreferences sharedPrefs = MainActivity.this.getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPrefs.edit();
                        editor.putString(BACKGROUND_PREFERENCE_NAME, file);
                        editor.apply();

                    }
                });
                colorChooserDialog.show();


            }
        }
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetFragment bottomSheet = new BottomSheetFragment();
                bottomSheet.show(getSupportFragmentManager(), "bottomSheetTag");
            }
        });

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                adapter = new QuoteViewPagerAdapter(getSupportFragmentManager(), getFragments());
                viewPager.setAdapter(adapter);
            }
        });

        String ALARM_PREFERENCE_TIME = "customAlarmPreference";

        String s = getApplicationContext().getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE)
                .getString(ALARM_PREFERENCE_TIME, "At 08:30 Daily");

        if ("At 08:30 Daily".equals(s)) {
            myAlarm();
        }
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

    private List<Fragment> getFragments() {

        final List<Fragment> fragments = new ArrayList<>();
        new QuoteData().getQuotes(new QuoteListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Quote> quotes) {

                Collections.shuffle(quotes);

                for (int i = 0; i < quotes.size(); i++) {
                    QuoteFragment quoteFragment = QuoteFragment.newInstance(quotes.get(i).getQuote(), quotes.get(i).getAuthor());
                    fragments.add(quoteFragment);
                }

                adapter.notifyDataSetChanged();
            }
        });
        return fragments;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == PICK_IMAGE_ID) && (resultCode == Activity.RESULT_OK)) {
            if (data != null) {
                try {

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

                    String file = generateNoteOnSD(bitmap);

                    constraintLayout.setBackground(Drawable.createFromPath(file));

                    SharedPreferences sharedPrefs = this.getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putString(BACKGROUND_PREFERENCE_NAME, file);
                    editor.apply();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String generateNoteOnSD(Bitmap image) {
        File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Quotes");

        if (!root.exists()) root.mkdirs();

        String file = root.toString() + File.separator + ".Quotes_Background" + ".jpg";

        try {
            FileOutputStream fOutputStream = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fOutputStream);

            image.compress(Bitmap.CompressFormat.JPEG, 100, bos);

            fOutputStream.flush();
            fOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

//        MediaScannerConnection.scanFile(context, new String[]{file}, null, null);
        return file;
    }

    @Override
    public void onBottomSheetButtonClicked(int id) {
        switch (id) {
            case R.id.bottomSheetFav: {
                FavoriteFragment fragment = FavoriteFragment.newInstance();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.constraintLayout, fragment)
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.bottomSheetAbout: {
                BlankFragment fragment = BlankFragment.newInstance();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.constraintLayout, fragment)
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.bottomSheetImageChooser: {

                if (!isPermissionGranted())
                    isPermissionGranted();
                else {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this, R.style.AlertDialogTheme);

                    final String[] items = {"Plain Colour", "Image From Gallery", "Default Images"};
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0: {
                                    ColorChooserDialog colorChooserDialog = new ColorChooserDialog(MainActivity.this);
                                    colorChooserDialog.setTitle("Choose Color");
                                    colorChooserDialog.setColorListener(new ColorListener() {
                                        @Override
                                        public void OnColorClick(View v, int color) {

                                            DisplayMetrics metrics = new DisplayMetrics();
                                            getWindowManager().getDefaultDisplay().getMetrics(metrics);

                                            Bitmap image = Bitmap.createBitmap(metrics.widthPixels, metrics.heightPixels, Bitmap.Config.ARGB_8888);
                                            Canvas canvas = new Canvas(image);
                                            canvas.drawColor(color);

                                            String file = generateNoteOnSD(image);

                                            constraintLayout.setBackground(Drawable.createFromPath(file));

                                            SharedPreferences sharedPrefs = MainActivity.this.getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPrefs.edit();
                                            editor.putString(BACKGROUND_PREFERENCE_NAME, file);
                                            editor.apply();

                                        }
                                    });
                                    colorChooserDialog.show();
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
                break;
            }
            case R.id.bottomSheetColorChooser: {
                final String COLOR_PREFERENCE_NAME = "colorPreference";

                final SharedPreferences prefs = MainActivity.this.getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE);

                ColorChooserDialog dialog = new ColorChooserDialog(MainActivity.this);
                dialog.setTitle("Choose Color");
                dialog.setColorListener(new ColorListener() {
                    @Override
                    public void OnColorClick(View v, int color) {

                        String colorString = Integer.toHexString(color).substring(2);

                        //TODO:Needs Fixing of string "WHITE"
                        if (colorString.toLowerCase().equals("ffffff")) colorString = "00000000";

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(COLOR_PREFERENCE_NAME, "#" + colorString);
                        editor.apply();
                        Toast.makeText(MainActivity.this, "Accent Colour Set..... \n Applying Changes", Toast.LENGTH_LONG).show();
                        MainActivity.this.recreate();
                    }
                });
                dialog.show();
                break;
            }
        }
    }

    private void showPermissionDeniedDialog() {

        final AlertDialog.Builder builder =
                new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
        builder.setTitle("Permission Denied");
        builder.setMessage("Please Accept Permission to Capture Screenshot of the Screen");
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQ_CODE);
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

    private boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 22) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showPermissionDeniedDialog();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQ_CODE);
                }
            } else {
                return true;
            }
        }
        return false;
    }

    private void shareScreenshot(String quote, String author) {

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @SuppressLint("InflateParams") View shareView = inflater.inflate(R.layout.share_layout, null);

        SharedPreferences sharedPreferences = this.getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE);
        String hexColor = sharedPreferences.getString("colorPreference", "#5C5C5C");

        String backgroundPath = sharedPreferences.getString("backgroundPreference", "-1");
        if (!"-1".equals(backgroundPath))
            shareView.findViewById(R.id.shareRelativeLayout).setBackground(Drawable.createFromPath(backgroundPath));

        CardView cardView = shareView.findViewById(R.id.shareCardView);
        cardView.setCardBackgroundColor(Color.parseColor(hexColor));

//        ((ImageView) shareView.findViewById(R.id.shareFavoriteImageView)).setColorFilter(Color.RED);
//        ((ImageView) shareView.findViewById(R.id.shareShareImageView)).setColorFilter(Color.GREEN);

        ((TextView) shareView.findViewById(R.id.shareQuoteTextView)).setText(quote);
        ((TextView) shareView.findViewById(R.id.shareAuthorTextView)).setText(author);

        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);

        shareView.measure(View.MeasureSpec.makeMeasureSpec(metrics.widthPixels, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(metrics.heightPixels, View.MeasureSpec.EXACTLY));

        shareView.findViewById(R.id.shareRelativeLayout).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ConstraintLayout.LayoutParams cardParams = new ConstraintLayout.LayoutParams(300, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardParams.verticalBias = 0.5f;
        cardParams.horizontalBias = 0.5f;

        shareView.setDrawingCacheEnabled(true);

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

        Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", new File(imagePath));
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image/*");
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
