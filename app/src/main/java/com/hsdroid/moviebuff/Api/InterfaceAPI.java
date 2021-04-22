package com.hsdroid.moviebuff.Api;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;

public interface InterfaceAPI {

    String api_key = "YOUR_API_KEY_HERE";

    @GET("popular?api_key=" + api_key)
    Call<JsonObject> getMovie();

}
