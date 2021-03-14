package com.example.filesupload;




import org.json.JSONObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

interface ApiConfig {
    String BASE_URL="http://chandra.sportsontheweb.net/";
    @Multipart
    @POST("test2.php")
    Call<String> uploadFile(@Part MultipartBody.Part file, @Part("file") RequestBody name);
}
