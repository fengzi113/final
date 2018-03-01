package com.example.fengzi113.inventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.fengzi113.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_IMAGE;
import static com.example.fengzi113.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME;
import static com.example.fengzi113.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_LEFT;
import static com.example.fengzi113.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE;
import static com.example.fengzi113.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_SALE;
import static com.example.fengzi113.inventory.data.InventoryContract.InventoryEntry.CONTENT_URI;
import static com.example.fengzi113.inventory.data.InventoryContract.InventoryEntry._ID;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER = 0;
    InventoryCursorAdapter mInventoryCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.bt_tianjia);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView listView = (ListView) findViewById(R.id.list_view);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        mInventoryCursorAdapter = new InventoryCursorAdapter(this, null);
        listView.setAdapter(mInventoryCursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {

                Uri currentUri = ContentUris.withAppendedId(CONTENT_URI, id);

                Intent intent = new Intent(MainActivity.this, EditorActivity.class);

                TextView leftTxetView = view.findViewById(R.id.tv_left);
                String currentLeftString = leftTxetView.getText().toString();
                int currentLeftInt = Integer.parseInt(currentLeftString);

                TextView saleTxetView = view.findViewById(R.id.tv_sale_text);
                String currentSaleString = saleTxetView.getText().toString();
                int currentSaleInt = Integer.parseInt(currentSaleString);

                Bundle date = new Bundle();
                date.putInt("currentLeftInt", currentLeftInt);
                date.putInt("currentSaleInt", currentSaleInt);
                date.putString("currentUri", currentUri.toString());
                intent.putExtra("date", date);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(LOADER, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    //    插入模拟数据
    private void insertInventory() {

        ContentValues values = new ContentValues();
        values.put(COLUMN_INVENTORY_NAME, getString(R.string.text_tieguo));
        values.put(COLUMN_INVENTORY_LEFT, 100);
        values.put(COLUMN_INVENTORY_PRICE, 12);
        values.put(COLUMN_INVENTORY_SALE, 50);
        values.put(COLUMN_INVENTORY_IMAGE,
                "android.resource://eu.laramartin.inventorymanager/drawable/gummibear");

        getContentResolver().insert(CONTENT_URI, values);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                // Do nothing for now
                insertInventory();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                showDeleteAllConfirmationDialog();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    //删除所有数据
    private void deleteAll() {

        int rowsDeleted = getContentResolver().delete(CONTENT_URI, null, null);
        Toast.makeText(this, R.string.delete_all, Toast.LENGTH_SHORT).show();

    }

    //    删除确认
    private void showDeleteAllConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.text_delete_all);

        builder.setPositiveButton(R.string.text_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteAll();
            }
        });
        builder.setNegativeButton(R.string.text_cancle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                _ID,
                COLUMN_INVENTORY_NAME,
                COLUMN_INVENTORY_LEFT,
                COLUMN_INVENTORY_PRICE,
                COLUMN_INVENTORY_SALE,
                COLUMN_INVENTORY_IMAGE
        };

        return new CursorLoader(this,
                CONTENT_URI,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        mInventoryCursorAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mInventoryCursorAdapter.swapCursor(null);

    }
}