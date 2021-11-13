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

package phone.vishnu.quotes.fragment;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import java.util.ArrayList;
import java.util.List;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.adapter.FavoritesRVAdapter;
import phone.vishnu.quotes.helper.ShareHelper;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;
import phone.vishnu.quotes.model.Quote;
import phone.vishnu.quotes.viewmodel.FavViewModel;

public class FavoriteFragment extends BottomSheetDialogFragment {

    private SharedPreferenceHelper sharedPreferenceHelper;

    private ArrayList<Quote> favArrayList;

    private FavViewModel viewModel;
    private FavoritesRVAdapter adapter;
    private RecyclerView recyclerView;

    private ImageView emptyHintIV;
    private TextView emptyHintTV, addTV, countTV;
    private LinearProgressIndicator progressBar;
    private CoordinatorLayout coordinatorLayout;

    private TextInputEditText textInputEditText;
    private TextInputLayout textInputLayout;

    private ChipGroup chipGroup;

    public FavoriteFragment() {}

    public static FavoriteFragment newInstance() {
        return new FavoriteFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_favorite, container, false);

        progressBar = inflate.findViewById(R.id.favProgressBar);
        emptyHintIV = inflate.findViewById(R.id.recyclerViewEmptyHintIV);
        emptyHintTV = inflate.findViewById(R.id.recyclerViewEmptyHintTV);
        addTV = inflate.findViewById(R.id.favAddTV);
        countTV = inflate.findViewById(R.id.favCountTV);
        recyclerView = inflate.findViewById(R.id.favoriteRecyclerView);
        chipGroup = inflate.findViewById(R.id.favChipGroup);
        coordinatorLayout = inflate.findViewById(R.id.favCoordinatorLayout);
        textInputEditText = inflate.findViewById(R.id.favSearchTIE);
        textInputLayout = inflate.findViewById(R.id.favSearchTIL);

        sharedPreferenceHelper = new SharedPreferenceHelper(requireContext());

        chipGroup.requestFocus();

        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpRecyclerView(requireContext());
        setUpChipGroup();

        hideKeyboard(view);

        importFavourites();

        addTV.setOnClickListener(
                v -> {
                    AddNewFragment.newInstance()
                            .show(requireActivity().getSupportFragmentManager(), "AddNewFragment");

                    chipGroup.clearCheck();
                });

        textInputEditText.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        getFilter().filter(s);

                        chipGroup.clearCheck();

