package com.hsdroid.moviebuff.Api;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;

public interface InterfaceAPI {

    String api_key = "6281dfd9c4c5cc256e22103a83619be0";

    @GET("popular?api_key=" + api_key)
    Call<JsonObject> getMovie();

}
