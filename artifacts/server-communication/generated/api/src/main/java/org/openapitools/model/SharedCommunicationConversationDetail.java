package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import org.openapitools.model.SharedCommunicationConversationPartner;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SharedCommunicationConversationDetail
 */

@JsonTypeName("Shared.Communication.ConversationDetail")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class SharedCommunicationConversationDetail {

  private String id;

  private SharedCommunicationConversationPartner partner;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime updatedAt;

  public SharedCommunicationConversationDetail() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SharedCommunicationConversationDetail(String id, SharedCommunicationConversationPartner partner, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
    this.id = id;
    this.partner = partner;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public SharedCommunicationConversationDetail id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  @NotNull 
  @Schema(name = "id", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(String id) {
    this.id = id;
  }

  public SharedCommunicationConversationDetail partner(SharedCommunicationConversationPartner partner) {
    this.partner = partner;
    return this;
  }

  /**
   * Get partner
   * @return partner
   */
  @NotNull @Valid 
  @Schema(name = "partner", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("partner")
  public SharedCommunicationConversationPartner getPartner() {
    return partner;
  }

  @JsonProperty("partner")
  public void setPartner(SharedCommunicationConversationPartner partner) {
    this.partner = partner;
  }

  public SharedCommunicationConversationDetail createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Get createdAt
   * @return createdAt
   */
  @NotNull @Valid 
  @Schema(name = "createdAt", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("createdAt")
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  @JsonProperty("createdAt")
  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public SharedCommunicationConversationDetail updatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * Get updatedAt
   * @return updatedAt
   */
  @NotNull @Valid 
  @Schema(name = "updatedAt", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("updatedAt")
  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  @JsonProperty("updatedAt")
  public void setUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SharedCommunicationConversationDetail sharedCommunicationConversationDetail = (SharedCommunicationConversationDetail) o;
    return Objects.equals(this.id, sharedCommunicationConversationDetail.id) &&
        Objects.equals(this.partner, sharedCommunicationConversationDetail.partner) &&
        Objects.equals(this.createdAt, sharedCommunicationConversationDetail.createdAt) &&
        Objects.equals(this.updatedAt, sharedCommunicationConversationDetail.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, partner, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedCommunicationConversationDetail {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    partner: ").append(toIndentedString(partner)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(@Nullable Object o) {
    return o == null ? "null" : o.toString().replace("\n", "\n    ");
  }
}

