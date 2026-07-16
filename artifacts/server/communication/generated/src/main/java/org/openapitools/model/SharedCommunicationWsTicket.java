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
 * SharedCommunicationWsTicket
 */

@JsonTypeName("Shared.Communication.WsTicket")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class SharedCommunicationWsTicket {

  private String ticket;

  private Integer expiresInSeconds;

  public SharedCommunicationWsTicket() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SharedCommunicationWsTicket(String ticket, Integer expiresInSeconds) {
    this.ticket = ticket;
    this.expiresInSeconds = expiresInSeconds;
  }

  public SharedCommunicationWsTicket ticket(String ticket) {
    this.ticket = ticket;
    return this;
  }

  /**
   * Get ticket
   * @return ticket
   */
  @NotNull 
  @JsonProperty("ticket")
  public String getTicket() {
    return ticket;
  }

  @JsonProperty("ticket")
  public void setTicket(String ticket) {
    this.ticket = ticket;
  }

  public SharedCommunicationWsTicket expiresInSeconds(Integer expiresInSeconds) {
    this.expiresInSeconds = expiresInSeconds;
    return this;
  }

  /**
   * Get expiresInSeconds
   * @return expiresInSeconds
   */
  @NotNull 
  @JsonProperty("expiresInSeconds")
  public Integer getExpiresInSeconds() {
    return expiresInSeconds;
  }

  @JsonProperty("expiresInSeconds")
  public void setExpiresInSeconds(Integer expiresInSeconds) {
    this.expiresInSeconds = expiresInSeconds;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SharedCommunicationWsTicket sharedCommunicationWsTicket = (SharedCommunicationWsTicket) o;
    return Objects.equals(this.ticket, sharedCommunicationWsTicket.ticket) &&
        Objects.equals(this.expiresInSeconds, sharedCommunicationWsTicket.expiresInSeconds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ticket, expiresInSeconds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedCommunicationWsTicket {\n");
    sb.append("    ticket: ").append(toIndentedString(ticket)).append("\n");
    sb.append("    expiresInSeconds: ").append(toIndentedString(expiresInSeconds)).append("\n");
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

