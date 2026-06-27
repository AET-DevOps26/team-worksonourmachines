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
 * SharedMarketplaceModuleListItem
 */

@JsonTypeName("Shared.Marketplace.ModuleListItem")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class SharedMarketplaceModuleListItem {

  private String id;

  private String code;

  private String title;

  private String description;

  private String difficultyHint;

  public SharedMarketplaceModuleListItem() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SharedMarketplaceModuleListItem(String id, String code, String title, String description, String difficultyHint) {
    this.id = id;
    this.code = code;
    this.title = title;
    this.description = description;
    this.difficultyHint = difficultyHint;
  }

  public SharedMarketplaceModuleListItem id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  @NotNull 
  @Schema(name = "id", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(String id) {
    this.id = id;
  }

  public SharedMarketplaceModuleListItem code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Get code
   * @return code
   */
  @NotNull 
  @Schema(name = "code", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("code")
  public String getCode() {
    return code;
  }

  @JsonProperty("code")
  public void setCode(String code) {
    this.code = code;
  }

  public SharedMarketplaceModuleListItem title(String title) {
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

  public SharedMarketplaceModuleListItem description(String description) {
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

  public SharedMarketplaceModuleListItem difficultyHint(String difficultyHint) {
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
    SharedMarketplaceModuleListItem sharedMarketplaceModuleListItem = (SharedMarketplaceModuleListItem) o;
    return Objects.equals(this.id, sharedMarketplaceModuleListItem.id) &&
        Objects.equals(this.code, sharedMarketplaceModuleListItem.code) &&
        Objects.equals(this.title, sharedMarketplaceModuleListItem.title) &&
        Objects.equals(this.description, sharedMarketplaceModuleListItem.description) &&
        Objects.equals(this.difficultyHint, sharedMarketplaceModuleListItem.difficultyHint);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, code, title, description, difficultyHint);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedMarketplaceModuleListItem {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
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

