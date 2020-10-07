package com.jkero.blackhawk.testaparatuocr.api;

import com.jkero.blackhawk.testaparatuocr.Constants;

import retrofit2.http.FormUrlEncoded;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;




public interface ApiInterface {

    @GET(Constants.URL_GETALLINGREDIENTS)
    Call<List<Ingredient>> getAllIngredients();

    @FormUrlEncoded
    @POST(Constants.URL_GETINGREDIENTSBYBARCODE)
    Call<List<Ingredient>> getIngredientsByBarcode(
            @Field("barcode") String barcode);

    @FormUrlEncoded
    @POST(Constants.URL_GETINGREDIENTSBYBARCODE)
    Call<String> getIngredientsByBarcode2(
            @Field("barcode") String barcode);

    @FormUrlEncoded
    @POST(Constants.URL_CHECKIFUSEDBARCODE)
    Call<String> checkIfBarcodeUsed(
            @Field("barcode") String barcode);

    @FormUrlEncoded
    @POST(Constants.URL_CHECKIFSCANNED)
    Call<String> checkIfScanned(
            @Field("barcode") String barcode,
            @Field("device_id") String idDevice);

    @FormUrlEncoded
    @POST(Constants.URL_DOSCAN)
    Call<String> doScan(
            @Field("barcode") String barcode,
            @Field("device_id") String idDevice,
            @Field("ingredients") String ingredientsIds);

    @FormUrlEncoded
    @POST(Constants.URL_GETINGREDIENTSOFDEVICE)
    Call<List<Ingredient>> getIngredientsScannedByDevice(
            @Field("device_id") String idDevice,
            @Field("barcode") String barcode);


}
