package com.paysys.indoMojaloopMarchant.api;

import com.paysys.indoMojaloopMarchant.model.Request.CreditUnionListRequest;
import com.paysys.indoMojaloopMarchant.model.Request.GenerateOtpRequest;
import com.paysys.indoMojaloopMarchant.model.Request.RegistrationAliasRequest;
import com.paysys.indoMojaloopMarchant.model.Request.UsernameAvailabilityRequest;
import com.paysys.indoMojaloopMarchant.model.Request.ValidateOTPRequest;
import com.paysys.indoMojaloopMarchant.model.Request.ValidateUserRequest;
import com.paysys.indoMojaloopMarchant.model.Respose.AuthenticateResponse;
import com.paysys.indoMojaloopMarchant.model.Respose.CreditUnionListRespose;
import com.paysys.indoMojaloopMarchant.model.Respose.GenerateOTPResponse;
import com.paysys.indoMojaloopMarchant.model.Respose.GenericResponse;
import com.paysys.indoMojaloopMarchant.model.Respose.RegistrationAliasResponse;
import com.paysys.indoMojaloopMarchant.model.Respose.UsernameAvailabiliyRespose;
import com.paysys.indoMojaloopMarchant.model.Respose.ValidateOTPResponse;
import com.paysys.indoMojaloopMarchant.model.Respose.ValidateUserResponse;

import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.Call;
import retrofit2.http.Header;

public interface MojaLoopPublicApi {
    @POST("api/v1/authenticate")
    Call<GenericResponse<AuthenticateResponse>> authenticate(@Header("X-Auth-Username") String username, @Header("X-Auth-Password") String password, @Header("X-Device-ID") String deviceid, @Header("X-App-Version") String app_version, @Header("X-Device-Type") String deviceType, @Header("X-Channel") String channel, @Header("X-Customer-Type") String customerType, @Header("X-Device-Token") String deviceToken);

    @POST("api/v1/register/checkUsernameAvailability")
    Call<GenericResponse<UsernameAvailabiliyRespose>> usernameAvailabiliy(@Body UsernameAvailabilityRequest requestObj);

    @POST("api/v1/register/validateUser")
    Call<GenericResponse<ValidateUserResponse>> validateUser(@Body ValidateUserRequest requestObj);
//
//    @POST("api/v1/register/resendOTP")
//    Call<GenericResponse<SendOTPResponse>> SendOtp(@Body EnterDataRequest requestObj);

    @POST("api/v1/register/registerAlias")
    Call<GenericResponse<RegistrationAliasResponse>> registrationAlias(@Body RegistrationAliasRequest requestObj);

    @POST("api/v1/register/creditUnionList")
    Call<GenericResponse<CreditUnionListRespose>> creditUnionList(@Body CreditUnionListRequest requestObj);

    @POST("api/v1/register/validateOTP")
    Call<GenericResponse<ValidateOTPResponse>> validateOTP(@Body ValidateOTPRequest requestObj);


    @POST("api/v1/test/otp")
    Call<GenericResponse<GenerateOTPResponse>> generateOTP(@Body GenerateOtpRequest requestObj);
}
