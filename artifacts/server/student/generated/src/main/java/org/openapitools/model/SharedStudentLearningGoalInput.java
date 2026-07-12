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
import org.openapitools.model.SharedMarketplaceLocation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * The template for omitting properties.
 */

@JsonTypeName("Shared.Student.LearningGoalInput")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class SharedStudentLearningGoalInput {

  private String moduleId;

  private String description;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime targetDate;

  private Integer selfAssessedLevel;

  private @Nullable Integer budgetEur;

  @Valid
  private List<SharedMarketplaceLocation> locations = new ArrayList<>();

  public SharedStudentLearningGoalInput() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SharedStudentLearningGoalInput(String moduleId, String description, OffsetDateTime targetDate, Integer selfAssessedLevel, List<SharedMarketplaceLocation> locations) {
    this.moduleId = moduleId;
    this.description = description;
    this.targetDate = targetDate;
    this.selfAssessedLevel = selfAssessedLevel;
    this.locations = locations;
  }

  public SharedStudentLearningGoalInput moduleId(String moduleId) {
    this.moduleId = moduleId;
    return this;
  }

  /**
   * Get moduleId
   * @return moduleId
   */
  @NotNull 
  @JsonProperty("moduleId")
  public String getModuleId() {
    return moduleId;
  }

  @JsonProperty("moduleId")
  public void setModuleId(String moduleId) {
    this.moduleId = moduleId;
  }

  public SharedStudentLearningGoalInput description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   */
  @NotNull 
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  @JsonProperty("description")
  public void setDescription(String description) {
    this.description = description;
  }

  public SharedStudentLearningGoalInput targetDate(OffsetDateTime targetDate) {
    this.targetDate = targetDate;
    return this;
  }

  /**
   * Get targetDate
   * @return targetDate
   */
  @NotNull @Valid 
  @JsonProperty("targetDate")
  public OffsetDateTime getTargetDate() {
    return targetDate;
  }

  @JsonProperty("targetDate")
  public void setTargetDate(OffsetDateTime targetDate) {
    this.targetDate = targetDate;
  }

  public SharedStudentLearningGoalInput selfAssessedLevel(Integer selfAssessedLevel) {
    this.selfAssessedLevel = selfAssessedLevel;
    return this;
  }

  /**
   * Self-assessed level from 1 (beginner) to 5 (expert).
   * minimum: 1
   * maximum: 5
   * @return selfAssessedLevel
   */
  @NotNull @Min(value = 1) @Max(value = 5) 
  @JsonProperty("selfAssessedLevel")
  public Integer getSelfAssessedLevel() {
    return selfAssessedLevel;
  }

  @JsonProperty("selfAssessedLevel")
  public void setSelfAssessedLevel(Integer selfAssessedLevel) {
    this.selfAssessedLevel = selfAssessedLevel;
  }

  public SharedStudentLearningGoalInput budgetEur(@Nullable Integer budgetEur) {
    this.budgetEur = budgetEur;
    return this;
  }

  /**
   * Budget in EUR (whole euros).
   * minimum: 0
   * @return budgetEur
   */
  @Min(value = 0) 
  @JsonProperty("budgetEur")
  public @Nullable Integer getBudgetEur() {
    return budgetEur;
  }

  @JsonProperty("budgetEur")
  public void setBudgetEur(@Nullable Integer budgetEur) {
    this.budgetEur = budgetEur;
  }

  public SharedStudentLearningGoalInput locations(List<SharedMarketplaceLocation> locations) {
    this.locations = locations;
    return this;
  }

  public SharedStudentLearningGoalInput addLocationsItem(SharedMarketplaceLocation locationsItem) {
    if (this.locations == null) {
      this.locations = new ArrayList<>();
    }
    this.locations.add(locationsItem);
    return this;
  }

  /**
   * Get locations
   * @return locations
   */
  @NotNull @Valid 
  @JsonProperty("locations")
  public List<SharedMarketplaceLocation> getLocations() {
    return locations;
  }

  @JsonProperty("locations")
  public void setLocations(List<SharedMarketplaceLocation> locations) {
    this.locations = locations;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SharedStudentLearningGoalInput sharedStudentLearningGoalInput = (SharedStudentLearningGoalInput) o;
    return Objects.equals(this.moduleId, sharedStudentLearningGoalInput.moduleId) &&
        Objects.equals(this.description, sharedStudentLearningGoalInput.description) &&
        Objects.equals(this.targetDate, sharedStudentLearningGoalInput.targetDate) &&
        Objects.equals(this.selfAssessedLevel, sharedStudentLearningGoalInput.selfAssessedLevel) &&
        Objects.equals(this.budgetEur, sharedStudentLearningGoalInput.budgetEur) &&
        Objects.equals(this.locations, sharedStudentLearningGoalInput.locations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(moduleId, description, targetDate, selfAssessedLevel, budgetEur, locations);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedStudentLearningGoalInput {\n");
    sb.append("    moduleId: ").append(toIndentedString(moduleId)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    targetDate: ").append(toIndentedString(targetDate)).append("\n");
    sb.append("    selfAssessedLevel: ").append(toIndentedString(selfAssessedLevel)).append("\n");
    sb.append("    budgetEur: ").append(toIndentedString(budgetEur)).append("\n");
    sb.append("    locations: ").append(toIndentedString(locations)).append("\n");
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

