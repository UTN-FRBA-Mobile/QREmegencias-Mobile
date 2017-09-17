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
import com.qre.models.ChangeDTO;


import java.io.IOException;
import org.threeten.bp.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.os.Parcelable;
import android.os.Parcel;

/**
 * ChangesDTO
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2017-09-10T21:22:21.097-03:00")
public class ChangesDTO implements Parcelable {
  @SerializedName("author")
  private String author = null;

  @SerializedName("changes")
  private Map<String, List<ChangeDTO>> changes = null;

  @SerializedName("date")
  private OffsetDateTime date = null;

  @SerializedName("id")
  private String id = null;

  public ChangesDTO author(String author) {
    this.author = author;
    return this;
  }

   /**
   * Get author
   * @return author
  **/

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public ChangesDTO changes(Map<String, List<ChangeDTO>> changes) {
    this.changes = changes;
    return this;
  }

  public ChangesDTO putChangesItem(String key, List<ChangeDTO> changesItem) {
    if (this.changes == null) {
      this.changes = new HashMap<String, List<ChangeDTO>>();
    }
    this.changes.put(key, changesItem);
    return this;
  }

   /**
   * Get changes
   * @return changes
  **/

  public Map<String, List<ChangeDTO>> getChanges() {
    return changes;
  }

  public void setChanges(Map<String, List<ChangeDTO>> changes) {
    this.changes = changes;
  }

  public ChangesDTO date(OffsetDateTime date) {
    this.date = date;
    return this;
  }

   /**
   * Get date
   * @return date
  **/

  public OffsetDateTime getDate() {
    return date;
  }

  public void setDate(OffsetDateTime date) {
    this.date = date;
  }

  public ChangesDTO id(String id) {
    this.id = id;
    return this;
  }

   /**
   * Get id
   * @return id
  **/

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChangesDTO changesDTO = (ChangesDTO) o;
    return Objects.equals(this.author, changesDTO.author) &&
        Objects.equals(this.changes, changesDTO.changes) &&
        Objects.equals(this.date, changesDTO.date) &&
        Objects.equals(this.id, changesDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(author, changes, date, id);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChangesDTO {\n");
    
    sb.append("    author: ").append(toIndentedString(author)).append("\n");
    sb.append("    changes: ").append(toIndentedString(changes)).append("\n");
    sb.append("    date: ").append(toIndentedString(date)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
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
     
    out.writeValue(author);

    out.writeValue(changes);

    out.writeValue(date);

    out.writeValue(id);
  }

  public ChangesDTO() {
    super();
  }

  ChangesDTO(Parcel in) {
    
    author = (String)in.readValue(null);
    changes = (Map<String, List<ChangeDTO>>)in.readValue(List.class.getClassLoader());
    date = (OffsetDateTime)in.readValue(OffsetDateTime.class.getClassLoader());
    id = (String)in.readValue(null);
  }
  
  public int describeContents() {
    return 0;
  }

  public static final Creator<ChangesDTO> CREATOR = new Creator<ChangesDTO>() {
    public ChangesDTO createFromParcel(Parcel in) {
      return new ChangesDTO(in);
    }
    public ChangesDTO[] newArray(int size) {
      return new ChangesDTO[size];
    }
  };
}
