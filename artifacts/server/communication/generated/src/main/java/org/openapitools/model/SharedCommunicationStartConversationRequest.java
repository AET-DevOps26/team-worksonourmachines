package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SharedCommunicationStartConversationRequest
 */

@JsonTypeName("Shared.Communication.StartConversationRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class SharedCommunicationStartConversationRequest {

  private String participantUserId;

  public SharedCommunicationStartConversationRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SharedCommunicationStartConversationRequest(String participantUserId) {
    this.participantUserId = participantUserId;
  }

  public SharedCommunicationStartConversationRequest participantUserId(String participantUserId) {
    this.participantUserId = participantUserId;
    return this;
  }

  /**
   * Get participantUserId
   * @return participantUserId
   */
  @NotNull 
  @JsonProperty("participantUserId")
  public String getParticipantUserId() {
    return participantUserId;
  }

  @JsonProperty("participantUserId")
  public void setParticipantUserId(String participantUserId) {
    this.participantUserId = participantUserId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SharedCommunicationStartConversationRequest sharedCommunicationStartConversationRequest = (SharedCommunicationStartConversationRequest) o;
    return Objects.equals(this.participantUserId, sharedCommunicationStartConversationRequest.participantUserId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(participantUserId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedCommunicationStartConversationRequest {\n");
    sb.append("    participantUserId: ").append(toIndentedString(participantUserId)).append("\n");
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

