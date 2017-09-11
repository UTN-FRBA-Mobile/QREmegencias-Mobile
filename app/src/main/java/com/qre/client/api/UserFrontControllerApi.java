package com.qre.client.api;

import com.qre.client.CollectionFormats.*;



import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;

import com.qre.models.ApiError;
import com.qre.models.CreateUserDTO;
import org.threeten.bp.LocalDate;
import com.qre.models.LoginUserDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public interface UserFrontControllerApi {
  /**
   * changePassword
   * 
   * @param id  (optional)
   * @param password  (optional)
   * @param newPassword  (optional)
   * @param confirmPassword  (optional)
   * @param recaptchaResponse  (optional)
   * @return Call&lt;Void&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @POST("api/userFront/changePassword")
  Call<Void> changePasswordUsingPOST(
          @Query("id") String id, @Query("password") String password, @Query("newPassword") String newPassword, @Query("confirmPassword") String confirmPassword, @Query("recaptchaResponse") String recaptchaResponse
  );

  /**
   * completeRegistration
   * 
   * @param token  (optional)
   * @param lastName  (optional)
   * @param name  (optional)
   * @param birthDate  (optional)
   * @param idNumber  (optional)
   * @param sex  (optional)
   * @return Call&lt;Void&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @POST("api/userFront/completeRegistration")
  Call<Void> completeRegistrationUsingPOST(
          @Query("token") String token, @Query("lastName") String lastName, @Query("name") String name, @Query("birthDate") LocalDate birthDate, @Query("idNumber") String idNumber, @Query("sex") String sex
  );

  /**
   * 
   * 
   * @param username  (required)
   * @param password  (required)
   * @param gRecaptchaResponse  (optional)
   * @return Call&lt;LoginUserDTO&gt;
   */
  @FormUrlEncoded
  @POST("api/login")
  Call<LoginUserDTO> loginUsingPOST(
          @Field("username") String username, @Field("password") String password, @Field("g-recaptcha-response") String gRecaptchaResponse
  );

  /**
   * 
   * 
   * @return Call&lt;Void&gt;
   */
  @Headers({
    "Content-Type:application/x-www-form-urlencoded"
  })
  @POST("api/logout")
  Call<Void> logoutUsingPOST();
    

  /**
   * register
   * 
   * @param model model (required)
   * @return Call&lt;Void&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @POST("api/userFront/register")
  Call<Void> registerUsingPOST(
          @Body CreateUserDTO model
  );

  /**
   * resetPassword
   * 
   * @param token  (optional)
   * @param newPassword  (optional)
   * @param confirmPassword  (optional)
   * @param recaptchaResponse  (optional)
   * @return Call&lt;Void&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @POST("api/userFront/resetPassword")
  Call<Void> resetPasswordUsingPOST(
          @Query("token") String token, @Query("newPassword") String newPassword, @Query("confirmPassword") String confirmPassword, @Query("recaptchaResponse") String recaptchaResponse
  );

  /**
   * sendForgotPassword
   * 
   * @param gRecaptchaResponse g-recaptcha-response (required)
   * @param username username (required)
   * @return Call&lt;Void&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @POST("api/userFront/sendForgotPassword")
  Call<Void> sendForgotPasswordUsingPOST(
          @Query("g-recaptcha-response") String gRecaptchaResponse, @Query("username") String username
  );

}
