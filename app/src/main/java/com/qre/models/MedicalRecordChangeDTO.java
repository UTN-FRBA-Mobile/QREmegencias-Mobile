/*
 * QR Emergencias WS
 * API Rest QR Emergencias
 *
 * OpenAPI spec version: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package com.qre.models;

import java.util.Objects;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;


import java.io.IOException;
import android.os.Parcelable;
import android.os.Parcel;

import org.threeten.bp.OffsetDateTime;

/**
 * MedicalRecordChangeDTO
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2017-09-10T21:22:21.097-03:00")
public class MedicalRecordChangeDTO implements Parcelable {
  @SerializedName("action")
  private String action = null;

  @SerializedName("modifiedBy")
  private String modifiedBy = null;

  @SerializedName("timestamp")
  private OffsetDateTime timestamp = null;

  public MedicalRecordChangeDTO action(String action) {
    this.action = action;
    return this;
  }

   /**
   * Get action
   * @return action
  **/

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public MedicalRecordChangeDTO modifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
    return this;
  }

   /**
   * Get modifiedBy
   * @return modifiedBy
  **/

  public String getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public MedicalRecordChangeDTO timestamp(OffsetDateTime timestamp) {
    this.timestamp = timestamp;
    return this;
  }

   /**
   * Get timestamp
   * @return timestamp
  **/

  public OffsetDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(OffsetDateTime timestamp) {
    this.timestamp = timestamp;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MedicalRecordChangeDTO medicalRecordChangeDTO = (MedicalRecordChangeDTO) o;
    return Objects.equals(this.action, medicalRecordChangeDTO.action) &&
        Objects.equals(this.modifiedBy, medicalRecordChangeDTO.modifiedBy) &&
        Objects.equals(this.timestamp, medicalRecordChangeDTO.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(action, modifiedBy, timestamp);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MedicalRecordChangeDTO {\n");
    
    sb.append("    action: ").append(toIndentedString(action)).append("\n");
    sb.append("    modifiedBy: ").append(toIndentedString(modifiedBy)).append("\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
  
  public void writeToParcel(Parcel out, int flags) {
     
    out.writeValue(action);

    out.writeValue(modifiedBy);

    out.writeValue(timestamp);
  }

  public MedicalRecordChangeDTO() {
    super();
  }

  MedicalRecordChangeDTO(Parcel in) {
    
    action = (String)in.readValue(null);
    modifiedBy = (String)in.readValue(null);
    timestamp = (OffsetDateTime)in.readValue(OffsetDateTime.class.getClassLoader());
  }
  
  public int describeContents() {
    return 0;
  }

  public static final Creator<MedicalRecordChangeDTO> CREATOR = new Creator<MedicalRecordChangeDTO>() {
    public MedicalRecordChangeDTO createFromParcel(Parcel in) {
      return new MedicalRecordChangeDTO(in);
    }
    public MedicalRecordChangeDTO[] newArray(int size) {
      return new MedicalRecordChangeDTO[size];
    }
  };
}

