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

import static android.content.Context.CLIPBOARD_SERVICE;

import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Objects;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.model.Quote;
import phone.vishnu.quotes.repository.FavRepository;

public class AddNewFragment extends BottomSheetDialogFragment {

    private TextInputEditText quoteTIE, authorTIE;
    private Button submitButton, cancelButton;

    public static AddNewFragment newInstance() {
        return new AddNewFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_new, container, false);

        quoteTIE = view.findViewById(R.id.addNewQuoteTIE);
        authorTIE = view.findViewById(R.id.addNewAuthorTIE);
        submitButton = view.findViewById(R.id.addNewSubmitButton);
        cancelButton = view.findViewById(R.id.addNewCancelButton);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        quoteTIE.requestFocus();

        checkClipboard();

        submitButton.setOnClickListener(
                v -> {
                    String quote = Objects.requireNonNull(quoteTIE.getText()).toString();
                    String author = Objects.requireNonNull(authorTIE.getText()).toString();

                    if (quote.isEmpty() || author.isEmpty()) {
                        if (quote.isEmpty()) {
                            quoteTIE.setError("Field Empty");
                            quoteTIE.requestFocus();
                        } else {
                            authorTIE.setError("Field Empty");
                            authorTIE.requestFocus();
                        }
                    } else {
                        new FavRepository((Application) requireContext().getApplicationContext())
                                .insertFav(new Quote(quote, author, true));

                        Toast.makeText(
                                        requireContext(),
                                        "Quote added to Favorites",
                                        Toast.LENGTH_SHORT)
                                .show();

                        dismiss();
                    }
                });

        cancelButton.setOnClickListener(v -> dismiss());
    }

    private void checkClipboard() {
        ClipboardManager clipBoard =
                (ClipboardManager) requireActivity().getSystemService(CLIPBOARD_SERVICE);

        ClipData clipData = clipBoard.getPrimaryClip();

        if (clipData == null || clipData.getItemCount() == 0) return;

        String text = clipData.getItemAt(0).getText().toString();

        if (text.contains("-")) {

            String[] split = text.split("-");

            if (split.length < 2
                    || split[0] == null
                    || split[0].isEmpty()
                    || !split[0].contains("\"")
                    || split[1] == null
                    || split[1].isEmpty()) return;

            quoteTIE.setText(split[0].replace("\"", "").trim());
            authorTIE.setText(split[1].replace("-", "").trim());
        }
    }
}
