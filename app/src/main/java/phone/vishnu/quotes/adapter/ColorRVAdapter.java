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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.fragment.ColorPickFragment;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;

public class ColorRVAdapter extends ListAdapter<String, ColorRVAdapter.ViewHolder> {

    private OnItemClickListener listener;
    private final int REQ_CODE;

    public ColorRVAdapter(int REQ_CODE) {
        super(
                new DiffUtil.ItemCallback<String>() {
                    @Override
                    public boolean areItemsTheSame(
                            @NonNull String oldItem, @NonNull String newItem) {
                        return oldItem.equals(newItem);
                    }

                    @Override
                    public boolean areContentsTheSame(
                            @NonNull String oldItem, @NonNull String newItem) {
                        return oldItem.equals(newItem);
                    }
                });
        this.REQ_CODE = REQ_CODE;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.color_pick_single_item, parent, false);
        return new ColorRVAdapter.ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        if (getItem(position).equals("#00000000")) {

            holder.imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                            holder.itemView.getContext(), R.drawable.ic_no_color));

            holder.textView.setText("TRANSPARENT");

        } else {

            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(Color.parseColor(getItem(position)));
            gradientDrawable.setCornerRadius(DPtoPX(holder.itemView.getContext(), 96));

            if (isFontColor(holder.itemView.getContext(), getItem(position))
                    || isCardColor(holder.itemView.getContext(), getItem(position))) {

                gradientDrawable.setStroke(
                        DPtoPX(holder.itemView.getContext(), 2),
                        holder.itemView.getContext().getResources().getColor(R.color.spacerColor));
            }

            holder.imageView.setBackgroundDrawable(gradientDrawable);

            holder.textView.setText(getItem(position).replace("#FF", "#"));
        }
    }

    private boolean isCardColor(Context context, String s) {
        return REQ_CODE == ColorPickFragment.PICK_CARD_COLOR_REQ_CODE
                && s.equals(new SharedPreferenceHelper(context).getCardColorPreference());
    }

    private boolean isFontColor(Context context, String s) {
        return REQ_CODE == ColorPickFragment.PICK_FONT_COLOR_REQ_CODE
                && s.equals(new SharedPreferenceHelper(context).getFontColorPreference());
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    private int DPtoPX(Context context, int DP) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(DP * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public interface OnItemClickListener {
        void onItemClick(String colorString);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView imageView;
        private final TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.colorPickSingleView);
            textView = itemView.findViewById(R.id.colorPickSingleTV);

            imageView.setOnClickListener(this);
            textView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION)
                listener.onItemClick(getItem(getAdapterPosition()));
        }
    }
}
