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
 * SharedStudentGeneratedPlanTutor
 */

@JsonTypeName("Shared.Student.GeneratedPlanTutor")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class SharedStudentGeneratedPlanTutor {

  private String id;

  private String displayName;

  private Integer hourlyRate;

  public SharedStudentGeneratedPlanTutor() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SharedStudentGeneratedPlanTutor(String id, String displayName, Integer hourlyRate) {
    this.id = id;
    this.displayName = displayName;
    this.hourlyRate = hourlyRate;
  }

  public SharedStudentGeneratedPlanTutor id(String id) {
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

  public SharedStudentGeneratedPlanTutor displayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

  /**
   * Get displayName
   * @return displayName
   */
  @NotNull 
  @JsonProperty("displayName")
  public String getDisplayName() {
    return displayName;
  }

  @JsonProperty("displayName")
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public SharedStudentGeneratedPlanTutor hourlyRate(Integer hourlyRate) {
    this.hourlyRate = hourlyRate;
    return this;
  }

  /**
   * Hourly rate in EUR (whole euros).
   * minimum: 0
   * @return hourlyRate
   */
  @NotNull @Min(value = 0) 
  @JsonProperty("hourlyRate")
  public Integer getHourlyRate() {
    return hourlyRate;
  }

  @JsonProperty("hourlyRate")
  public void setHourlyRate(Integer hourlyRate) {
    this.hourlyRate = hourlyRate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SharedStudentGeneratedPlanTutor sharedStudentGeneratedPlanTutor = (SharedStudentGeneratedPlanTutor) o;
    return Objects.equals(this.id, sharedStudentGeneratedPlanTutor.id) &&
        Objects.equals(this.displayName, sharedStudentGeneratedPlanTutor.displayName) &&
        Objects.equals(this.hourlyRate, sharedStudentGeneratedPlanTutor.hourlyRate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, displayName, hourlyRate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedStudentGeneratedPlanTutor {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
    sb.append("    hourlyRate: ").append(toIndentedString(hourlyRate)).append("\n");
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

