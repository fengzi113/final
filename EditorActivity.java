/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.fengzi113.inventory;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import static com.example.fengzi113.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_IMAGE;
import static com.example.fengzi113.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME;
import static com.example.fengzi113.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_LEFT;
import static com.example.fengzi113.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE;
import static com.example.fengzi113.inventory.data.InventoryContract.InventoryEntry.COLUMN_INVENTORY_SALE;
import static com.example.fengzi113.inventory.data.InventoryContract.InventoryEntry.CONTENT_URI;
import static com.example.fengzi113.inventory.data.InventoryContract.InventoryEntry._ID;

public class EditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXITED_LOADER = 0;
    private EditText mNameEditText;
    private EditText mPriEditText;
    private EditText mSaleEditText;
    private EditText mLeftEditText;
    private Button mIncreButton;
    private Button mDecreButton;
    private Button mOrderButton;
    private ImageView imageView;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int PICK_IMAGE_REQUEST = 0;
    private static final String TAG = "EditorActivity";
    private boolean mHabitHasChanged = false;
    Uri actualUri;
    private Uri mCurrentUri;
    int currentLeftInt, currentSaleInt;
    //    表格监控
    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mHabitHasChanged = true;
            return false;
        }
    };

    //    返回监控
    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mHabitHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    //    退出确认
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.message_exit);
        builder.setPositiveButton(R.string.bt_discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.bt_keep, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //    删除确认
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.text_message);

        builder.setPositiveButton(R.string.text_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Log.v(TAG, "========  onCreate  actualUri ======== "+ actualUri);

        if (savedInstanceState != null){

            String actualUriString = savedInstanceState.getString("actualUriString");
            Log.v(TAG, "========  savedInstanceState  actualUri ======== "+ actualUriString);

//            actualUri = Uri.parse(actualUriString);

        }

        Intent mIntent = getIntent();
        Bundle date = mIntent.getBundleExtra("date");
        //        取MainActivity传入的数据
        if (date != null) {
            currentLeftInt = date.getInt("currentLeftInt");
            currentSaleInt = date.getInt("currentSaleInt");
            String uriString = date.getString("currentUri");
            mCurrentUri = Uri.parse(uriString);
        }
        mNameEditText = (EditText) findViewById(R.id.tv_name);
        mPriEditText = (EditText) findViewById(R.id.tv_price);
        mSaleEditText = (EditText) findViewById(R.id.tv_sale_cliclk);
        mLeftEditText = (EditText) findViewById(R.id.tv_left);

        mIncreButton = findViewById(R.id.bt_incre);
        mDecreButton = findViewById(R.id.bt_decre);
        mOrderButton = findViewById(R.id.bt_order);
        imageView = findViewById(R.id.iv_pic);

        mOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("tel:12345678910");
                Intent it = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(it);
            }
        });
        mNameEditText.setOnTouchListener(mOnTouchListener);
        mPriEditText.setOnTouchListener(mOnTouchListener);
        mSaleEditText.setOnTouchListener(mOnTouchListener);
        mLeftEditText.setOnTouchListener(mOnTouchListener);
        mIncreButton.setOnTouchListener(mOnTouchListener);
        mDecreButton.setOnTouchListener(mOnTouchListener);

        if (mCurrentUri == null) {

            setTitle(getString(R.string.title_add));
            mDecreButton.setVisibility(View.GONE);
            mIncreButton.setVisibility(View.GONE);
            invalidateOptionsMenu();

        } else {
            setTitle(getString(R.string.title_edit));
            getLoaderManager().initLoader(EXITED_LOADER, null, this);
        }

        Log.v(TAG, "======== onCreate initLoader ========");

    }

    //存量加1
    public void addOne(View view) {
        String leftString = mLeftEditText.getText().toString();
        int leftInt;
        if (leftString.isEmpty()) {
            leftInt = 0;
        } else {
            leftInt = Integer.parseInt(leftString);
        }
        mLeftEditText.setText(String.valueOf(leftInt + 1));
    }

    // 存量减1
    public void desOne(View view) {

        String leftString = mLeftEditText.getText().toString();
        int leftInt;
        if (leftString.isEmpty()) {
            leftInt = 0;
        } else {
            leftInt = Integer.parseInt(leftString);
        }
        if (leftInt >= 1) {

            mLeftEditText.setText(String.valueOf(leftInt - 1));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    //    标题栏 - 按钮控制
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Do nothing for now
                if (saveInventory()) {
                    finish();
                }
                return true;

            // Respond to a click on the "Delete" menu option
            case R.id.action_del:
                // Do nothing for now
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if (!mHabitHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //    添加货物
    private boolean saveInventory() {

        Log.v(TAG, "======== saveInventory ========");
        String nameString = mNameEditText.getText().toString().trim();
        String leftString = mLeftEditText.getText().toString().trim();
        String pricetring = mPriEditText.getText().toString().trim();
        String saleString = mSaleEditText.getText().toString().trim();

        if (TextUtils.isEmpty(nameString) ||
                TextUtils.isEmpty(leftString) ||
                TextUtils.isEmpty(pricetring) ||
                TextUtils.isEmpty(saleString)) {
            Toast.makeText(this, R.string.input_error, Toast.LENGTH_SHORT).show();
            return false;
        }

        int left2, price2, sale2;
        left2 = Integer.parseInt(leftString);
        price2 = Integer.parseInt(pricetring);
        sale2 = Integer.parseInt(saleString);

        ContentValues values = new ContentValues();
        values.put(COLUMN_INVENTORY_NAME, nameString);
        values.put(COLUMN_INVENTORY_LEFT, left2);
        values.put(COLUMN_INVENTORY_PRICE, price2);
        values.put(COLUMN_INVENTORY_SALE, sale2);
        values.put(COLUMN_INVENTORY_IMAGE, String.valueOf(actualUri));

        // 新货物
        if (mCurrentUri == null) {

            Uri newUri = getContentResolver().insert(CONTENT_URI, values);
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, R.string.insert_failed,
                        Toast.LENGTH_SHORT).show();
                return false;

            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, R.string.insert_successful,
                        Toast.LENGTH_SHORT).show();
                return true;

            }
        } else {

            // 更新现有货物
            int rowsAffected = getContentResolver().update(mCurrentUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, R.string.update_failed,
                        Toast.LENGTH_SHORT).show();
                return false;

            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, R.string.update_successful,
                        Toast.LENGTH_SHORT).show();
                return true;
            }

        }
    }

    //    添加新货物时--删除菜单选项
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_del);
            menuItem.setVisible(false);

        }
        return true;
    }

    //    删除货物
    private void deletePet() {

        int rowsDeleted = getContentResolver().
                delete(mCurrentUri, null, null);
        // TODO: Implement this method
        // Show a toast message depending on whether or not the delete was successful.
        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, R.string.delete_failed, Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, R.string.delete_successful, Toast.LENGTH_SHORT).show();
        }
        finish();
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

        Log.v(TAG, "======== onCreateLoader ========");

        return new CursorLoader(this,
                mCurrentUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        Log.v(TAG, "======== onLoadFinished ========");

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            int nameColumnIndex = cursor.getColumnIndex(COLUMN_INVENTORY_NAME);
            int priceColumnIndex = cursor.getColumnIndex(COLUMN_INVENTORY_PRICE);
            int imgColumnIndex = cursor.getColumnIndex(COLUMN_INVENTORY_IMAGE);

            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            String imgUri = cursor.getString(imgColumnIndex);

            actualUri = Uri.parse(imgUri);
            mNameEditText.setText(name);
            mPriEditText.setText(Integer.toString(price));
            imageView.setImageURI(actualUri);
            mLeftEditText.setText(Integer.toString(currentLeftInt));
            mSaleEditText.setText(Integer.toString(currentSaleInt));

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        Log.v(TAG, "======== onLoaderReset ========");
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mLeftEditText.setText("");
        mPriEditText.setText("");
        mSaleEditText.setText("");
    }

    //    selectPic
    public void selectPic(View view) {

        Log.v(TAG, "========  selectPic checkSelfPermission   ======== ");

        //       检查是否授予了权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            //            没有授予权限 -- 提起权限请求
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            return;
        }
        openImageSelector();
    }

    //    权限请求处理
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        Log.v(TAG, "========  onRequestPermissionsResult   ======== ");

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImageSelector();
                    // permission was granted
                }
            }
        }
    }

    //    openImageSelector
    private void openImageSelector() {

        Log.v(TAG, "========  openImageSelector   ======== ");

        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser
                (intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        Log.v(TAG, "========  onActivityResult   ======== ");

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            //onActivityResult - resultData
            //所选照片 - 结果数据
            if (resultData != null) {
                actualUri = resultData.getData();
                imageView.setImageURI(actualUri);
                imageView.invalidate();
            }
        }
    }

}