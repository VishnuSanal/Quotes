/*
 * Copyright (C) 2019 - 2024 Vishnu Sanal. T
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

package phone.vishnu.quotes.adapter;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import java.io.File;
import java.util.Objects;
import phone.vishnu.quotes.R;

public class FontRVAdapter extends ListAdapter<String, FontRVAdapter.ViewHolder> {

    private OnItemClickListener listener;

    public FontRVAdapter() {
        super(
                new DiffUtil.ItemCallback<String>() {
                    @Override
                    public boolean areItemsTheSame(
                            @NonNull String oldItem, @NonNull String newItem) {
                        return Objects.equals(oldItem, newItem);
                    }

                    @Override
                    public boolean areContentsTheSame(
                            @NonNull String oldItem, @NonNull String newItem) {
                        return Objects.equals(oldItem, newItem);
                    }
                });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.font_single_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String fontString = getItem(position).toLowerCase() + ".ttf";

        final File f = new File(holder.itemView.getContext().getFilesDir(), fontString);

        File otfFile =
                new File(
                        holder.itemView.getContext().getFilesDir(),
                        fontString.replace(".ttf", ".otf"));

        if (f.exists() || otfFile.exists()) {

            File file = (f.exists()) ? f : otfFile;

            holder.progressBar.setVisibility(View.GONE);

            try {
                Typeface face = Typeface.createFromFile(file);
                holder.fontTV.setTypeface(face);
            } catch (Exception e) {
                Toast.makeText(
                                holder.itemView.getContext(),
                                holder.itemView
                                        .getContext()
                                        .getString(R.string.oops_something_went_wrong),
                                Toast.LENGTH_SHORT)
                        .show();
                e.printStackTrace();
            }
        } else {
            holder.fontTV.setTextColor(
                    ContextCompat.getColor(
                            holder.itemView.getContext(), R.color.disabledTextColor));
            holder.constraintLayout.setOnClickListener(null);
        }

        fontString = fontString.replace(".ttf", "").replace(".otf", "");

        fontString = fontString.toUpperCase().charAt(0) + fontString.substring(1);

        holder.fontTV.setText(fontString);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(String s);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ConstraintLayout constraintLayout;
        private final TextView fontTV;
        private final LinearProgressIndicator progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.fontSingleItemConstraintLayout);
            fontTV = itemView.findViewById(R.id.fontSingleItemTitle);
            progressBar = itemView.findViewById(R.id.fontSingleItemProgressBar);

            itemView.findViewById(R.id.fontSingleItemConstraintLayout)
                    .setOnClickListener(
                            v -> {
                                if (listener != null
                                        && getAdapterPosition() != RecyclerView.NO_POSITION)
                                    listener.onItemClick(getItem(getAdapterPosition()));
                            });
        }
    }
}
