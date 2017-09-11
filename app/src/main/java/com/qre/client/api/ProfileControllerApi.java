package com.qre.client.api;

import com.qre.client.CollectionFormats.*;



import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;

import com.qre.models.ApiError;
import com.qre.models.UserProfileDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public interface ProfileControllerApi {
  /**
   * list
   * 
   * @return Call&lt;UserProfileDTO&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @GET("api/profile")
  Call<UserProfileDTO> listUsingGET();
    

  /**
   * update
   * 
   * @param userProfileDTO userProfileDTO (required)
   * @return Call&lt;Void&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @PATCH("api/profile")
  Call<Void> updateUsingPATCH1(
          @Body UserProfileDTO userProfileDTO
  );

}