                        for (int i = 0; i < chipGroup.getChildCount(); i++)
                            chipGroup
                                    .getChildAt(i)
                                    .setEnabled(
                                            !((textInputEditText.getText() == null)
                                                    || (!textInputEditText
                                                            .getText()
                                                            .toString()
                                                            .equals(""))));
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });

        textInputLayout.setEndIconOnClickListener(
                v -> {
                    textInputEditText.setText("");
                    hideKeyboard(v);
                    chipGroup.requestFocus();
                });

        textInputLayout.setOnFocusChangeListener(
                (v, hasFocus) -> {
                    if (!hasFocus) {
                        hideKeyboard(v);
                        chipGroup.requestFocus();
                    }
                });
    }

    private Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                ArrayList<Quote> filteredResults = new ArrayList<>();

                if (constraint.toString().isEmpty()) filteredResults.addAll(favArrayList);
                else
                    for (Quote quote : favArrayList) {
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
                // noinspection unchecked
                submitList((List<Quote>) results.values);
            }
        };
    }

    private void importFavourites() {

        String favArrayListString = sharedPreferenceHelper.getFavoriteArrayString();

        if (favArrayListString != null) {

            ProgressDialog progressDialog =
                    new ProgressDialog(requireContext(), R.style.DialogTheme);

            progressDialog.setMessage("Please Wait....");
            progressDialog.show();
            progressDialog.setCancelable(false);

            ArrayList<Quote> arrayList =
                    new Gson()
                            .fromJson(
                                    favArrayListString,
                                    new TypeToken<ArrayList<Quote>>() {}.getType());

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
        recyclerView.addItemDecoration(
                new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));

        adapter = new FavoritesRVAdapter();
        recyclerView.setAdapter(adapter);

        viewModel =
                new ViewModelProvider(
                                this,
                                new ViewModelProvider.AndroidViewModelFactory(
                                        (Application) context.getApplicationContext()))
                        .get(FavViewModel.class);

        viewModel
                .getAllFav()
                .observe(
                        this,
                        favList -> {
                            if (sharedPreferenceHelper.getFavHintShownCount() < 2
                                    && !favList.isEmpty()) {
                                Toast.makeText(
                                                requireContext(),
                                                sharedPreferenceHelper.isFavActionReversed()
                                                        ? "Swipe Right to Share\nSwipe Left to Delete"
                                                        : "Swipe Right to Delete\nSwipe Left to Share",
                                                Toast.LENGTH_LONG)
                                        .show();
                                sharedPreferenceHelper.incrementFavHintShownCount();
                            }

                            submitList(favList);

                            favArrayList = new ArrayList<>(favList);
                        });

        boolean isFavActionReversed = sharedPreferenceHelper.isFavActionReversed();

        new ItemTouchHelper(
                        new ItemTouchHelper.SimpleCallback(
                                0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
                            @Override
                            public void onChildDraw(
                                    @NonNull Canvas c,
                                    @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX,
                                    float dY,
                                    int actionState,
                                    boolean isCurrentlyActive) {

                                new RecyclerViewSwipeDecorator.Builder(
                                                c,
                                                recyclerView,
                                                viewHolder,
                                                dX,
                                                dY,
                                                actionState,
                                                isCurrentlyActive)
                                        .addSwipeLeftBackgroundColor(
                                                getResources()
                                                        .getColor(
                                                                isFavActionReversed
                                                                        ? R.color.favRedColor
                                                                        : R.color.favGreenColor))
                                        .addSwipeRightBackgroundColor(
                                                getResources()
                                                        .getColor(
                                                                isFavActionReversed
                                                                        ? R.color.favGreenColor
                                                                        : R.color.favRedColor))
                                        .addSwipeLeftActionIcon(
                                                isFavActionReversed
                                                        ? R.drawable.ic_delete
                                                        : R.drawable.ic_share)
                                        .addSwipeRightActionIcon(
                                                isFavActionReversed
                                                        ? R.drawable.ic_share
                                                        : R.drawable.ic_delete)
                                        .create()
                                        .decorate();

                                super.onChildDraw(
                                        c,
                                        recyclerView,
                                        viewHolder,
                                        dX,
                                        dY,
                                        actionState,
                                        isCurrentlyActive);
                            }

                            @Override
                            public boolean onMove(
                                    @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    @NonNull RecyclerView.ViewHolder target) {
                                return false;
                            }

                            @Override
                            public void onSwiped(
                                    @NonNull final RecyclerView.ViewHolder viewHolder,
                                    int direction) {

                                if ((direction == ItemTouchHelper.LEFT && !isFavActionReversed)
                                        || (direction == ItemTouchHelper.RIGHT
                                                && isFavActionReversed)) {
                                    shareButtonClicked(
                                            new SharedPreferenceHelper(requireContext())
                                                    .getShareButtonAction(),
                                            adapter.getFav(viewHolder.getAdapterPosition()));

                                    new Handler()
                                            .postDelayed(
                                                    () ->
                                                            adapter.notifyItemChanged(
                                                                    viewHolder
                                                                            .getAdapterPosition()),
                                                    100);

                                } else {

                                    chipGroup.clearCheck();

                                    showUndoSnackBar(
                                            adapter.getFav(viewHolder.getAdapterPosition()));

                                    viewModel.delete(
                                            adapter.getFav(viewHolder.getAdapterPosition()));
                                    adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                                    adapter.notifyItemRangeChanged(
                                            viewHolder.getAdapterPosition(),
                                            adapter.getItemCount());
                                }
                            }
                        })
                .attachToRecyclerView(recyclerView);
    }

    private void showUndoSnackBar(Quote q) {
        Snackbar snackbar =
                Snackbar.make(coordinatorLayout, "Removed from Favorites", Snackbar.LENGTH_SHORT);

        snackbar.setAction("Undo", v -> viewModel.insert(q));
        snackbar.show();
    }

    private void setUpChipGroup() {
        String[] tags = {"All", "Default", "Custom"};

        for (String string : tags) {

            Chip chip =
                    new Chip(new ContextThemeWrapper(chipGroup.getContext(), R.style.ChipStyle));

            chip.setText(string);
            chip.setLetterSpacing(0.15f);

            chip.setCheckable(true);
            chip.setCheckedIconVisible(true);

            chipGroup.addView(chip);

            chipGroup.setOnCheckedChangeListener(
                    (group, checkedId) ->
                            filterItems(
                                    (checkedId == View.NO_ID
                                                    || ((Chip) chipGroup.findViewById(checkedId))
                                                            .getText()
                                                            .equals("All"))
                                            ? ""
                                            : ((Chip) chipGroup.findViewById(checkedId))
                                                    .getText()));
        }
    }

    private void filterItems(CharSequence text) {

        ArrayList<Quote> filteredResults = new ArrayList<>();

        if (text == "") {
            submitList(favArrayList);
            return;
        }

        for (Quote quote : favArrayList)
            if ((text.equals("Default") && !quote.isUserAdded())
                    || (text.equals("Custom") && quote.isUserAdded())) filteredResults.add(quote);

        submitList(filteredResults);
    }

    private void submitList(List<Quote> arrayList) {

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        adapter.submitList(
                arrayList,
                () -> {
                    recyclerView.requestLayout();

                    new Handler()
                            .postDelayed(
                                    () -> {
                                        if (progressBar != null
                                                && progressBar.getVisibility() == View.VISIBLE)
                                            progressBar.setVisibility(View.GONE);
                                    },
                                    200);

                    if (adapter.getItemCount() == 0) {
                        emptyHintIV.setVisibility(View.VISIBLE);
                        emptyHintTV.setVisibility(View.VISIBLE);
                    } else {
                        emptyHintIV.setVisibility(View.GONE);
                        emptyHintTV.setVisibility(View.GONE);
                    }

                    countTV.setText(String.valueOf(adapter.getItemCount()));
                });
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
        ShareOptionPickFragment bottomSheet = ShareOptionPickFragment.newInstance(q);
        bottomSheet.show(requireActivity().getSupportFragmentManager(), "ModalBottomSheet");
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager)
                        requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
