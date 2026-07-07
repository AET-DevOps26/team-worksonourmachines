package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.openapitools.model.SharedMarketplaceTutorProfileInput;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SharedMarketplaceSubmitTutorApplicationRequest
 */

@JsonTypeName("Shared.Marketplace.SubmitTutorApplicationRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class SharedMarketplaceSubmitTutorApplicationRequest {

  private String moduleId;

  private String certificateRef;

  private @Nullable SharedMarketplaceTutorProfileInput profile;

  public SharedMarketplaceSubmitTutorApplicationRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SharedMarketplaceSubmitTutorApplicationRequest(String moduleId, String certificateRef) {
    this.moduleId = moduleId;
    this.certificateRef = certificateRef;
  }

  public SharedMarketplaceSubmitTutorApplicationRequest moduleId(String moduleId) {
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

  public SharedMarketplaceSubmitTutorApplicationRequest certificateRef(String certificateRef) {
    this.certificateRef = certificateRef;
    return this;
  }

  /**
   * Get certificateRef
   * @return certificateRef
   */
  @NotNull 
  @JsonProperty("certificateRef")
  public String getCertificateRef() {
    return certificateRef;
  }

  @JsonProperty("certificateRef")
  public void setCertificateRef(String certificateRef) {
    this.certificateRef = certificateRef;
  }

  public SharedMarketplaceSubmitTutorApplicationRequest profile(@Nullable SharedMarketplaceTutorProfileInput profile) {
    this.profile = profile;
    return this;
  }

  /**
   * Get profile
   * @return profile
   */
  @Valid 
  @JsonProperty("profile")
  public @Nullable SharedMarketplaceTutorProfileInput getProfile() {
    return profile;
  }

  @JsonProperty("profile")
  public void setProfile(@Nullable SharedMarketplaceTutorProfileInput profile) {
    this.profile = profile;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SharedMarketplaceSubmitTutorApplicationRequest sharedMarketplaceSubmitTutorApplicationRequest = (SharedMarketplaceSubmitTutorApplicationRequest) o;
    return Objects.equals(this.moduleId, sharedMarketplaceSubmitTutorApplicationRequest.moduleId) &&
        Objects.equals(this.certificateRef, sharedMarketplaceSubmitTutorApplicationRequest.certificateRef) &&
        Objects.equals(this.profile, sharedMarketplaceSubmitTutorApplicationRequest.profile);
  }

  @Override
  public int hashCode() {
    return Objects.hash(moduleId, certificateRef, profile);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedMarketplaceSubmitTutorApplicationRequest {\n");
    sb.append("    moduleId: ").append(toIndentedString(moduleId)).append("\n");
    sb.append("    certificateRef: ").append(toIndentedString(certificateRef)).append("\n");
    sb.append("    profile: ").append(toIndentedString(profile)).append("\n");
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

