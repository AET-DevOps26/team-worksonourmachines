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
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SharedMarketplaceAdminModuleInput
 */

@JsonTypeName("Shared.Marketplace.AdminModuleInput")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class SharedMarketplaceAdminModuleInput {

  private String code;

  private String title;

  private String description;

  private String difficultyHint;

  @Valid
  private List<@Valid SharedMarketplaceTopicInput> topics = new ArrayList<>();

  public SharedMarketplaceAdminModuleInput() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SharedMarketplaceAdminModuleInput(String code, String title, String description, String difficultyHint, List<@Valid SharedMarketplaceTopicInput> topics) {
    this.code = code;
    this.title = title;
    this.description = description;
    this.difficultyHint = difficultyHint;
    this.topics = topics;
  }

  public SharedMarketplaceAdminModuleInput code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Get code
   * @return code
   */
  @NotNull 
  @JsonProperty("code")
  public String getCode() {
    return code;
  }

  @JsonProperty("code")
  public void setCode(String code) {
    this.code = code;
  }

  public SharedMarketplaceAdminModuleInput title(String title) {
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

  public SharedMarketplaceAdminModuleInput description(String description) {
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

  public SharedMarketplaceAdminModuleInput difficultyHint(String difficultyHint) {
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

  public SharedMarketplaceAdminModuleInput topics(List<@Valid SharedMarketplaceTopicInput> topics) {
    this.topics = topics;
    return this;
  }

  public SharedMarketplaceAdminModuleInput addTopicsItem(SharedMarketplaceTopicInput topicsItem) {
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
    SharedMarketplaceAdminModuleInput sharedMarketplaceAdminModuleInput = (SharedMarketplaceAdminModuleInput) o;
    return Objects.equals(this.code, sharedMarketplaceAdminModuleInput.code) &&
        Objects.equals(this.title, sharedMarketplaceAdminModuleInput.title) &&
        Objects.equals(this.description, sharedMarketplaceAdminModuleInput.description) &&
        Objects.equals(this.difficultyHint, sharedMarketplaceAdminModuleInput.difficultyHint) &&
        Objects.equals(this.topics, sharedMarketplaceAdminModuleInput.topics);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, title, description, difficultyHint, topics);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedMarketplaceAdminModuleInput {\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
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

