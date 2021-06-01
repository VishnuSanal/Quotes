package phone.vishnu.quotes.fragment;

import android.Manifest;
import android.animation.Animator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.MainActivity;
import phone.vishnu.quotes.helper.ExportHelper;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;
import phone.vishnu.quotes.model.Quote;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private static final String QUOTE_EXTRA = "quote";
    private static final String AUTHOR_EXTRA = "author";

    private RadioGroup radioGroup;
    private SharedPreferenceHelper sharedPreferenceHelper;
    private ExportHelper exportHelper;

    private Quote quote = null;

    public static BottomSheetFragment newInstance() {
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();

        bottomSheetFragment.setArguments(null);

        return bottomSheetFragment;
    }

    public static BottomSheetFragment newInstance(Quote q) {
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();

        Bundle bundle = new Bundle();

        bundle.putString(QUOTE_EXTRA, q.getQuote());
        bundle.putString(AUTHOR_EXTRA, q.getAuthor());

        bottomSheetFragment.setArguments(bundle);

        return bottomSheetFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);

        if (getArguments() != null)
            if (getArguments().containsKey(QUOTE_EXTRA) && getArguments().containsKey(QUOTE_EXTRA)) {
                quote = new Quote(getArguments().getString(QUOTE_EXTRA), getArguments().getString(AUTHOR_EXTRA));
                Log.e("vishnu", "newInstance:" + quote.toString());
            }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.bottom_sheet, container, false);

        sharedPreferenceHelper = new SharedPreferenceHelper(requireContext());
        exportHelper = new ExportHelper(requireContext());

        radioGroup = inflate.findViewById(R.id.bottomSheetRadioGroup);

        if (quote != null)
            ((RadioButton) radioGroup.findViewById(R.id.bottomSheetAskRadioButton)).setVisibility(View.GONE);

        setChecked(sharedPreferenceHelper.getShareButtonAction());

        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        radioGroup.setOnCheckedChangeListener((group, id) -> {

            if (quote != null)
                shareButtonClicked(getInt(id), quote);
            else
                sharedPreferenceHelper.setShareButtonAction(getInt(id));

            ((MainActivity) requireActivity()).updateViewPager();

            ((ImageView) view.findViewById(R.id.bottomSheetDoneIndicatorIV))
                    .animate()
                    .alpha(1)
                    .rotation(360)
                    .translationY(70 * (getInt(id) + 1))
                    .setListener(new Animator.AnimatorListener() {

                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            dismiss();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });

        });
    }

    private void shareButtonClicked(int i, Quote q) {

        //Copy -> 0
        //Share -> 1
        //Save -> 2
        //Ask -> 3

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

    private void setChecked(int i) {
        if (i == 0) {
            ((RadioButton) radioGroup.findViewById(R.id.bottomSheetCopyRadioButton)).setChecked(true);
        } else if (i == 1) {
            ((RadioButton) radioGroup.findViewById(R.id.bottomSheetShareRadioButton)).setChecked(true);
        } else if (i == 2) {
            ((RadioButton) radioGroup.findViewById(R.id.bottomSheetSaveRadioButton)).setChecked(true);
        } else if (i == 3) {
            ((RadioButton) radioGroup.findViewById(R.id.bottomSheetAskRadioButton)).setChecked(true);
        }
    }

    private int getInt(int id) {
        //Copy -> 0
        //Share -> 1
        //Save -> 2
        //Askqf -> 3

        if (id == R.id.bottomSheetCopyRadioButton)
            return 0;
        else if (id == R.id.bottomSheetShareRadioButton)
            return 1;
        else if (id == R.id.bottomSheetSaveRadioButton)
            return 2;
        else if (id == R.id.bottomSheetAskRadioButton)
            return 3;
        return 1;
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
}