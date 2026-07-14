package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openapitools.model.SharedStudentGeneratedPlanMilestone;
import org.openapitools.model.SharedStudentGeneratedPlanTutor;
import org.openapitools.model.SharedStudentPlanTier;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SharedStudentGeneratedPlanSuggestion
 */

@JsonTypeName("Shared.Student.GeneratedPlanSuggestion")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class SharedStudentGeneratedPlanSuggestion {

  private SharedStudentPlanTier tier;

  private String description;

  private Integer totalEstimatedCost;

  @Valid
  private List<@Valid SharedStudentGeneratedPlanTutor> proposedTutors = new ArrayList<>();

  @Valid
  private List<@Valid SharedStudentGeneratedPlanMilestone> milestones = new ArrayList<>();

  public SharedStudentGeneratedPlanSuggestion() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SharedStudentGeneratedPlanSuggestion(SharedStudentPlanTier tier, String description, Integer totalEstimatedCost, List<@Valid SharedStudentGeneratedPlanTutor> proposedTutors, List<@Valid SharedStudentGeneratedPlanMilestone> milestones) {
    this.tier = tier;
    this.description = description;
    this.totalEstimatedCost = totalEstimatedCost;
    this.proposedTutors = proposedTutors;
    this.milestones = milestones;
  }

  public SharedStudentGeneratedPlanSuggestion tier(SharedStudentPlanTier tier) {
    this.tier = tier;
    return this;
  }

  /**
   * Get tier
   * @return tier
   */
  @NotNull @Valid 
  @JsonProperty("tier")
  public SharedStudentPlanTier getTier() {
    return tier;
  }

  @JsonProperty("tier")
  public void setTier(SharedStudentPlanTier tier) {
    this.tier = tier;
  }

  public SharedStudentGeneratedPlanSuggestion description(String description) {
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

  public SharedStudentGeneratedPlanSuggestion totalEstimatedCost(Integer totalEstimatedCost) {
    this.totalEstimatedCost = totalEstimatedCost;
    return this;
  }

  /**
   * Total estimated cost in EUR (whole euros).
   * minimum: 0
   * @return totalEstimatedCost
   */
  @NotNull @Min(value = 0) 
  @JsonProperty("totalEstimatedCost")
  public Integer getTotalEstimatedCost() {
    return totalEstimatedCost;
  }

  @JsonProperty("totalEstimatedCost")
  public void setTotalEstimatedCost(Integer totalEstimatedCost) {
    this.totalEstimatedCost = totalEstimatedCost;
  }

  public SharedStudentGeneratedPlanSuggestion proposedTutors(List<@Valid SharedStudentGeneratedPlanTutor> proposedTutors) {
    this.proposedTutors = proposedTutors;
    return this;
  }

  public SharedStudentGeneratedPlanSuggestion addProposedTutorsItem(SharedStudentGeneratedPlanTutor proposedTutorsItem) {
    if (this.proposedTutors == null) {
      this.proposedTutors = new ArrayList<>();
    }
    this.proposedTutors.add(proposedTutorsItem);
    return this;
  }

  /**
   * Get proposedTutors
   * @return proposedTutors
   */
  @NotNull @Valid 
  @JsonProperty("proposedTutors")
  public List<@Valid SharedStudentGeneratedPlanTutor> getProposedTutors() {
    return proposedTutors;
  }

  @JsonProperty("proposedTutors")
  public void setProposedTutors(List<@Valid SharedStudentGeneratedPlanTutor> proposedTutors) {
    this.proposedTutors = proposedTutors;
  }

  public SharedStudentGeneratedPlanSuggestion milestones(List<@Valid SharedStudentGeneratedPlanMilestone> milestones) {
    this.milestones = milestones;
    return this;
  }

  public SharedStudentGeneratedPlanSuggestion addMilestonesItem(SharedStudentGeneratedPlanMilestone milestonesItem) {
    if (this.milestones == null) {
      this.milestones = new ArrayList<>();
    }
    this.milestones.add(milestonesItem);
    return this;
  }

  /**
   * Get milestones
   * @return milestones
   */
  @NotNull @Valid 
  @JsonProperty("milestones")
  public List<@Valid SharedStudentGeneratedPlanMilestone> getMilestones() {
    return milestones;
  }

  @JsonProperty("milestones")
  public void setMilestones(List<@Valid SharedStudentGeneratedPlanMilestone> milestones) {
    this.milestones = milestones;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SharedStudentGeneratedPlanSuggestion sharedStudentGeneratedPlanSuggestion = (SharedStudentGeneratedPlanSuggestion) o;
    return Objects.equals(this.tier, sharedStudentGeneratedPlanSuggestion.tier) &&
        Objects.equals(this.description, sharedStudentGeneratedPlanSuggestion.description) &&
        Objects.equals(this.totalEstimatedCost, sharedStudentGeneratedPlanSuggestion.totalEstimatedCost) &&
        Objects.equals(this.proposedTutors, sharedStudentGeneratedPlanSuggestion.proposedTutors) &&
        Objects.equals(this.milestones, sharedStudentGeneratedPlanSuggestion.milestones);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tier, description, totalEstimatedCost, proposedTutors, milestones);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedStudentGeneratedPlanSuggestion {\n");
    sb.append("    tier: ").append(toIndentedString(tier)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    totalEstimatedCost: ").append(toIndentedString(totalEstimatedCost)).append("\n");
    sb.append("    proposedTutors: ").append(toIndentedString(proposedTutors)).append("\n");
    sb.append("    milestones: ").append(toIndentedString(milestones)).append("\n");
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

