package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.openapitools.model.SharedMarketplaceTutorApplication;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SharedMarketplaceApproveApplicationResponse
 */

@JsonTypeName("Shared.Marketplace.ApproveApplicationResponse")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class SharedMarketplaceApproveApplicationResponse {

  private SharedMarketplaceTutorApplication application;

  private Boolean isFirstApproval;

  public SharedMarketplaceApproveApplicationResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SharedMarketplaceApproveApplicationResponse(SharedMarketplaceTutorApplication application, Boolean isFirstApproval) {
    this.application = application;
    this.isFirstApproval = isFirstApproval;
  }

  public SharedMarketplaceApproveApplicationResponse application(SharedMarketplaceTutorApplication application) {
    this.application = application;
    return this;
  }

  /**
   * Get application
   * @return application
   */
  @NotNull @Valid 
  @Schema(name = "application", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("application")
  public SharedMarketplaceTutorApplication getApplication() {
    return application;
  }

  @JsonProperty("application")
  public void setApplication(SharedMarketplaceTutorApplication application) {
    this.application = application;
  }

  public SharedMarketplaceApproveApplicationResponse isFirstApproval(Boolean isFirstApproval) {
    this.isFirstApproval = isFirstApproval;
    return this;
  }

  /**
   * Get isFirstApproval
   * @return isFirstApproval
   */
  @NotNull 
  @Schema(name = "isFirstApproval", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("isFirstApproval")
  public Boolean getIsFirstApproval() {
    return isFirstApproval;
  }

  @JsonProperty("isFirstApproval")
  public void setIsFirstApproval(Boolean isFirstApproval) {
    this.isFirstApproval = isFirstApproval;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SharedMarketplaceApproveApplicationResponse sharedMarketplaceApproveApplicationResponse = (SharedMarketplaceApproveApplicationResponse) o;
    return Objects.equals(this.application, sharedMarketplaceApproveApplicationResponse.application) &&
        Objects.equals(this.isFirstApproval, sharedMarketplaceApproveApplicationResponse.isFirstApproval);
  }

  @Override
  public int hashCode() {
    return Objects.hash(application, isFirstApproval);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedMarketplaceApproveApplicationResponse {\n");
    sb.append("    application: ").append(toIndentedString(application)).append("\n");
    sb.append("    isFirstApproval: ").append(toIndentedString(isFirstApproval)).append("\n");
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

