package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openapitools.model.SharedMarketplaceTopic;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SharedMarketplaceModuleDetail
 */

@JsonTypeName("Shared.Marketplace.ModuleDetail")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class SharedMarketplaceModuleDetail {

  private String id;

  private String code;

  private String title;

  private String description;

  private String difficultyHint;

  @Valid
  private List<@Valid SharedMarketplaceTopic> topics = new ArrayList<>();

  public SharedMarketplaceModuleDetail() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SharedMarketplaceModuleDetail(String id, String code, String title, String description, String difficultyHint, List<@Valid SharedMarketplaceTopic> topics) {
    this.id = id;
    this.code = code;
    this.title = title;
    this.description = description;
    this.difficultyHint = difficultyHint;
    this.topics = topics;
  }

  public SharedMarketplaceModuleDetail id(String id) {
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

  public SharedMarketplaceModuleDetail code(String code) {
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

  public SharedMarketplaceModuleDetail title(String title) {
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

  public SharedMarketplaceModuleDetail description(String description) {
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

  public SharedMarketplaceModuleDetail difficultyHint(String difficultyHint) {
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

  public SharedMarketplaceModuleDetail topics(List<@Valid SharedMarketplaceTopic> topics) {
    this.topics = topics;
    return this;
  }

  public SharedMarketplaceModuleDetail addTopicsItem(SharedMarketplaceTopic topicsItem) {
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
  public List<@Valid SharedMarketplaceTopic> getTopics() {
    return topics;
  }

  @JsonProperty("topics")
  public void setTopics(List<@Valid SharedMarketplaceTopic> topics) {
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
    SharedMarketplaceModuleDetail sharedMarketplaceModuleDetail = (SharedMarketplaceModuleDetail) o;
    return Objects.equals(this.id, sharedMarketplaceModuleDetail.id) &&
        Objects.equals(this.code, sharedMarketplaceModuleDetail.code) &&
        Objects.equals(this.title, sharedMarketplaceModuleDetail.title) &&
        Objects.equals(this.description, sharedMarketplaceModuleDetail.description) &&
        Objects.equals(this.difficultyHint, sharedMarketplaceModuleDetail.difficultyHint) &&
        Objects.equals(this.topics, sharedMarketplaceModuleDetail.topics);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, code, title, description, difficultyHint, topics);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedMarketplaceModuleDetail {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
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

