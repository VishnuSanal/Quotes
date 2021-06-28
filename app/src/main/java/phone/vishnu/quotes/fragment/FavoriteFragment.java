package phone.vishnu.quotes.fragment;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.adapter.FavoritesAdapter;
import phone.vishnu.quotes.helper.ShareHelper;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;
import phone.vishnu.quotes.model.Quote;
import phone.vishnu.quotes.viewmodel.FavViewModel;

public class FavoriteFragment extends BottomSheetDialogFragment {

    private SharedPreferenceHelper sharedPreferenceHelper;

    private FavViewModel viewModel;
    private FavoritesAdapter adapter;
    private RecyclerView recyclerView;

    private ImageView emptyHintIV;
    private TextView emptyHintTV, addTV;
    private CircularProgressIndicator progressBar;

    public FavoriteFragment() {
    }

    public static FavoriteFragment newInstance() {
        return new FavoriteFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_favorite, container, false);

        progressBar = inflate.findViewById(R.id.favProgressBar);
        emptyHintIV = inflate.findViewById(R.id.recyclerViewEmptyHintIV);
        emptyHintTV = inflate.findViewById(R.id.recyclerViewEmptyHintTV);
        addTV = inflate.findViewById(R.id.favAddTV);
        recyclerView = inflate.findViewById(R.id.favoriteRecyclerView);

        sharedPreferenceHelper = new SharedPreferenceHelper(requireContext());

        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpRecyclerView(requireContext());

        importFavourites();

        addTV.setOnClickListener(v ->
                AddNewBottomSheetDialogFragment.newInstance()
                        .show(
                                requireActivity().getSupportFragmentManager(),
                                "AddNewBottomSheetDialogFragment"
                        )
        );
    }

    private void importFavourites() {

        String favArrayListString = sharedPreferenceHelper.getFavoriteArrayString();

        if (favArrayListString != null) {

            ProgressDialog progressDialog = new ProgressDialog(requireContext(), R.style.DialogTheme);

            progressDialog.setMessage("Please Wait....");
            progressDialog.show();
            progressDialog.setCancelable(false);

            ArrayList<Quote> arrayList = new Gson().fromJson(favArrayListString, new TypeToken<ArrayList<Quote>>() {
            }.getType());

            for (Quote quote : arrayList) {
                viewModel.insert(quote);
                adapter.notifyDataSetChanged();
            }
            progressDialog.dismiss();

            sharedPreferenceHelper.deleteFavPreference();
        }
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

                if (sharedPreferenceHelper.getFavHintShownCount() < 2) {
                    Toast.makeText(requireContext(), "Swipe Right to Delete\nSwipe Left to Share", Toast.LENGTH_LONG).show();
                    sharedPreferenceHelper.incrementFavHintShownCount();
                }

            }

            if (progressBar.getVisibility() == View.VISIBLE)
                progressBar.setVisibility(View.GONE);

            adapter.submitList(favList);
            recyclerView.requestLayout();
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
        ShareActionPickBottomSheetDialogFragment bottomSheet = ShareActionPickBottomSheetDialogFragment.newInstance(q);
        bottomSheet.show(requireActivity().getSupportFragmentManager(), "ModalBottomSheet");
    }

}
