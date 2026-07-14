package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openapitools.model.SharedStudentGeneratedPlanSuggestion;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SharedStudentGeneratedPlan
 */

@JsonTypeName("Shared.Student.GeneratedPlan")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class SharedStudentGeneratedPlan {

  private String id;

  private String learningGoalId;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  @Valid
  private List<@Valid SharedStudentGeneratedPlanSuggestion> suggestions = new ArrayList<>();

  public SharedStudentGeneratedPlan() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SharedStudentGeneratedPlan(String id, String learningGoalId, OffsetDateTime createdAt, List<@Valid SharedStudentGeneratedPlanSuggestion> suggestions) {
    this.id = id;
    this.learningGoalId = learningGoalId;
    this.createdAt = createdAt;
    this.suggestions = suggestions;
  }

  public SharedStudentGeneratedPlan id(String id) {
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

  public SharedStudentGeneratedPlan learningGoalId(String learningGoalId) {
    this.learningGoalId = learningGoalId;
    return this;
  }

  /**
   * Get learningGoalId
   * @return learningGoalId
   */
  @NotNull 
  @JsonProperty("learningGoalId")
  public String getLearningGoalId() {
    return learningGoalId;
  }

  @JsonProperty("learningGoalId")
  public void setLearningGoalId(String learningGoalId) {
    this.learningGoalId = learningGoalId;
  }

  public SharedStudentGeneratedPlan createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Get createdAt
   * @return createdAt
   */
  @NotNull @Valid 
  @JsonProperty("createdAt")
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  @JsonProperty("createdAt")
  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public SharedStudentGeneratedPlan suggestions(List<@Valid SharedStudentGeneratedPlanSuggestion> suggestions) {
    this.suggestions = suggestions;
    return this;
  }

  public SharedStudentGeneratedPlan addSuggestionsItem(SharedStudentGeneratedPlanSuggestion suggestionsItem) {
    if (this.suggestions == null) {
      this.suggestions = new ArrayList<>();
    }
    this.suggestions.add(suggestionsItem);
    return this;
  }

  /**
   * Get suggestions
   * @return suggestions
   */
  @NotNull @Valid 
  @JsonProperty("suggestions")
  public List<@Valid SharedStudentGeneratedPlanSuggestion> getSuggestions() {
    return suggestions;
  }

  @JsonProperty("suggestions")
  public void setSuggestions(List<@Valid SharedStudentGeneratedPlanSuggestion> suggestions) {
    this.suggestions = suggestions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SharedStudentGeneratedPlan sharedStudentGeneratedPlan = (SharedStudentGeneratedPlan) o;
    return Objects.equals(this.id, sharedStudentGeneratedPlan.id) &&
        Objects.equals(this.learningGoalId, sharedStudentGeneratedPlan.learningGoalId) &&
        Objects.equals(this.createdAt, sharedStudentGeneratedPlan.createdAt) &&
        Objects.equals(this.suggestions, sharedStudentGeneratedPlan.suggestions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, learningGoalId, createdAt, suggestions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedStudentGeneratedPlan {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    learningGoalId: ").append(toIndentedString(learningGoalId)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    suggestions: ").append(toIndentedString(suggestions)).append("\n");
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

