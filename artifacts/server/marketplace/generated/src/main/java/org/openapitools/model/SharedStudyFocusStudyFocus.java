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
 * Ratings for study-skill dimensions on a 1 (low) to 5 (high) scale.
 */

@JsonTypeName("Shared.StudyFocus.StudyFocus")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class SharedStudyFocusStudyFocus {

  private Integer memorization;

  private Integer formalReasoning;

  private Integer conceptualUnderstanding;

  private Integer problemSolving;

  public SharedStudyFocusStudyFocus() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SharedStudyFocusStudyFocus(Integer memorization, Integer formalReasoning, Integer conceptualUnderstanding, Integer problemSolving) {
    this.memorization = memorization;
    this.formalReasoning = formalReasoning;
    this.conceptualUnderstanding = conceptualUnderstanding;
    this.problemSolving = problemSolving;
  }

  public SharedStudyFocusStudyFocus memorization(Integer memorization) {
    this.memorization = memorization;
    return this;
  }

  /**
   * Recalling definitions, formulas, and terminology.
   * minimum: 1
   * maximum: 5
   * @return memorization
   */
  @NotNull @Min(value = 1) @Max(value = 5) 
  @JsonProperty("memorization")
  public Integer getMemorization() {
    return memorization;
  }

  @JsonProperty("memorization")
  public void setMemorization(Integer memorization) {
    this.memorization = memorization;
  }

  public SharedStudyFocusStudyFocus formalReasoning(Integer formalReasoning) {
    this.formalReasoning = formalReasoning;
    return this;
  }

  /**
   * Proofs, logic, derivations, and symbolic manipulation.
   * minimum: 1
   * maximum: 5
   * @return formalReasoning
   */
  @NotNull @Min(value = 1) @Max(value = 5) 
  @JsonProperty("formalReasoning")
  public Integer getFormalReasoning() {
    return formalReasoning;
  }

  @JsonProperty("formalReasoning")
  public void setFormalReasoning(Integer formalReasoning) {
    this.formalReasoning = formalReasoning;
  }

  public SharedStudyFocusStudyFocus conceptualUnderstanding(Integer conceptualUnderstanding) {
    this.conceptualUnderstanding = conceptualUnderstanding;
    return this;
  }

  /**
   * Building mental models and understanding why concepts work.
   * minimum: 1
   * maximum: 5
   * @return conceptualUnderstanding
   */
  @NotNull @Min(value = 1) @Max(value = 5) 
  @JsonProperty("conceptualUnderstanding")
  public Integer getConceptualUnderstanding() {
    return conceptualUnderstanding;
  }

  @JsonProperty("conceptualUnderstanding")
  public void setConceptualUnderstanding(Integer conceptualUnderstanding) {
    this.conceptualUnderstanding = conceptualUnderstanding;
  }

  public SharedStudyFocusStudyFocus problemSolving(Integer problemSolving) {
    this.problemSolving = problemSolving;
    return this;
  }

  /**
   * Applying theory to exercises and exam-style problems.
   * minimum: 1
   * maximum: 5
   * @return problemSolving
   */
  @NotNull @Min(value = 1) @Max(value = 5) 
  @JsonProperty("problemSolving")
  public Integer getProblemSolving() {
    return problemSolving;
  }

  @JsonProperty("problemSolving")
  public void setProblemSolving(Integer problemSolving) {
    this.problemSolving = problemSolving;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SharedStudyFocusStudyFocus sharedStudyFocusStudyFocus = (SharedStudyFocusStudyFocus) o;
    return Objects.equals(this.memorization, sharedStudyFocusStudyFocus.memorization) &&
        Objects.equals(this.formalReasoning, sharedStudyFocusStudyFocus.formalReasoning) &&
        Objects.equals(this.conceptualUnderstanding, sharedStudyFocusStudyFocus.conceptualUnderstanding) &&
        Objects.equals(this.problemSolving, sharedStudyFocusStudyFocus.problemSolving);
  }

  @Override
  public int hashCode() {
    return Objects.hash(memorization, formalReasoning, conceptualUnderstanding, problemSolving);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedStudyFocusStudyFocus {\n");
    sb.append("    memorization: ").append(toIndentedString(memorization)).append("\n");
    sb.append("    formalReasoning: ").append(toIndentedString(formalReasoning)).append("\n");
    sb.append("    conceptualUnderstanding: ").append(toIndentedString(conceptualUnderstanding)).append("\n");
    sb.append("    problemSolving: ").append(toIndentedString(problemSolving)).append("\n");
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

