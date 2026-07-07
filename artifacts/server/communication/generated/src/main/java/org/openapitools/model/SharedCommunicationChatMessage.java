package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SharedCommunicationChatMessage
 */

@JsonTypeName("Shared.Communication.ChatMessage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class SharedCommunicationChatMessage {

  private String id;

  private String senderId;

  private String content;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime sentAt;

  public SharedCommunicationChatMessage() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SharedCommunicationChatMessage(String id, String senderId, String content, OffsetDateTime sentAt) {
    this.id = id;
    this.senderId = senderId;
    this.content = content;
    this.sentAt = sentAt;
  }

  public SharedCommunicationChatMessage id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  @NotNull 
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(String id) {
    this.id = id;
  }

  public SharedCommunicationChatMessage senderId(String senderId) {
    this.senderId = senderId;
    return this;
  }

  /**
   * Get senderId
   * @return senderId
   */
  @NotNull 
  @JsonProperty("senderId")
  public String getSenderId() {
    return senderId;
  }

  @JsonProperty("senderId")
  public void setSenderId(String senderId) {
    this.senderId = senderId;
  }

  public SharedCommunicationChatMessage content(String content) {
    this.content = content;
    return this;
  }

  /**
   * Get content
   * @return content
   */
  @NotNull 
  @JsonProperty("content")
  public String getContent() {
    return content;
  }

  @JsonProperty("content")
  public void setContent(String content) {
    this.content = content;
  }

  public SharedCommunicationChatMessage sentAt(OffsetDateTime sentAt) {
    this.sentAt = sentAt;
    return this;
  }

  /**
   * Get sentAt
   * @return sentAt
   */
  @NotNull @Valid 
  @JsonProperty("sentAt")
  public OffsetDateTime getSentAt() {
    return sentAt;
  }

  @JsonProperty("sentAt")
  public void setSentAt(OffsetDateTime sentAt) {
    this.sentAt = sentAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SharedCommunicationChatMessage sharedCommunicationChatMessage = (SharedCommunicationChatMessage) o;
    return Objects.equals(this.id, sharedCommunicationChatMessage.id) &&
        Objects.equals(this.senderId, sharedCommunicationChatMessage.senderId) &&
        Objects.equals(this.content, sharedCommunicationChatMessage.content) &&
        Objects.equals(this.sentAt, sharedCommunicationChatMessage.sentAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, senderId, content, sentAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedCommunicationChatMessage {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    senderId: ").append(toIndentedString(senderId)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    sentAt: ").append(toIndentedString(sentAt)).append("\n");
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

