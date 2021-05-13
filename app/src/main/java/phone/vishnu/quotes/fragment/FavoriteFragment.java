package phone.vishnu.quotes.fragment;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.adapter.FavoritesDataAdapter;
import phone.vishnu.quotes.helper.ExportHelper;
import phone.vishnu.quotes.helper.FavUtils;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;
import phone.vishnu.quotes.model.Quote;

public class FavoriteFragment extends Fragment {

    private final View.OnClickListener viewImageViewOnClickListener;
    private final View.OnClickListener removeImageViewOnClickListener;

    private FavUtils favUtils;
    private ExportHelper exportHelper;
    private ListView listView;
    private ImageView addImageView;

    public FavoriteFragment() {
        viewImageViewOnClickListener = v -> {
            {
                final Animation shake = AnimationUtils.loadAnimation(requireContext(), R.anim.animate);
                v.startAnimation(shake);

                int position = Integer.parseInt(v.getTag().toString());

                shareButtonClicked(new SharedPreferenceHelper(requireContext()).getShareButtonAction(),
                        favUtils.getFavourite(position)
                );
            }
        };
        removeImageViewOnClickListener = v -> {
            {
                final Animation shake = AnimationUtils.loadAnimation(requireContext(), R.anim.animate);
                v.startAnimation(shake);

                int position = Integer.parseInt(v.getTag().toString());

                favUtils.removeFavorite(position);

                initFavourites();
            }
        };
    }

    public static FavoriteFragment newInstance() {
        return new FavoriteFragment();
    }

    private void initFavourites() {
        FavoritesDataAdapter adapter = new FavoritesDataAdapter(requireContext().getApplicationContext(), favUtils.getFavArrayList(), viewImageViewOnClickListener, removeImageViewOnClickListener);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initFavourites();

        addImageView.setOnClickListener(v -> requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.favoriteConstraintLayout, AddNewFragment.newInstance()).commit());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_favorite, container, false);
        listView = inflate.findViewById(R.id.favoriteListView);
        addImageView = inflate.findViewById(R.id.favoriteAddImageView);
        favUtils = new FavUtils(requireContext());
        exportHelper = new ExportHelper(requireContext());
        return inflate;
    }

    private void showPermissionDeniedDialog() {
        final androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Permission Denied");
        builder.setMessage("Please Accept Necessary Permissions");
        builder.setCancelable(true);
        builder.setPositiveButton("OK", (imageDialog, which) -> {
            imageDialog.cancel();
            startActivity(
                    new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            .setData(Uri.fromParts("package", requireContext().getPackageName(), null))
            );
        });
        builder.setNegativeButton("Cancel", (imageDialog, which) -> {
            imageDialog.cancel();
            Toast.makeText(requireContext(), "App requires these permissions to run properly", Toast.LENGTH_SHORT).show();
        });
        builder.show();

    }

    private void shareButtonClicked(int i, Quote q) {

        //Copy -> 0
        //Share -> 1
        //Save -> 2

        if (i == 0) {
            copyQuote(q);
        } else if (i == 1) {
            shareQuote(q);
        } else if (i == 2) {
            saveQuote(q);
        }
    }

    private void copyQuote(Quote quote) {

        String q = "\"" + quote.getQuote() + "\"" + " - " + quote.getAuthor().replace("-", "");

        ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(getResources().getString(R.string.app_name), q);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(requireContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
    }

    private void saveQuote(final Quote q) {

        Dexter.withContext(requireContext())
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Toast.makeText(requireContext(), "Saving to Gallery", Toast.LENGTH_SHORT).show();
                        AsyncTask.execute(() -> exportHelper.saveImage(requireContext(), q));
                    }

                    @Override
                    public void onPermissionDenied(final PermissionDeniedResponse permissionDeniedResponse) {
                        showPermissionDeniedDialog();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        Toast.makeText(requireContext(), "App requires these permissions to share the quote", Toast.LENGTH_SHORT).show();
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();

    }

    private void shareQuote(final Quote q) {
        Dexter.withContext(requireContext())
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        AsyncTask.execute(() -> exportHelper.shareImage(requireContext(), q));
                    }

                    @Override
                    public void onPermissionDenied(final PermissionDeniedResponse permissionDeniedResponse) {
                        showPermissionDeniedDialog();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        Toast.makeText(requireContext(), "App requires these permissions to share the quote", Toast.LENGTH_SHORT).show();
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }

}
