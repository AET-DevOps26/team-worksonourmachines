package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SharedMarketplaceTopicInput
 */

@JsonTypeName("Shared.Marketplace.TopicInput")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class SharedMarketplaceTopicInput {

  private String name;

  private String description;

  private String difficultyHint;

  public SharedMarketplaceTopicInput() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SharedMarketplaceTopicInput(String name, String description, String difficultyHint) {
    this.name = name;
    this.description = description;
    this.difficultyHint = difficultyHint;
  }

  public SharedMarketplaceTopicInput name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  @NotNull 
  @Schema(name = "name", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  public SharedMarketplaceTopicInput description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   */
  @NotNull 
  @Schema(name = "description", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  @JsonProperty("description")
  public void setDescription(String description) {
    this.description = description;
  }

  public SharedMarketplaceTopicInput difficultyHint(String difficultyHint) {
    this.difficultyHint = difficultyHint;
    return this;
  }

  /**
   * Get difficultyHint
   * @return difficultyHint
   */
  @NotNull 
  @Schema(name = "difficultyHint", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("difficultyHint")
  public String getDifficultyHint() {
    return difficultyHint;
  }

  @JsonProperty("difficultyHint")
  public void setDifficultyHint(String difficultyHint) {
    this.difficultyHint = difficultyHint;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SharedMarketplaceTopicInput sharedMarketplaceTopicInput = (SharedMarketplaceTopicInput) o;
    return Objects.equals(this.name, sharedMarketplaceTopicInput.name) &&
        Objects.equals(this.description, sharedMarketplaceTopicInput.description) &&
        Objects.equals(this.difficultyHint, sharedMarketplaceTopicInput.difficultyHint);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description, difficultyHint);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedMarketplaceTopicInput {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    difficultyHint: ").append(toIndentedString(difficultyHint)).append("\n");
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

