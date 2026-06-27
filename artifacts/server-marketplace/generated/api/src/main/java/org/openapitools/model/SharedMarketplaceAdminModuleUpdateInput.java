package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openapitools.model.SharedMarketplaceTopicInput;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SharedMarketplaceAdminModuleUpdateInput
 */

@JsonTypeName("Shared.Marketplace.AdminModuleUpdateInput")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class SharedMarketplaceAdminModuleUpdateInput {

  private String title;

  private String description;

  private String difficultyHint;

  @Valid
  private List<@Valid SharedMarketplaceTopicInput> topics = new ArrayList<>();

  public SharedMarketplaceAdminModuleUpdateInput() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SharedMarketplaceAdminModuleUpdateInput(String title, String description, String difficultyHint, List<@Valid SharedMarketplaceTopicInput> topics) {
    this.title = title;
    this.description = description;
    this.difficultyHint = difficultyHint;
    this.topics = topics;
  }

  public SharedMarketplaceAdminModuleUpdateInput title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
   */
  @NotNull 
  @Schema(name = "title", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  @JsonProperty("title")
  public void setTitle(String title) {
    this.title = title;
  }

  public SharedMarketplaceAdminModuleUpdateInput description(String description) {
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

  public SharedMarketplaceAdminModuleUpdateInput difficultyHint(String difficultyHint) {
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

  public SharedMarketplaceAdminModuleUpdateInput topics(List<@Valid SharedMarketplaceTopicInput> topics) {
    this.topics = topics;
    return this;
  }

  public SharedMarketplaceAdminModuleUpdateInput addTopicsItem(SharedMarketplaceTopicInput topicsItem) {
    if (this.topics == null) {
      this.topics = new ArrayList<>();
    }
    this.topics.add(topicsItem);
    return this;
  }

  /**
   * Get topics
   * @return topics
   */
  @NotNull @Valid 
  @Schema(name = "topics", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("topics")
  public List<@Valid SharedMarketplaceTopicInput> getTopics() {
    return topics;
  }

  @JsonProperty("topics")
  public void setTopics(List<@Valid SharedMarketplaceTopicInput> topics) {
    this.topics = topics;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SharedMarketplaceAdminModuleUpdateInput sharedMarketplaceAdminModuleUpdateInput = (SharedMarketplaceAdminModuleUpdateInput) o;
    return Objects.equals(this.title, sharedMarketplaceAdminModuleUpdateInput.title) &&
        Objects.equals(this.description, sharedMarketplaceAdminModuleUpdateInput.description) &&
        Objects.equals(this.difficultyHint, sharedMarketplaceAdminModuleUpdateInput.difficultyHint) &&
        Objects.equals(this.topics, sharedMarketplaceAdminModuleUpdateInput.topics);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, description, difficultyHint, topics);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedMarketplaceAdminModuleUpdateInput {\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    difficultyHint: ").append(toIndentedString(difficultyHint)).append("\n");
    sb.append("    topics: ").append(toIndentedString(topics)).append("\n");
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

