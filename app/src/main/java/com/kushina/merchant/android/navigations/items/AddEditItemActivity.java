package com.kushina.merchant.android.navigations.items;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kushina.merchant.android.R;
import com.kushina.merchant.android.globals.API;
import com.kushina.merchant.android.globals.Globals;
import com.kushina.merchant.android.globals.Preferences;
import com.kushina.merchant.android.navigations.MainActivity;
import com.kushina.merchant.android.navigations.categories.AddEditCategoryActivity;
import com.kushina.merchant.android.navigations.categories.RVCategoriesModel;
import com.squareup.picasso.Picasso;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.kushina.merchant.android.globals.Endpoints.API_NODE_CATEGORY;
import static com.kushina.merchant.android.globals.Endpoints.API_NODE_ITEMS;

public class AddEditItemActivity extends AppCompatActivity {

    @BindView(R.id.s_category) SearchableSpinner sCategory;
    @BindView(R.id.til_category) TextInputLayout tilCategory;
    @BindView(R.id.switch_item_status) Switch switchItemStatus;
    @BindView(R.id.edt_add_edit_items_item_name) TextInputEditText edtAddEditItemsItemName;
    @BindView(R.id.til_add_edit_items_item_name) TextInputLayout tilAddEditItemsItemName;
    @BindView(R.id.edt_add_edit_items_item_price) TextInputEditText edtAddEditItemsItemPrice;
    @BindView(R.id.til_add_edit_items_item_price) TextInputLayout tilAddEditItemsItemPrice;
    @BindView(R.id.edt_add_edit_items_item_quantity) TextInputEditText edtAddEditItemsItemQuantity;
    @BindView(R.id.til_add_edit_items_item_quantity) TextInputLayout tilAddEditItemsItemQuantity;
    @BindView(R.id.edt_add_edit_items_item_toque) TextInputEditText edtAddEditItemsItemToque;
    @BindView(R.id.til_add_edit_items_item_toque) TextInputLayout tilAddEditItemsItemToque;
    @BindView(R.id.edt_add_edit_items_item_description) TextInputEditText edtAddEditItemsItemDescription;
    @BindView(R.id.til_add_edit_items_item_description) TextInputLayout tilAddEditItemsItemDescription;
    @BindView(R.id.iv_item_image) ImageView ivItemImage;
    @BindView(R.id.btn_save_item) Button btnSaveItem;

    @OnClick(R.id.iv_item_image)
    public void setImage(){
        if (ContextCompat.checkSelfPermission(AddEditItemActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ivItemImage.setEnabled(false);
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            selectImage(this);
        }
    }

    public final String TAG = getClass().getSimpleName();

    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;

    List<RVCategoriesModel> cModel;

    private String task,categoryID,availability,itemID;
    private Bitmap bitmap;
    private Boolean uploadItemImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);
        ButterKnife.bind(this);

        mAPI = new API(this);
        mGlobals = new Globals(this);
        mPreferences = new Preferences(this);



        task = getIntent().getStringExtra("task");
        validateFields();

        if(task.toLowerCase().contains("update")){

            itemID = getIntent().getStringExtra("item_id");
            categoryID = getIntent().getStringExtra("category_id");
            if(getIntent().getStringExtra("item_status").toLowerCase().equals("available")){
                availability = "1";
            }else{
                availability = "0";
            }
            edtAddEditItemsItemName.setText(getIntent().getStringExtra("item_name"));
            edtAddEditItemsItemPrice.setText(getIntent().getStringExtra("item_srp"));
            edtAddEditItemsItemQuantity.setText(getIntent().getStringExtra("item_quantity"));
            edtAddEditItemsItemDescription.setText(getIntent().getStringExtra("item_description"));

            try {
                Picasso.get()
                        //   .load(ITEMS_URL+item.getItemPicture())
                        .load(getIntent().getStringExtra("item_image"))
                        //.load(item.getItemImage())
                        .resize(400,400)
                        .placeholder(R.drawable.ic_add_a_photo_black_24dp)
                        .into(ivItemImage);

            } catch (Exception e) {
                e.printStackTrace();
            }
            if(categoryID.equals("1")){
                tilAddEditItemsItemToque.setVisibility(View.VISIBLE);
                edtAddEditItemsItemToque.setText(getIntent().getStringExtra("item_toque"));
            }else{
                tilAddEditItemsItemToque.setVisibility(View.GONE);
            }
            if(getIntent().getStringExtra("item_status").toLowerCase().equals("available")){
                switchItemStatus.setText("Available");
                switchItemStatus.setChecked(true);
            }else{
                switchItemStatus.setText("Unavailable");
                switchItemStatus.setChecked(false);
            }

            loadCategories();

        }else{
            switchItemStatus.setText("Available");
            switchItemStatus.setChecked(true);
            availability = "1";

            loadCategories();
        }


        switchItemStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    switchItemStatus.setText("Available");
                    availability = "1";
                }else{
                    switchItemStatus.setText("Unavailable");
                    availability = "0";
                }
            }
        });

    }

    @OnClick(R.id.btn_save_item)
    public void onViewClicked() {
        if(categoryID.equals("0")){
            mGlobals.showErrorMessage("Please select a category.", true, new Globals.Callback() {
                @Override
                public void onPickCallback(Boolean result) {
                    if(result){
                        return;
                    }
                }
            });
        }else{
            mGlobals.showChoiceDialog("Save this item?", true, new Globals.Callback() {
                @Override
                public void onPickCallback(Boolean result) {
                    if(result){
                        checkFields();
                    }
                }
            });

        }
    }

    private void checkFields(){

        if(task.toLowerCase().contains("update")){
            bitmap = ((BitmapDrawable)ivItemImage.getDrawable()).getBitmap();
        }

        if (

                !mGlobals.validateField(tilAddEditItemsItemName, edtAddEditItemsItemName, true, getString(R.string.err_msg_items_name)) ||
                !mGlobals.validateField(tilAddEditItemsItemPrice, edtAddEditItemsItemPrice, true, getString(R.string.err_msg_items_price)) ||
                !mGlobals.validateField(tilAddEditItemsItemQuantity, edtAddEditItemsItemQuantity, true, getString(R.string.err_msg_items_quantity)) ||
                bitmap == null


        ) {
            mGlobals.showErrorMessageWithDelay(
                    "Fields and item image cannot be empty.",
                    true,
                    new Globals.Callback() {
                        @Override
                        public void onPickCallback(Boolean result) {
                            if (result) {
                                return;
                            }
                        }
                    }
            );
        } else {

            if(categoryID.equals("1")){
                if(      !mGlobals.validateField(tilAddEditItemsItemToque, edtAddEditItemsItemToque, true, getString(R.string.err_msg_items_toque))){
                    mGlobals.showErrorMessageWithDelay(
                            "Enter a number of toque.",
                            true,
                            new Globals.Callback() {
                                @Override
                                public void onPickCallback(Boolean result) {
                                    if (result) {
                                        return;
                                    }
                                }
                            }
                    );
                }else{
                    if(task.toLowerCase().contains("add")){
                        addItem();
                    }else{
                        updateItem();
                    }
                }
            }else{
                if(task.toLowerCase().contains("add")){
                    addItem();
                }else{
                    updateItem();
                }
            }

        }

    }

    private void addItem(){

        // get image name and file
        Bitmap proof_of_payment = bitmap;
        String encodedImage = "";
        if (proof_of_payment != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            proof_of_payment.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        }

        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("user_id", mPreferences.getUserId().toString());
        request_data.put("category_id", categoryID);
        request_data.put("description", edtAddEditItemsItemName.getText().toString());
        request_data.put("long_description", edtAddEditItemsItemDescription.getText().toString());
        request_data.put("srp", edtAddEditItemsItemPrice.getText().toString());
        request_data.put("quantity", edtAddEditItemsItemQuantity.getText().toString());
        request_data.put("item_image", encodedImage);
        request_data.put("availability", availability);
        if(categoryID.equals("1")) {
            request_data.put("toque", edtAddEditItemsItemToque.getText().toString());
        }

        // api call
        mAPI.api_request("POST",
                API_NODE_ITEMS + "addNewItem",
                request_data,
                true,
                AddEditItemActivity.this,
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {

                        try {
                            mGlobals.log(getClass().getEnclosingMethod().getName(), result.toString(4));
                            String status_message = result.getString("status_message");

                            Integer status_code = result.getInt("status_code");

                            if (status_code == 200) {

                                JSONObject root = result;


                                mGlobals.showSuccessDialog(status_message, true, new Globals.Callback() {
                                    @Override
                                    public void onPickCallback(Boolean result) {
                                        if(result){
                                            onBackPressed();
                                        }
                                    }
                                });
                                // mGlobals.dismissLoadingDialog();


                            } else {
                                // show error
                                mGlobals.log(TAG, status_message);
                            }
                        } catch (Exception e) {
                            // show exception error
                            mGlobals.log(TAG, e.toString());
                        }
                    }
                });

    }

    private void updateItem(){

        bitmap = ((BitmapDrawable)ivItemImage.getDrawable()).getBitmap();
        // get image name and file
        Bitmap proof_of_payment = bitmap;
        String encodedImage = "";
        if (proof_of_payment != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            proof_of_payment.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        }

        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("user_id", mPreferences.getUserId().toString());
        request_data.put("category_id", categoryID);
        request_data.put("description", edtAddEditItemsItemName.getText().toString());
        request_data.put("long_description", edtAddEditItemsItemDescription.getText().toString());
        request_data.put("srp", edtAddEditItemsItemPrice.getText().toString());
        request_data.put("quantity", edtAddEditItemsItemQuantity.getText().toString());
        if(uploadItemImage){
            request_data.put("item_image", encodedImage);
        }
        request_data.put("availability", availability);
        if(categoryID.equals("1")) {
            request_data.put("toque", edtAddEditItemsItemToque.getText().toString());
        }
        request_data.put("item_id", itemID);

        // api call
        mAPI.api_request("POST",
                API_NODE_ITEMS + "updateItem",
                request_data,
                true,
                AddEditItemActivity.this,
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {

                        try {
                            mGlobals.log(getClass().getEnclosingMethod().getName(), result.toString(4));
                            String status_message = result.getString("status_message");

                            Integer status_code = result.getInt("status_code");

                            if (status_code == 200) {

                                JSONObject root = result;


                                mGlobals.showSuccessDialog(status_message, true, new Globals.Callback() {
                                    @Override
                                    public void onPickCallback(Boolean result) {
                                        if(result){
                                            onBackPressed();
                                        }
                                    }
                                });
                                // mGlobals.dismissLoadingDialog();


                            } else {
                                // show error
                                mGlobals.log(TAG, status_message);
                            }
                        } catch (Exception e) {
                            // show exception error
                            mGlobals.log(TAG, e.toString());
                        }
                    }
                });

    }

    private void loadCategories() {

        cModel = new ArrayList<>();
        cModel.clear();

        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("status", "active");

        mAPI.api_request("POST",
                API_NODE_CATEGORY + "getAllCategories",
                request_data,
                true,
                AddEditItemActivity.this,
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {



                        try {
                            mGlobals.log(getClass().getEnclosingMethod().getName(), result.toString(4));
                            String status_message = result.getString("status_message");

                            Integer status_code = result.getInt("status_code");

                            final ArrayList<String> spinner_categories = new ArrayList<String>();

                            spinner_categories.add("Select Category");
                            cModel.add(new RVCategoriesModel("0", "Select Category", "avail", ""));

                            if (status_code == 200) {

                                JSONObject root = result;
                                JSONObject data_array = root.getJSONObject("data");
                                JSONArray items = data_array.getJSONArray("categories");

                                for (int i = 0; i < items.length(); i++) {

                                    String category_id = ((JSONObject) items.get(i)).get("category_id").toString();
                                    String category = ((JSONObject) items.get(i)).get("category").toString();
                                    String status = ((JSONObject) items.get(i)).get("status").toString();
                                    String date_created = ((JSONObject) items.get(i)).get("date_created").toString();

                                    cModel.add(new RVCategoriesModel(category_id, category, status, date_created));
                                    spinner_categories.add(category);

                                }


                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddEditItemActivity.this,
                                        android.R.layout.simple_spinner_dropdown_item, spinner_categories);

                                sCategory.setAdapter(adapter);

                                if(task.toLowerCase().contains("update")){
                                    for(int i = 0; i < cModel.size(); i++){
                                        if(categoryID.equals(cModel.get(i).getCategoryID())){
                                            sCategory.setSelection(i,false);
                                            categoryID = cModel.get(i).getCategoryID();
                                        }
                                    }
                                }else{
                                    sCategory.setSelection(0,false);
                                    categoryID = "0";
                                    tilAddEditItemsItemToque.setVisibility(View.GONE);
                                }


                                sCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                        RVCategoriesModel selectedItem = cModel.get(position);

                                        categoryID = selectedItem.getCategoryID();

                                        if(categoryID.equals("1")){
                                            tilAddEditItemsItemToque.setVisibility(View.VISIBLE);
                                        }else{

                                            tilAddEditItemsItemToque.setVisibility(View.GONE);
                                        }


                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });


                            } else {
                                // show error
                                mGlobals.log(getClass().getEnclosingMethod().getName(), status_message);


                            }
                        } catch (Exception e) {
                            // show exception error
                            mGlobals.log(getClass().getEnclosingMethod().getName(), e.toString());
                        }


                    }
                });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        mGlobals.log(TAG, "onRequestPermissionsResult()");
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                ivItemImage.setEnabled(true);
                selectImage(this);
            }
        }
    }

    private void selectImage(Context context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery", "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your item picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        bitmap = selectedImage;
                        ivItemImage.setImageBitmap(selectedImage);
                        uploadItemImage = true;
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        try {
                            Uri selectedImg = data.getData();
                            Bitmap imageFromGallery = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImg);
                            if(imageFromGallery != null) {
                                bitmap = imageFromGallery;
                                ivItemImage.setImageBitmap(imageFromGallery);
                                uploadItemImage = true;
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }

//                        Uri selectedImage =  data.getData();
//                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                        if (selectedImage != null) {
//                            Cursor cursor = getContentResolver().query(selectedImage,
//                                    filePathColumn, null, null, null);
//                            if (cursor != null) {
//                                cursor.moveToFirst();
//
//                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                                String picturePath = cursor.getString(columnIndex);
//                                bitmap = BitmapFactory.decodeFile(picturePath);
//                                ivItemImage.setImageBitmap(bitmap);
//                                cursor.close();
//                            }
//                        }

                    }
                    break;
            }
        }
    }

    private void validateFields() {
        edtAddEditItemsItemName.addTextChangedListener(new MyTextWatcher(edtAddEditItemsItemName));
        edtAddEditItemsItemPrice.addTextChangedListener(new MyTextWatcher(edtAddEditItemsItemPrice));
        edtAddEditItemsItemQuantity.addTextChangedListener(new MyTextWatcher(edtAddEditItemsItemQuantity));
        edtAddEditItemsItemToque.addTextChangedListener(new MyTextWatcher(edtAddEditItemsItemToque));
        //edtAddEditItemsItemDescription.addTextChangedListener(new MyTextWatcher(edtAddEditItemsItemDescription));
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {

                case R.id.edt_add_edit_items_item_name:
                    mGlobals.validateField(tilAddEditItemsItemName, edtAddEditItemsItemName, true, getString(R.string.err_msg_items_name));
                    break;
                case R.id.edt_add_edit_items_item_price:
                    mGlobals.validateField(tilAddEditItemsItemPrice, edtAddEditItemsItemPrice, true, getString(R.string.err_msg_items_price));
                    break;
                case R.id.edt_add_edit_items_item_quantity:
                    mGlobals.validateField(tilAddEditItemsItemQuantity, edtAddEditItemsItemQuantity, true, getString(R.string.err_msg_items_quantity));
                    break;
                case R.id.edt_add_edit_items_item_toque:
                    mGlobals.validateField(tilAddEditItemsItemToque, edtAddEditItemsItemToque, true, getString(R.string.err_msg_items_toque));
                    break;
                case R.id.edt_add_edit_items_item_description:
                    mGlobals.validateField(tilAddEditItemsItemDescription, edtAddEditItemsItemDescription, true, getString(R.string.err_msg_items_description));
                    break;

            }
        }
    }

}
