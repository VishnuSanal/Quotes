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

package phone.vishnu.quotes.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.Objects;
import phone.vishnu.quotes.R;

public class BGImageRVAdapter extends ListAdapter<Uri, BGImageRVAdapter.ViewHolder> {

    private OnItemClickListener listener;

    public BGImageRVAdapter() {
        super(
                new DiffUtil.ItemCallback<Uri>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull Uri oldItem, @NonNull Uri newItem) {
                        return Objects.equals(oldItem.getEncodedPath(), newItem.getEncodedPath());
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull Uri oldItem, @NonNull Uri newItem) {
                        return Objects.equals(oldItem.getEncodedPath(), newItem.getEncodedPath());
                    }
                });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.background_image_pick_single_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (getItem(position).toString().startsWith("file://"))
            Picasso.get()
                    .load(
                            new File(
                                    getItem(position)
                                            .toString()
                                            .replace(
                                                    "file://",
                                                    holder.itemView
                                                            .getContext()
                                                            .getFilesDir()
                                                            .getPath())))
                    .into(
                            holder.imageView,
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
        else
            Picasso.get()
                    .load(getItem(position))
                    .into(
                            holder.imageView,
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
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Uri uri);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final LinearProgressIndicator progressIndicator;

        ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.defaultSingleImage);
            progressIndicator = itemView.findViewById(R.id.defaultSingleImageProgressIndicator);

            imageView.setOnClickListener(
                    v -> {
                        if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION)
                            listener.onItemClick(getItem(getAdapterPosition()));
                    });
        }
    }
}
