/*
 * Copyright (C) 2019 - 2022 Vishnu Sanal. T
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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.Objects;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.MainActivity;
import phone.vishnu.quotes.helper.ShareHelper;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;
import phone.vishnu.quotes.model.Quote;

public class ShareOptionPickFragment extends BaseBottomSheetDialogFragment {

    private static final String QUOTE_EXTRA = "quote";
    private static final String AUTHOR_EXTRA = "author";

    private RadioGroup radioGroup;
    private SharedPreferenceHelper sharedPreferenceHelper;

    private Quote quote = null;

    public static ShareOptionPickFragment newInstance() {
        ShareOptionPickFragment bottomSheetFragment = new ShareOptionPickFragment();

        bottomSheetFragment.setArguments(null);

        return bottomSheetFragment;
    }

    public static ShareOptionPickFragment newInstance(Quote q) {
        ShareOptionPickFragment bottomSheetFragment = new ShareOptionPickFragment();

        Bundle bundle = new Bundle();

        bundle.putString(QUOTE_EXTRA, q.getQuote());
        bundle.putString(AUTHOR_EXTRA, q.getAuthor());

        bottomSheetFragment.setArguments(bundle);

        return bottomSheetFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
            if (getArguments().containsKey(QUOTE_EXTRA)
                    && getArguments().containsKey(QUOTE_EXTRA)) {
                quote =
                        new Quote(
                                Objects.requireNonNull(getArguments().getString(QUOTE_EXTRA)),
                                Objects.requireNonNull(getArguments().getString(AUTHOR_EXTRA)));
            }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.share_action_option_pick_dialog, container, false);

        sharedPreferenceHelper = new SharedPreferenceHelper(requireContext());

        radioGroup = inflate.findViewById(R.id.bottomSheetRadioGroup);

        if (quote != null)
            radioGroup.findViewById(R.id.bottomSheetAskRadioButton).setVisibility(View.GONE);

        if (quote == null) setChecked(sharedPreferenceHelper.getShareButtonAction());

        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        radioGroup.setOnCheckedChangeListener(
                (group, id) -> {
                    if (quote != null) shareButtonClicked(getInt(id), quote);
                    else sharedPreferenceHelper.setShareButtonAction(getInt(id));

                    ((MainActivity) requireActivity()).updateViewPager();

                    view.findViewById(R.id.bottomSheetDoneIndicatorIV)
                            .animate()
                            .alpha(1)
                            .rotation(360)
                            .translationY(70 * (getInt(id) + 1))
                            .setListener(
                                    new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            dismiss();
                                        }
                                    });
                });
    }

    private void shareButtonClicked(int i, Quote q) {
        if (i == 0) {
            ShareHelper.copyQuote(requireContext(), q);
        } else if (i == 1) {
            ShareHelper.shareQuote(requireContext(), q);
        } else if (i == 2) {
            ShareHelper.saveQuote(requireContext(), q);
        }
    }

    private void setChecked(int i) {
        if (i == 0) {
            ((RadioButton) radioGroup.findViewById(R.id.bottomSheetCopyRadioButton))
                    .setChecked(true);
        } else if (i == 1) {
            ((RadioButton) radioGroup.findViewById(R.id.bottomSheetShareRadioButton))
                    .setChecked(true);
        } else if (i == 2) {
            ((RadioButton) radioGroup.findViewById(R.id.bottomSheetSaveRadioButton))
                    .setChecked(true);
        } else if (i == 3) {
            ((RadioButton) radioGroup.findViewById(R.id.bottomSheetAskRadioButton))
                    .setChecked(true);
        }
    }

    private int getInt(int id) {
        // Copy -> 0
        // Share -> 1
        // Save -> 2
        // Askqf -> 3

        if (id == R.id.bottomSheetCopyRadioButton) return 0;
        else if (id == R.id.bottomSheetShareRadioButton) return 1;
        else if (id == R.id.bottomSheetSaveRadioButton) return 2;
        else if (id == R.id.bottomSheetAskRadioButton) return 3;
        return 1;
    }
}
