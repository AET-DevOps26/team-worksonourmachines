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
 * SharedStudentGeneratedPlanMilestone
 */

@JsonTypeName("Shared.Student.GeneratedPlanMilestone")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class SharedStudentGeneratedPlanMilestone {

  private String title;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime dueDate;

  private String topicId;

  private String tutorId;

  private Integer estimatedCost;

  public SharedStudentGeneratedPlanMilestone() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SharedStudentGeneratedPlanMilestone(String title, OffsetDateTime dueDate, String topicId, String tutorId, Integer estimatedCost) {
    this.title = title;
    this.dueDate = dueDate;
    this.topicId = topicId;
    this.tutorId = tutorId;
    this.estimatedCost = estimatedCost;
  }

  public SharedStudentGeneratedPlanMilestone title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
   */
  @NotNull 
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  @JsonProperty("title")
  public void setTitle(String title) {
    this.title = title;
  }

  public SharedStudentGeneratedPlanMilestone dueDate(OffsetDateTime dueDate) {
    this.dueDate = dueDate;
    return this;
  }

  /**
   * Get dueDate
   * @return dueDate
   */
  @NotNull @Valid 
  @JsonProperty("dueDate")
  public OffsetDateTime getDueDate() {
    return dueDate;
  }

  @JsonProperty("dueDate")
  public void setDueDate(OffsetDateTime dueDate) {
    this.dueDate = dueDate;
  }

  public SharedStudentGeneratedPlanMilestone topicId(String topicId) {
    this.topicId = topicId;
    return this;
  }

  /**
   * Get topicId
   * @return topicId
   */
  @NotNull 
  @JsonProperty("topicId")
  public String getTopicId() {
    return topicId;
  }

  @JsonProperty("topicId")
  public void setTopicId(String topicId) {
    this.topicId = topicId;
  }

  public SharedStudentGeneratedPlanMilestone tutorId(String tutorId) {
    this.tutorId = tutorId;
    return this;
  }

  /**
   * Get tutorId
   * @return tutorId
   */
  @NotNull 
  @JsonProperty("tutorId")
  public String getTutorId() {
    return tutorId;
  }

  @JsonProperty("tutorId")
  public void setTutorId(String tutorId) {
    this.tutorId = tutorId;
  }

  public SharedStudentGeneratedPlanMilestone estimatedCost(Integer estimatedCost) {
    this.estimatedCost = estimatedCost;
    return this;
  }

  /**
   * Estimated cost in EUR (whole euros).
   * minimum: 0
   * @return estimatedCost
   */
  @NotNull @Min(value = 0) 
  @JsonProperty("estimatedCost")
  public Integer getEstimatedCost() {
    return estimatedCost;
  }

  @JsonProperty("estimatedCost")
  public void setEstimatedCost(Integer estimatedCost) {
    this.estimatedCost = estimatedCost;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SharedStudentGeneratedPlanMilestone sharedStudentGeneratedPlanMilestone = (SharedStudentGeneratedPlanMilestone) o;
    return Objects.equals(this.title, sharedStudentGeneratedPlanMilestone.title) &&
        Objects.equals(this.dueDate, sharedStudentGeneratedPlanMilestone.dueDate) &&
        Objects.equals(this.topicId, sharedStudentGeneratedPlanMilestone.topicId) &&
        Objects.equals(this.tutorId, sharedStudentGeneratedPlanMilestone.tutorId) &&
        Objects.equals(this.estimatedCost, sharedStudentGeneratedPlanMilestone.estimatedCost);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, dueDate, topicId, tutorId, estimatedCost);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedStudentGeneratedPlanMilestone {\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    dueDate: ").append(toIndentedString(dueDate)).append("\n");
    sb.append("    topicId: ").append(toIndentedString(topicId)).append("\n");
    sb.append("    tutorId: ").append(toIndentedString(tutorId)).append("\n");
    sb.append("    estimatedCost: ").append(toIndentedString(estimatedCost)).append("\n");
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

