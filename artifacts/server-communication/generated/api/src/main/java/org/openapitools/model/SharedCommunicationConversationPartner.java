package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SharedCommunicationConversationPartner
 */

@JsonTypeName("Shared.Communication.ConversationPartner")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class SharedCommunicationConversationPartner {

  private String userId;

  private String displayName;

  private @Nullable String tutorId;

  public SharedCommunicationConversationPartner() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SharedCommunicationConversationPartner(String userId, String displayName) {
    this.userId = userId;
    this.displayName = displayName;
  }

  public SharedCommunicationConversationPartner userId(String userId) {
    this.userId = userId;
    return this;
  }

  /**
   * Get userId
   * @return userId
   */
  @NotNull 
  @Schema(name = "userId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("userId")
  public String getUserId() {
    return userId;
  }

  @JsonProperty("userId")
  public void setUserId(String userId) {
    this.userId = userId;
  }

  public SharedCommunicationConversationPartner displayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

  /**
   * Get displayName
   * @return displayName
   */
  @NotNull 
  @Schema(name = "displayName", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("displayName")
  public String getDisplayName() {
    return displayName;
  }

  @JsonProperty("displayName")
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public SharedCommunicationConversationPartner tutorId(@Nullable String tutorId) {
    this.tutorId = tutorId;
    return this;
  }

  /**
   * Get tutorId
   * @return tutorId
   */
  
  @Schema(name = "tutorId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tutorId")
  public @Nullable String getTutorId() {
    return tutorId;
  }

  @JsonProperty("tutorId")
  public void setTutorId(@Nullable String tutorId) {
    this.tutorId = tutorId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SharedCommunicationConversationPartner sharedCommunicationConversationPartner = (SharedCommunicationConversationPartner) o;
    return Objects.equals(this.userId, sharedCommunicationConversationPartner.userId) &&
        Objects.equals(this.displayName, sharedCommunicationConversationPartner.displayName) &&
        Objects.equals(this.tutorId, sharedCommunicationConversationPartner.tutorId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, displayName, tutorId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedCommunicationConversationPartner {\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
    sb.append("    tutorId: ").append(toIndentedString(tutorId)).append("\n");
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

