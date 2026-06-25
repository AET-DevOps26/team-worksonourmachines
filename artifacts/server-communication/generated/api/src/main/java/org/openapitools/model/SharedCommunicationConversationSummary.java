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
 * SharedCommunicationConversationSummary
 */

@JsonTypeName("Shared.Communication.ConversationSummary")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class SharedCommunicationConversationSummary {

  private String id;

  private SharedCommunicationConversationPartner partner;

  private String lastMessage;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime lastMessageAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime updatedAt;

  public SharedCommunicationConversationSummary() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SharedCommunicationConversationSummary(String id, SharedCommunicationConversationPartner partner, String lastMessage, OffsetDateTime lastMessageAt, OffsetDateTime updatedAt) {
    this.id = id;
    this.partner = partner;
    this.lastMessage = lastMessage;
    this.lastMessageAt = lastMessageAt;
    this.updatedAt = updatedAt;
  }

  public SharedCommunicationConversationSummary id(String id) {
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

  public SharedCommunicationConversationSummary partner(SharedCommunicationConversationPartner partner) {
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

  public SharedCommunicationConversationSummary lastMessage(String lastMessage) {
    this.lastMessage = lastMessage;
    return this;
  }

  /**
   * Get lastMessage
   * @return lastMessage
   */
  @NotNull 
  @Schema(name = "lastMessage", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("lastMessage")
  public String getLastMessage() {
    return lastMessage;
  }

  @JsonProperty("lastMessage")
  public void setLastMessage(String lastMessage) {
    this.lastMessage = lastMessage;
  }

  public SharedCommunicationConversationSummary lastMessageAt(OffsetDateTime lastMessageAt) {
    this.lastMessageAt = lastMessageAt;
    return this;
  }

  /**
   * Get lastMessageAt
   * @return lastMessageAt
   */
  @NotNull @Valid 
  @Schema(name = "lastMessageAt", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("lastMessageAt")
  public OffsetDateTime getLastMessageAt() {
    return lastMessageAt;
  }

  @JsonProperty("lastMessageAt")
  public void setLastMessageAt(OffsetDateTime lastMessageAt) {
    this.lastMessageAt = lastMessageAt;
  }

  public SharedCommunicationConversationSummary updatedAt(OffsetDateTime updatedAt) {
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
    SharedCommunicationConversationSummary sharedCommunicationConversationSummary = (SharedCommunicationConversationSummary) o;
    return Objects.equals(this.id, sharedCommunicationConversationSummary.id) &&
        Objects.equals(this.partner, sharedCommunicationConversationSummary.partner) &&
        Objects.equals(this.lastMessage, sharedCommunicationConversationSummary.lastMessage) &&
        Objects.equals(this.lastMessageAt, sharedCommunicationConversationSummary.lastMessageAt) &&
        Objects.equals(this.updatedAt, sharedCommunicationConversationSummary.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, partner, lastMessage, lastMessageAt, updatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedCommunicationConversationSummary {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    partner: ").append(toIndentedString(partner)).append("\n");
    sb.append("    lastMessage: ").append(toIndentedString(lastMessage)).append("\n");
    sb.append("    lastMessageAt: ").append(toIndentedString(lastMessageAt)).append("\n");
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

