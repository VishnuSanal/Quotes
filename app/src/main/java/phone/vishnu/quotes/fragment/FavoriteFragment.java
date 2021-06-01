package phone.vishnu.quotes.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.adapter.FavoritesDataAdapter;
import phone.vishnu.quotes.helper.FavUtils;
import phone.vishnu.quotes.helper.ShareHelper;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;
import phone.vishnu.quotes.model.Quote;

public class FavoriteFragment extends Fragment {

    private final View.OnClickListener viewImageViewOnClickListener;
    private final View.OnClickListener removeImageViewOnClickListener;

    private FavUtils favUtils;
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
        return inflate;
    }

    private void shareButtonClicked(int i, Quote q) {
        if (i == 0) {
            ShareHelper.copyQuote(requireContext(), q);
        } else if (i == 1) {
            ShareHelper.shareQuote(requireContext(), q);
        } else if (i == 2) {
            ShareHelper.saveQuote(requireContext(), q);
        } else if (i == 3) {
            showBottomSheetDialog(q);
        }
    }

    private void showBottomSheetDialog(Quote q) {
        BottomSheetFragment bottomSheet = BottomSheetFragment.newInstance(q);
        bottomSheet.show(requireActivity().getSupportFragmentManager(), "ModalBottomSheet");
    }
}
