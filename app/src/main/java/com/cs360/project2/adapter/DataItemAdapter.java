package com.cs360.project2.adapter;
/*
 * RecyclerView adapter for inventory items.
 * - Mutates the provided list (owned by Activity) and issues targeted notify* calls.
 * - Delegates persistence to the Activity via OnItemActionListener.
 */

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs360.project2.R;
import com.cs360.project2.model.InventoryItem;

import java.util.List;

public class DataItemAdapter extends RecyclerView.Adapter<DataItemAdapter.ViewHolder> {

    /** Callbacks to let the Activity persist changes and send SMS if needed. */
    public interface OnItemActionListener {
        void onIncrement(String name, int currentQty);
        void onDecrement(String name, int currentQty);
        void onDelete(String name);
    }

    private OnItemActionListener listener;
    public void setOnItemActionListener(OnItemActionListener l) { this.listener = l; }

    private final List<InventoryItem> data;

    public DataItemAdapter(List<InventoryItem> data) {
        this.data = data;
        // No stable IDs since name can be edited in future.
        setHasStableIds(false);
    }

    @NonNull
    @Override
    public DataItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item, parent, false);
        return new DataItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataItemAdapter.ViewHolder holder, int position) {
        InventoryItem item = data.get(position);

        // Bind simple text fields using localized string templates.
        holder.nameLabel.setText(item.getName());
        holder.quantityLabel.setText(
                holder.itemView.getContext().getString(R.string.quantity_format, item.getQuantity())
        );
        holder.dateLabel.setText(
                holder.itemView.getContext().getString(R.string.date_format, item.getLastUpdated())
        );

        // Increment: update UI immediately and notify consumer.
        holder.incrementButton.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;
            InventoryItem it = data.get(pos);
            it.setQuantity(it.getQuantity() + 1);
            notifyItemChanged(pos);
            if (listener != null) listener.onIncrement(it.getName(), it.getQuantity());
        });

        // Decrement with floor at 0.
        holder.decrementButton.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;
            InventoryItem it = data.get(pos);
            if (it.getQuantity() > 0) {
                it.setQuantity(it.getQuantity() - 1);
                notifyItemChanged(pos);
                if (listener != null) listener.onDecrement(it.getName(), it.getQuantity());
            }
        });

        // Delete: remove from list and let consumer persist the change.
        holder.deleteButton.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;
            InventoryItem it = data.remove(pos);
            notifyItemRemoved(pos);
            if (listener != null) listener.onDelete(it.getName());
        });
    }

    @Override
    public int getItemCount() { return data.size(); }

    // Make this PUBLIC so it can appear in public method signatures without visibility warnings.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameLabel;
        final TextView quantityLabel;
        final TextView dateLabel;
        final ImageButton incrementButton;
        final ImageButton decrementButton;
        final ImageButton deleteButton;

        public ViewHolder(@NonNull View v) {
            super(v);
            nameLabel       = v.findViewById(R.id.itemNameLabel);
            quantityLabel   = v.findViewById(R.id.itemQuantity);
            dateLabel       = v.findViewById(R.id.itemDate);
            incrementButton = v.findViewById(R.id.incrementButton);
            decrementButton = v.findViewById(R.id.decrementButton);
            deleteButton    = v.findViewById(R.id.deleteButton);
        }
    }
}
