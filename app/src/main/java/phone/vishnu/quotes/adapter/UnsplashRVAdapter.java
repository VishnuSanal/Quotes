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

package phone.vishnu.quotes.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.util.Objects;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.model.UnsplashItem;

public class UnsplashRVAdapter extends ListAdapter<UnsplashItem, UnsplashRVAdapter.ViewHolder> {

    private OnItemClickListener listener;

    public UnsplashRVAdapter() {
        super(
                new DiffUtil.ItemCallback<UnsplashItem>() {
                    @Override
                    public boolean areItemsTheSame(
                            @NonNull UnsplashItem oldItem, @NonNull UnsplashItem newItem) {
                        return Objects.equals(oldItem, newItem);
                    }

                    @Override
                    public boolean areContentsTheSame(
                            @NonNull UnsplashItem oldItem, @NonNull UnsplashItem newItem) {
                        return Objects.equals(oldItem, newItem);
                    }
                });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.unsplash_image_pick_single_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Picasso.get()
                .load(getItem(position).getThumbUri())
                .into(
                        holder.imageIV,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.progressIndicator.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                e.printStackTrace();
                            }
                        });

        Picasso.get()
                .load(getItem(position).getUserProfile())
                .into(holder.profileIV); // TODO: Circular

        holder.nameTV.setText(getItem(position).getUserName());
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(UnsplashItem item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView imageIV, profileIV;
        private final TextView nameTV;
        private final LinearProgressIndicator progressIndicator;

        ViewHolder(View itemView) {
            super(itemView);

            imageIV = itemView.findViewById(R.id.unsplashSingleImage);
            profileIV = itemView.findViewById(R.id.unsplashSingleImageUserProfile);

            nameTV = itemView.findViewById(R.id.unsplashSingleImageUserName);

            progressIndicator = itemView.findViewById(R.id.unsplashSingleImageProgressIndicator);

            imageIV.setOnClickListener(
                    v -> {
                        if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION)
                            listener.onItemClick(getItem(getAdapterPosition()));
                    });

            nameTV.setOnClickListener(this);

            profileIV.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Uri uri = getItem(getAdapterPosition()).getUserProfileLink();

            if (getAdapterPosition() != RecyclerView.NO_POSITION)
                if (uri != null && uri != Uri.EMPTY)
                    itemView.getContext().startActivity(new Intent(Intent.ACTION_VIEW, uri));
                else
                    Toast.makeText(
                                    itemView.getContext(),
                                    "Profile link not found",
                                    Toast.LENGTH_SHORT)
                            .show();
        }
    }
}
