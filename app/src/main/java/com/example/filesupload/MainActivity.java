package com.example.filesupload;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.github.dhaval2404.imagepicker.ImagePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {
    RelativeLayout progress;
    ImageView image;
    Button upload;
    String addr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image=findViewById(R.id.image);
        upload=findViewById(R.id.upload);
        progress=findViewById(R.id.progress);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(MainActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();

            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if(resultCode==RESULT_OK){
//                circle_img.setImageURI(result.getUri());
                Uri uri = data.getData();
                uri.getPath();
//                Toast.makeText(MainActivity.this,uri.toString(), Toast.LENGTH_SHORT).show();
                addr=uri.getPath();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), uri);
                    image.setImageBitmap(bitmap); //set image from image picker

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(MainActivity.this,"Try other option",Toast.LENGTH_SHORT).show();
            }
            if(requestCode==2){
                Toast.makeText(MainActivity.this,data.toString(),Toast.LENGTH_SHORT).show();
            }

    }
    private void uploadFile() {
        progress.setVisibility(View.VISIBLE);

        // Map is used to multipart the file using okhttp3.RequestBody
        File file = new File(addr);

//        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        RequestBody requestBody=RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        ApiConfig api=retrofit.create(ApiConfig.class);
        Call<String> call=api.uploadFile(fileToUpload,filename);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject data=new JSONObject(response.body());
                    if(data.getBoolean("status")){
                        Toast.makeText(MainActivity.this,data.getString("message"),Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MainActivity.this,data.getString("message"),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                progress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(MainActivity.this,t.toString(),Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);
            }
        });
    }
}