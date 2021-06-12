package phone.vishnu.quotes.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Application;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.adapter.FavoritesAdapter;
import phone.vishnu.quotes.helper.ShareHelper;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;
import phone.vishnu.quotes.model.Quote;
import phone.vishnu.quotes.viewmodel.FavViewModel;

public class FavoriteFragment extends Fragment {

    private FavViewModel viewModel;
    private FavoritesAdapter adapter;
    private RecyclerView recyclerView;

    private ImageView addImageView, emptyHintIV;
    private TextView emptyHintTV;
    private LinearProgressIndicator progressBar;

    public FavoriteFragment() {
    }

    public static FavoriteFragment newInstance() {
        return new FavoriteFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_favorite, container, false);

        addImageView = inflate.findViewById(R.id.favoriteAddImageView);
        progressBar = inflate.findViewById(R.id.favProgressBar);
        emptyHintIV = inflate.findViewById(R.id.recyclerViewEmptyHintIV);
        emptyHintTV = inflate.findViewById(R.id.recyclerViewEmptyHintTV);
        recyclerView = inflate.findViewById(R.id.favoriteRecyclerView);

        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpRecyclerView(requireContext());

        addImageView.setOnClickListener(
                v -> requireActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.favoriteConstraintLayout, AddNewFragment.newInstance())
                        .commit()
        );

    }

    private void setUpRecyclerView(final Context context) {

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        recyclerView.setHasFixedSize(true);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));

        adapter = new FavoritesAdapter();
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(
                this,
                new ViewModelProvider
                        .AndroidViewModelFactory(
                        (Application) context.getApplicationContext()
                )
        ).get(FavViewModel.class);

        viewModel.getAllFav().observe(this, favList -> {

            if (favList.size() == 0) {
                emptyHintIV.setVisibility(View.VISIBLE);
                emptyHintTV.setVisibility(View.VISIBLE);
            } else {
                emptyHintIV.setVisibility(View.GONE);
                emptyHintTV.setVisibility(View.GONE);

                SharedPreferenceHelper sharedPreferenceHelper = new SharedPreferenceHelper(requireContext());

                if (sharedPreferenceHelper.getFavHintShownCount() < 2) {
                    Toast.makeText(requireContext(), "Swipe Right to Delete\nSwipe Left to Share", Toast.LENGTH_LONG).show();
                    sharedPreferenceHelper.incrementFavHintShownCount();
                }

            }

            if (progressBar.getVisibility() == View.VISIBLE)
                progressBar.animate().translationY(DPtoPX(-8)).alpha(0).setDuration(200).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        progressBar.setVisibility(View.GONE);
                    }
                });

            adapter.submitList(favList);//TODO: Find a way to reverse the list :)
        });

        new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
                    @Override
                    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                            @NonNull RecyclerView.ViewHolder viewHolder,
                                            float dX, float dY,
                                            int actionState, boolean isCurrentlyActive) {

                        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                                .addSwipeLeftBackgroundColor(Color.GREEN)
                                .addSwipeRightBackgroundColor(Color.RED)
                                .addSwipeLeftActionIcon(R.drawable.ic_share)
                                .addSwipeRightActionIcon(R.drawable.ic_delete)
                                .create()
                                .decorate();

                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {

                        if (direction == ItemTouchHelper.LEFT) {
                            shareButtonClicked(new SharedPreferenceHelper(requireContext()).getShareButtonAction(),
                                    adapter.getFav(viewHolder.getAdapterPosition()));

                            new Handler().postDelayed(() -> adapter.notifyItemChanged(viewHolder.getAdapterPosition()), 100);

                        } else {
                            viewModel.delete(adapter.getFav(viewHolder.getAdapterPosition()));
                            adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                            adapter.notifyItemRangeChanged(viewHolder.getAdapterPosition(), adapter.getItemCount());
                        }
                    }
                }
        ).attachToRecyclerView(recyclerView);
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

    @SuppressWarnings("SameParameterValue")
    private int DPtoPX(int DP) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(DP * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
