package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.openapitools.model.SharedMarketplaceWeekday;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SharedMarketplaceTutorAvailability
 */

@JsonTypeName("Shared.Marketplace.TutorAvailability")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class SharedMarketplaceTutorAvailability {

  private SharedMarketplaceWeekday weekday;

  private Boolean available;

  private @Nullable String note;

  public SharedMarketplaceTutorAvailability() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SharedMarketplaceTutorAvailability(SharedMarketplaceWeekday weekday, Boolean available) {
    this.weekday = weekday;
    this.available = available;
  }

  public SharedMarketplaceTutorAvailability weekday(SharedMarketplaceWeekday weekday) {
    this.weekday = weekday;
    return this;
  }

  /**
   * Get weekday
   * @return weekday
   */
  @NotNull @Valid 
  @JsonProperty("weekday")
  public SharedMarketplaceWeekday getWeekday() {
    return weekday;
  }

  @JsonProperty("weekday")
  public void setWeekday(SharedMarketplaceWeekday weekday) {
    this.weekday = weekday;
  }

  public SharedMarketplaceTutorAvailability available(Boolean available) {
    this.available = available;
    return this;
  }

  /**
   * Get available
   * @return available
   */
  @NotNull 
  @JsonProperty("available")
  public Boolean getAvailable() {
    return available;
  }

  @JsonProperty("available")
  public void setAvailable(Boolean available) {
    this.available = available;
  }

  public SharedMarketplaceTutorAvailability note(@Nullable String note) {
    this.note = note;
    return this;
  }

  /**
   * Get note
   * @return note
   */
  
  @JsonProperty("note")
  public @Nullable String getNote() {
    return note;
  }

  @JsonProperty("note")
  public void setNote(@Nullable String note) {
    this.note = note;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SharedMarketplaceTutorAvailability sharedMarketplaceTutorAvailability = (SharedMarketplaceTutorAvailability) o;
    return Objects.equals(this.weekday, sharedMarketplaceTutorAvailability.weekday) &&
        Objects.equals(this.available, sharedMarketplaceTutorAvailability.available) &&
        Objects.equals(this.note, sharedMarketplaceTutorAvailability.note);
  }

  @Override
  public int hashCode() {
    return Objects.hash(weekday, available, note);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedMarketplaceTutorAvailability {\n");
    sb.append("    weekday: ").append(toIndentedString(weekday)).append("\n");
    sb.append("    available: ").append(toIndentedString(available)).append("\n");
    sb.append("    note: ").append(toIndentedString(note)).append("\n");
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

