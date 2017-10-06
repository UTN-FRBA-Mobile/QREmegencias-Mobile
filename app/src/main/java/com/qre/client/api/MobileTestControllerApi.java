package com.qre.client.api;

import com.qre.client.CollectionFormats.*;



import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;

import com.qre.models.ApiError;
import com.qre.models.PublicKeyDTO;
import com.qre.models.VerificationDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public interface MobileTestControllerApi {
  /**
   * uploadPublicKey
   * 
   * @param body body (required)
   * @return Call&lt;Void&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @PUT("api/mobile/upload")
  Call<Void> uploadPublicKeyUsingPUT(
    @retrofit2.http.Body PublicKeyDTO body
  );

  /**
   * verifySignature
   * 
   * @param body body (required)
   * @return Call&lt;Boolean&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @POST("api/mobile/verify")
  Call<Boolean> verifySignatureUsingPOST(
    @retrofit2.http.Body VerificationDTO body
  );

}
