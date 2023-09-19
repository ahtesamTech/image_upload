package com.tech_land.imageuploadtoserver;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button upload;
    EditText name_up;
    ImageView up;

    Bitmap bitmap;

    String encodeArry;

    String url = "https://lrstock.shadighor.com/upload_img.php";
    int REQUEST_CODE = 222;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        up = findViewById(R.id.up);
        name_up = findViewById(R.id.name_up);
        upload = findViewById(R.id.upload);

        up.setOnClickListener( view -> {


            Dexter.withActivity(this)
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent,"select Image"),REQUEST_CODE);
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                            permissionToken.continuePermissionRequest();
                        }
                    }).check();


        });



        RequestQueue queue = Volley.newRequestQueue(this);


        upload.setOnClickListener(view -> {

         String name =   name_up.getText().toString();

            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Toast.makeText(MainActivity.this,"Upload Done",Toast.LENGTH_SHORT).show();

                    up = null;
                    name_up= null;


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(MainActivity.this,"Upload error"+error,Toast.LENGTH_SHORT).show();

                    Log.d("myError", "onErrorResponse: "+error);

                }
            }
            ){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> feach = new HashMap<>();
                    feach.put("images", encodeArry);
                    feach.put("name", name);
                    return feach;
                }
            };


            queue.add(request);


        });



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // The user has successfully selected an image.
            if (data != null) {
                // Get the URI of the selected image.
                Uri filePath = data.getData();
                // For example, you can use data.getData() to get the URI.
                try {
                    InputStream inputStream = getContentResolver().openInputStream(filePath);

                    bitmap = BitmapFactory.decodeStream(inputStream);
                    // Convert the InputStream to a Bitmap and set it to the ImageView
                    up.setImageBitmap(bitmap);
                    ImageConvert(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Here, we'll just display the URI as a string in the EditText.
//                String selectedImageUri = data.getDataString();
//                name_up.setText(selectedImageUri);
            }
        }
    }


    private void ImageConvert(Bitmap bit){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG,100,stream);
        byte[] imgByt = stream.toByteArray();
        encodeArry = android.util.Base64.encodeToString(imgByt, Base64.DEFAULT);

    }




}