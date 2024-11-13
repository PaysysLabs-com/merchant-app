package com.paysys.indoMojaloopMarchant.api;

import com.paysys.indoMojaloopMarchant.model.Request.BalanceInquiryRequest;
import com.paysys.indoMojaloopMarchant.model.Respose.BalanceInquiryResponse;
import com.paysys.indoMojaloopMarchant.model.Respose.GenericResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MojaLoopApi {

    @POST("api/v1/my/balanceInquiry")
    Call<GenericResponse<BalanceInquiryResponse>>balanceInquiry(@Body BalanceInquiryRequest request);

}
