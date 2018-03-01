package com.example.fengzi113.inventory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fengzi113.inventory.data.InventoryContract;

public class InventoryCursorAdapter extends CursorAdapter {

    private static final String TAG = "InventoryCursorAdapter";

    public InventoryCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context)
                .inflate(R.layout.list_item, viewGroup, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.tv_name);
        final TextView leftTextView = (TextView) view.findViewById(R.id.tv_left);
        TextView priTextView = (TextView) view.findViewById(R.id.tv_price);
        TextView saleClick = (TextView) view.findViewById(R.id.tv_sale_cliclk);
        final TextView saleTextView = (TextView) view.findViewById(R.id.tv_sale_text);

        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME);
        int leftColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_LEFT);
        int priColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE);
        int saleColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_SALE);


        String name = cursor.getString(nameColumnIndex);
        int left = cursor.getInt(leftColumnIndex);
        int pri = cursor.getInt(priColumnIndex);
        int sale = cursor.getInt(saleColumnIndex);

        nameTextView.setText(name);
        leftTextView.setText(Integer.toString(left));
        priTextView.setText(pri + context.getString(R.string.text_meige));
        saleTextView.setText(Integer.toString(sale));

        saleClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int leftInt,saleInt;
                String leftString = leftTextView.getText().toString();
                String saleString = saleTextView.getText().toString();

                if (TextUtils.isEmpty(leftString)) {

                    leftInt = 0;

                } else {

                    leftInt = Integer.parseInt(leftString);
                    if (leftInt == 0){

                        Toast.makeText(v.getContext(),R.string.tetxt_quehuo,Toast.LENGTH_SHORT).show();

                    }
                    if (leftInt >= 1) {

                        if (TextUtils.isEmpty(saleString)) {
                            saleInt = 0;
                        } else {
                            saleInt = Integer.parseInt(saleString);
                        }

                        saleTextView.setText(Integer.toString(saleInt + 1));
                        leftTextView.setText(Integer.toString(leftInt - 1));
                    }
                }
            }
        });
    }

}
