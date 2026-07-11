package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.openapitools.model.SharedStudyFocusStudyFocus;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SharedMarketplaceTopic
 */

@JsonTypeName("Shared.Marketplace.Topic")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class SharedMarketplaceTopic {

  private String id;

  private String name;

  private String description;

  private String difficultyHint;

  private SharedStudyFocusStudyFocus studyFocus;

  public SharedMarketplaceTopic() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SharedMarketplaceTopic(String id, String name, String description, String difficultyHint, SharedStudyFocusStudyFocus studyFocus) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.difficultyHint = difficultyHint;
    this.studyFocus = studyFocus;
  }

  public SharedMarketplaceTopic id(String id) {
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

  public SharedMarketplaceTopic name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  @NotNull 
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  public SharedMarketplaceTopic description(String description) {
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

  public SharedMarketplaceTopic difficultyHint(String difficultyHint) {
    this.difficultyHint = difficultyHint;
    return this;
  }

  /**
   * Get difficultyHint
   * @return difficultyHint
   */
  @NotNull 
  @JsonProperty("difficultyHint")
  public String getDifficultyHint() {
    return difficultyHint;
  }

  @JsonProperty("difficultyHint")
  public void setDifficultyHint(String difficultyHint) {
    this.difficultyHint = difficultyHint;
  }

  public SharedMarketplaceTopic studyFocus(SharedStudyFocusStudyFocus studyFocus) {
    this.studyFocus = studyFocus;
    return this;
  }

  /**
   * Get studyFocus
   * @return studyFocus
   */
  @NotNull @Valid 
  @JsonProperty("studyFocus")
  public SharedStudyFocusStudyFocus getStudyFocus() {
    return studyFocus;
  }

  @JsonProperty("studyFocus")
  public void setStudyFocus(SharedStudyFocusStudyFocus studyFocus) {
    this.studyFocus = studyFocus;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SharedMarketplaceTopic sharedMarketplaceTopic = (SharedMarketplaceTopic) o;
    return Objects.equals(this.id, sharedMarketplaceTopic.id) &&
        Objects.equals(this.name, sharedMarketplaceTopic.name) &&
        Objects.equals(this.description, sharedMarketplaceTopic.description) &&
        Objects.equals(this.difficultyHint, sharedMarketplaceTopic.difficultyHint) &&
        Objects.equals(this.studyFocus, sharedMarketplaceTopic.studyFocus);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, description, difficultyHint, studyFocus);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedMarketplaceTopic {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    difficultyHint: ").append(toIndentedString(difficultyHint)).append("\n");
    sb.append("    studyFocus: ").append(toIndentedString(studyFocus)).append("\n");
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

