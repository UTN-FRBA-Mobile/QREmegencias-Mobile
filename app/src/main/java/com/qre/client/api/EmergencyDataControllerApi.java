package com.qre.client.api;

import com.qre.client.CollectionFormats.*;



import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;

import com.qre.models.ApiError;
import com.qre.models.EmergencyDataDTO;
import com.qre.models.PageOfChangesDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public interface EmergencyDataControllerApi {
  /**
   * getChanges
   * 
   * @return Call&lt;PageOfChangesDTO&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @GET("api/emergencyData/changes")
  Call<PageOfChangesDTO> getChangesUsingGET();
    

  /**
   * getEmergencyData
   * 
   * @param userId userId (required)
   * @return Call&lt;EmergencyDataDTO&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @GET("api/emergencyData")
  Call<EmergencyDataDTO> getEmergencyDataUsingGET(
          @Query("userId") String userId
  );

  /**
   * updateEmergencyData
   * 
   * @param emergencyDataDTO emergencyDataDTO (required)
   * @param userId userId (required)
   * @return Call&lt;Void&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @PATCH("api/emergencyData")
  Call<Void> updateEmergencyDataUsingPATCH(
          @Body EmergencyDataDTO emergencyDataDTO, @Query("userId") String userId
  );

  @Headers({
          "Content-Type:application/json"
  })
  @GET("api/emergencyData/{uuid}")
  Call<EmergencyDataDTO> getPublicEmergencyDataUsingGET(
          @Path("uuid") String uuid
  );


}
