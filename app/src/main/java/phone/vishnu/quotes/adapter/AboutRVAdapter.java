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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.model.TourItem;

public class AboutRVAdapter extends ListAdapter<TourItem, AboutRVAdapter.ViewHolder> {

    private OnItemClickListener listener;

    public AboutRVAdapter() {
        super(
                new DiffUtil.ItemCallback<TourItem>() {
                    @Override
                    public boolean areItemsTheSame(
                            @NonNull TourItem oldItem, @NonNull TourItem newItem) {
                        return oldItem.equals(newItem);
                    }

                    @Override
                    public boolean areContentsTheSame(
                            @NonNull TourItem oldItem, @NonNull TourItem newItem) {
                        return oldItem.equals(newItem);
                    }
                });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.about_single_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TourItem item = getItem(position);

        holder.iconIV.setImageResource(item.getImgId());

        holder.titleTV.setText(item.getTitle());
        holder.descTV.setText(item.getDesc());
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView iconIV;
        private final TextView titleTV;
        private final TextView descTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            iconIV = itemView.findViewById(R.id.aboutSingleItemIconIV);
            titleTV = itemView.findViewById(R.id.aboutSingleItemTitleTV);
            descTV = itemView.findViewById(R.id.aboutSingleItemDescriptionTV);

            itemView.findViewById(R.id.aboutConstraintLayout)
                    .setOnClickListener(
                            v -> {
                                if (listener != null
                                        && getAdapterPosition() != RecyclerView.NO_POSITION)
                                    listener.onItemClick(getAdapterPosition());
                            });
        }
    }
}
