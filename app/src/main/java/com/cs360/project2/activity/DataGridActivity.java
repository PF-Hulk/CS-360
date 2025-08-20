package com.cs360.project2.activity;
/*
 * Inventory grid (CRUD) + low-stock SMS trigger (qty == 0).
 * - Displays SQLite-backed list in a RecyclerView grid.
 * - Uses targeted RecyclerView notifications (insert/remove/change).
 * - Persists changes via AppRepository.
 */
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs360.project2.R;
import com.cs360.project2.adapter.DataItemAdapter;
import com.cs360.project2.data.AppRepository;
import com.cs360.project2.model.InventoryItem;
import com.cs360.project2.util.SmsUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class DataGridActivity extends AppCompatActivity {

    // Adapter is shared across methods; keep as a field.
    private DataItemAdapter adapter;

    // Backing list for the adapter; mutated locally on UI actions.
    private final List<InventoryItem> items = new ArrayList<>();

    // Thin DAO wrapper for SQLite operations.
    private AppRepository repo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_grid);

        repo = new AppRepository(this);

        // Narrow scope: keep these local
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton addButton = findViewById(R.id.addButton);

        adapter = new DataItemAdapter(items);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        // Initial load from DB using specific range notifications (no notifyDataSetChanged)
        refreshItems();

        // Adapter callbacks: persist to DB; adapter already updates UI precisely
        adapter.setOnItemActionListener(new DataItemAdapter.OnItemActionListener() {
            @Override public void onIncrement(String name, int currentQty) {
                repo.updateQtyByName(name, currentQty, today());
                if (currentQty == 0) maybeSendLowStockSms(name);
            }
            @Override public void onDecrement(String name, int currentQty) {
                repo.updateQtyByName(name, currentQty, today());
                if (currentQty == 0) maybeSendLowStockSms(name);
            }
            @Override public void onDelete(String name) {
                repo.deleteByName(name);
                Toast.makeText(DataGridActivity.this, R.string.toast_deleted, Toast.LENGTH_SHORT).show();
            }
        });

        addButton.setOnClickListener(v -> showAddDialog());
    }

    /** Re-query DB and update the adapter using specific range operations. */
    private void refreshItems() {
        int oldCount = items.size();
        if (oldCount > 0) {
            items.clear();
            adapter.notifyItemRangeRemoved(0, oldCount);
        }
        List<InventoryItem> fresh = repo.getAllItems();
        if (!fresh.isEmpty()) {
            items.addAll(fresh);
            adapter.notifyItemRangeInserted(0, fresh.size());
        }
    }

    /** Add dialog that inserts exactly one row into the adapter without full refresh. */
    private void showAddDialog() {
        final EditText name = new EditText(this);
        name.setHint(R.string.item);

        final EditText qty = new EditText(this);
        // shows "Qty: %d" as a simple hint
        qty.setHint(R.string.quantity_format);
        qty.setInputType(InputType.TYPE_CLASS_NUMBER);

        // Lightweight vertical container for the two inputs.
        androidx.appcompat.widget.LinearLayoutCompat ll =
                new androidx.appcompat.widget.LinearLayoutCompat(this);
        ll.setOrientation(androidx.appcompat.widget.LinearLayoutCompat.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        ll.setPadding(pad, pad, pad, 0);
        ll.addView(name);
        ll.addView(qty);

        new AlertDialog.Builder(this)
                .setTitle(R.string.add_item_dialog_title)
                .setView(ll)
                .setPositiveButton(R.string.add, (d, w) -> {
                    String n = name.getText() != null ? name.getText().toString().trim() : "";
                    int q = 0;
                    try {
                        String qs = qty.getText() != null ? qty.getText().toString().trim() : "0";
                        q = Integer.parseInt(qs);
                    } catch (Exception ignored) {}

                    if (n.isEmpty()) {
                        Toast.makeText(this, R.string.toast_name_required, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Persist new row; fail if item already exists.
                    boolean ok = repo.addItem(n, q, today());
                    if (!ok) {
                        Toast.makeText(this, R.string.toast_item_exists, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Update UI precisely (append one row).
                    InventoryItem newItem = new InventoryItem(n, q, today());
                    items.add(newItem);
                    adapter.notifyItemInserted(items.size() - 1);

                    if (q == 0) maybeSendLowStockSms(n);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    /** Sends SMS alert when item hits 0; silently no-ops if permission not granted. */
    private void maybeSendLowStockSms(String itemName) {
        String phone = "5551234567"; // emulator/demo
        String msg = getString(R.string.sms_alert_template, itemName);
        SmsUtil.sendIfPermitted(this, phone, msg);
    }

    /** Today’s date in MM/dd/yyyy. */
    private String today() {
        return new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(new Date());
    }
}
