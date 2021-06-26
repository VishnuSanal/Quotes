package phone.vishnu.quotes.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.MainActivity;
import phone.vishnu.quotes.adapter.ColorAdapter;
import phone.vishnu.quotes.helper.ExportHelper;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;

public class ColorPickFragment extends BottomSheetDialogFragment {

    private ColorAdapter colorAdapter;
    private RecyclerView recyclerView;

    public ColorPickFragment() {
    }

    public static ColorPickFragment newInstance(int COLOR_REQ_CODE) {
        Bundle args = new Bundle();
        args.putInt("ColorRequestCode", COLOR_REQ_CODE);
        ColorPickFragment fragment = new ColorPickFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View i = inflater.inflate(R.layout.fragment_color_pick, container, false);

        recyclerView = i.findViewById(R.id.colorPickRecyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));

        colorAdapter = new ColorAdapter();

        colorAdapter.submitList(getColorList());

        recyclerView.setAdapter(colorAdapter);

        return i;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final int colorRequestCode = Objects.requireNonNull(getArguments()).getInt("ColorRequestCode");

        final SharedPreferenceHelper sharedPreferenceHelper = new SharedPreferenceHelper(requireContext());

        colorAdapter.setOnItemClickListener(colorString -> {

            if (colorRequestCode == 0) {

                DisplayMetrics metrics = new DisplayMetrics();
                metrics.widthPixels = 1080;
                metrics.heightPixels = 1920;

                Bitmap image = Bitmap.createBitmap(metrics.widthPixels, metrics.heightPixels, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(image);
                canvas.drawColor(Color.parseColor(colorString));

                new ExportHelper(requireContext()).exportBackgroundImage(image);

                ((MainActivity) requireActivity()).setConstraintLayoutBackground(Drawable.createFromPath(sharedPreferenceHelper.getBackgroundPath()));

                dismiss();

            } else if (colorRequestCode == 1) {

                sharedPreferenceHelper.setColorPreference(colorString);

                ((MainActivity) requireActivity()).updateViewPager();

                dismiss();
            } else if (colorRequestCode == 2) {

                if (colorString.equals("#00000000")) colorString = "#FFFFFF";

                sharedPreferenceHelper.setFontColorPreference(colorString);

                ((MainActivity) requireActivity()).updateViewPager();

                dismiss();
            }

        });

    }

    private List<String> getColorList() {
        return Arrays.asList(
                "#FFF44336",
                "#FFE91E63",
                "#FF9C27B0",
                "#FF673AB7",
                "#FF3F51B5",
                "#FF2196F3",
                "#FF03A9F4",
                "#FF00BCD4",
                "#FF009688",
                "#FF4CAF50",
                "#FF8BC34A",
                "#FFCDDC39",
                "#FFFFEB3B",
                "#FFFFC107",
                "#FFFF9800",
                "#FFFF5722",
                "#FF795548",
                "#FF9E9E9E",
                "#FF607D8B",
                "#FF000000",
                "#00000000"
        );
    }
}