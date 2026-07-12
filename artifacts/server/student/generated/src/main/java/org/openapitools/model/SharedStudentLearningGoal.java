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
 * SharedStudentLearningGoal
 */

@JsonTypeName("Shared.Student.LearningGoal")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class SharedStudentLearningGoal {

  private String id;

  private String moduleId;

  private String description;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime targetDate;

  private Integer selfAssessedLevel;

  private @Nullable Integer budgetEur;

  @Valid
  private List<SharedMarketplaceLocation> locations = new ArrayList<>();

  public SharedStudentLearningGoal() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SharedStudentLearningGoal(String id, String moduleId, String description, OffsetDateTime targetDate, Integer selfAssessedLevel, List<SharedMarketplaceLocation> locations) {
    this.id = id;
    this.moduleId = moduleId;
    this.description = description;
    this.targetDate = targetDate;
    this.selfAssessedLevel = selfAssessedLevel;
    this.locations = locations;
  }

  public SharedStudentLearningGoal id(String id) {
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

  public SharedStudentLearningGoal moduleId(String moduleId) {
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

  public SharedStudentLearningGoal description(String description) {
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

  public SharedStudentLearningGoal targetDate(OffsetDateTime targetDate) {
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

  public SharedStudentLearningGoal selfAssessedLevel(Integer selfAssessedLevel) {
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

  public SharedStudentLearningGoal budgetEur(@Nullable Integer budgetEur) {
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

  public SharedStudentLearningGoal locations(List<SharedMarketplaceLocation> locations) {
    this.locations = locations;
    return this;
  }

  public SharedStudentLearningGoal addLocationsItem(SharedMarketplaceLocation locationsItem) {
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
    SharedStudentLearningGoal sharedStudentLearningGoal = (SharedStudentLearningGoal) o;
    return Objects.equals(this.id, sharedStudentLearningGoal.id) &&
        Objects.equals(this.moduleId, sharedStudentLearningGoal.moduleId) &&
        Objects.equals(this.description, sharedStudentLearningGoal.description) &&
        Objects.equals(this.targetDate, sharedStudentLearningGoal.targetDate) &&
        Objects.equals(this.selfAssessedLevel, sharedStudentLearningGoal.selfAssessedLevel) &&
        Objects.equals(this.budgetEur, sharedStudentLearningGoal.budgetEur) &&
        Objects.equals(this.locations, sharedStudentLearningGoal.locations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, moduleId, description, targetDate, selfAssessedLevel, budgetEur, locations);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedStudentLearningGoal {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
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

