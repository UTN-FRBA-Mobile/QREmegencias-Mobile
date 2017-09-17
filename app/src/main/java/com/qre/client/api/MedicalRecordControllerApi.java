package com.qre.client.api;

import com.qre.client.CollectionFormats.*;



import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;

import com.qre.models.ApiError;
import java.io.File;
import org.threeten.bp.LocalDate;
import com.qre.models.MedicalRecordDTO;
import com.qre.models.PageOfMedicalRecordDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public interface MedicalRecordControllerApi {
  /**
   * create
   * 
   * @param name  (optional)
   * @param text  (optional)
   * @param performed  (optional)
   * @param user  (optional)
   * @param file file (optional)
   * @return Call&lt;Map&lt;String, String&gt;&gt;
   */
  @Multipart
  @POST("api/medicalRecord")
  Call<Map<String, String>> createUsingPOST(
          @Query("name") String name, @Query("text") String text, @Query("performed") LocalDate performed, @Query("user") String user, @Part("file\"; filename=\"file") RequestBody file
  );

  /**
   * delete
   * 
   * @param id id (required)
   * @return Call&lt;Void&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @DELETE("api/medicalRecord/{id}")
  Call<Void> deleteUsingDELETE(
          @Path("id") String id
  );

  /**
   * findById
   * 
   * @param id id (required)
   * @return Call&lt;MedicalRecordDTO&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @GET("api/medicalRecord/{id}")
  Call<MedicalRecordDTO> findByIdUsingGET(
          @Path("id") String id
  );

  /**
   * listMyRecords
   * 
   * @param page Results page you want to retrieve (0..N) (optional)
   * @param size Number of records per page (optional)
   * @param sort Sorting criteria in the format: property(,asc|desc). Default sort order is ascending. Multiple sort criteria are supported. (optional)
   * @return Call&lt;PageOfMedicalRecordDTO&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @GET("api/medicalRecord")
  Call<PageOfMedicalRecordDTO> listMyRecordsUsingGET(
          @Query("page") Integer page, @Query("size") Integer size, @Query("sort") List<String> sort
  );

  /**
   * listPatientRecords
   * 
   * @param username username (required)
   * @param page Results page you want to retrieve (0..N) (optional)
   * @param size Number of records per page (optional)
   * @param sort Sorting criteria in the format: property(,asc|desc). Default sort order is ascending. Multiple sort criteria are supported. (optional)
   * @return Call&lt;PageOfMedicalRecordDTO&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @GET("api/medicalRecord/user")
  Call<PageOfMedicalRecordDTO> listPatientRecordsUsingGET(
          @Query("username") String username, @Query("page") Integer page, @Query("size") Integer size, @Query("sort") List<String> sort
  );

  /**
   * update
   * 
   * @param id id (required)
   * @param medicalRecord medicalRecord (required)
   * @return Call&lt;Void&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @PATCH("api/medicalRecord/{id}")
  Call<Void> updateUsingPATCH(
          @Path("id") String id, @Body MedicalRecordDTO medicalRecord
  );

}
