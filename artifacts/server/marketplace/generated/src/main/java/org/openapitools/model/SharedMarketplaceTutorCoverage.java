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
 * SharedMarketplaceTutorCoverage
 */

@JsonTypeName("Shared.Marketplace.TutorCoverage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class SharedMarketplaceTutorCoverage {

  private String moduleId;

  private String moduleCode;

  private String moduleTitle;

  private String proficiencyLevel;

  public SharedMarketplaceTutorCoverage() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SharedMarketplaceTutorCoverage(String moduleId, String moduleCode, String moduleTitle, String proficiencyLevel) {
    this.moduleId = moduleId;
    this.moduleCode = moduleCode;
    this.moduleTitle = moduleTitle;
    this.proficiencyLevel = proficiencyLevel;
  }

  public SharedMarketplaceTutorCoverage moduleId(String moduleId) {
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

  public SharedMarketplaceTutorCoverage moduleCode(String moduleCode) {
    this.moduleCode = moduleCode;
    return this;
  }

  /**
   * Get moduleCode
   * @return moduleCode
   */
  @NotNull 
  @JsonProperty("moduleCode")
  public String getModuleCode() {
    return moduleCode;
  }

  @JsonProperty("moduleCode")
  public void setModuleCode(String moduleCode) {
    this.moduleCode = moduleCode;
  }

  public SharedMarketplaceTutorCoverage moduleTitle(String moduleTitle) {
    this.moduleTitle = moduleTitle;
    return this;
  }

  /**
   * Get moduleTitle
   * @return moduleTitle
   */
  @NotNull 
  @JsonProperty("moduleTitle")
  public String getModuleTitle() {
    return moduleTitle;
  }

  @JsonProperty("moduleTitle")
  public void setModuleTitle(String moduleTitle) {
    this.moduleTitle = moduleTitle;
  }

  public SharedMarketplaceTutorCoverage proficiencyLevel(String proficiencyLevel) {
    this.proficiencyLevel = proficiencyLevel;
    return this;
  }

  /**
   * Get proficiencyLevel
   * @return proficiencyLevel
   */
  @NotNull 
  @JsonProperty("proficiencyLevel")
  public String getProficiencyLevel() {
    return proficiencyLevel;
  }

  @JsonProperty("proficiencyLevel")
  public void setProficiencyLevel(String proficiencyLevel) {
    this.proficiencyLevel = proficiencyLevel;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SharedMarketplaceTutorCoverage sharedMarketplaceTutorCoverage = (SharedMarketplaceTutorCoverage) o;
    return Objects.equals(this.moduleId, sharedMarketplaceTutorCoverage.moduleId) &&
        Objects.equals(this.moduleCode, sharedMarketplaceTutorCoverage.moduleCode) &&
        Objects.equals(this.moduleTitle, sharedMarketplaceTutorCoverage.moduleTitle) &&
        Objects.equals(this.proficiencyLevel, sharedMarketplaceTutorCoverage.proficiencyLevel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(moduleId, moduleCode, moduleTitle, proficiencyLevel);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedMarketplaceTutorCoverage {\n");
    sb.append("    moduleId: ").append(toIndentedString(moduleId)).append("\n");
    sb.append("    moduleCode: ").append(toIndentedString(moduleCode)).append("\n");
    sb.append("    moduleTitle: ").append(toIndentedString(moduleTitle)).append("\n");
    sb.append("    proficiencyLevel: ").append(toIndentedString(proficiencyLevel)).append("\n");
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

