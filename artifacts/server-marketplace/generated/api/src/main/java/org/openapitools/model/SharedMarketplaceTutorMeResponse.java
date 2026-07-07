package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openapitools.model.SharedMarketplaceTutorApplication;
import org.openapitools.model.SharedMarketplaceTutorProfile;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SharedMarketplaceTutorMeResponse
 */

@JsonTypeName("Shared.Marketplace.TutorMeResponse")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class SharedMarketplaceTutorMeResponse {

  private JsonNullable<SharedMarketplaceTutorProfile> profile = JsonNullable.<SharedMarketplaceTutorProfile>undefined();

  @Valid
  private List<@Valid SharedMarketplaceTutorApplication> applications = new ArrayList<>();

  public SharedMarketplaceTutorMeResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SharedMarketplaceTutorMeResponse(SharedMarketplaceTutorProfile profile, List<@Valid SharedMarketplaceTutorApplication> applications) {
    this.profile = JsonNullable.of(profile);
    this.applications = applications;
  }

  public SharedMarketplaceTutorMeResponse profile(SharedMarketplaceTutorProfile profile) {
    this.profile = JsonNullable.of(profile);
    return this;
  }

  /**
   * Get profile
   * @return profile
   */
  @NotNull @Valid 
  @Schema(name = "profile", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("profile")
  public JsonNullable<SharedMarketplaceTutorProfile> getProfile() {
    return profile;
  }

  @JsonProperty("profile")
  public void setProfile(JsonNullable<SharedMarketplaceTutorProfile> profile) {
    this.profile = profile;
  }

  public SharedMarketplaceTutorMeResponse applications(List<@Valid SharedMarketplaceTutorApplication> applications) {
    this.applications = applications;
    return this;
  }

  public SharedMarketplaceTutorMeResponse addApplicationsItem(SharedMarketplaceTutorApplication applicationsItem) {
    if (this.applications == null) {
      this.applications = new ArrayList<>();
    }
    this.applications.add(applicationsItem);
    return this;
  }

  /**
   * Get applications
   * @return applications
   */
  @NotNull @Valid 
  @Schema(name = "applications", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("applications")
  public List<@Valid SharedMarketplaceTutorApplication> getApplications() {
    return applications;
  }

  @JsonProperty("applications")
  public void setApplications(List<@Valid SharedMarketplaceTutorApplication> applications) {
    this.applications = applications;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SharedMarketplaceTutorMeResponse sharedMarketplaceTutorMeResponse = (SharedMarketplaceTutorMeResponse) o;
    return Objects.equals(this.profile, sharedMarketplaceTutorMeResponse.profile) &&
        Objects.equals(this.applications, sharedMarketplaceTutorMeResponse.applications);
  }

  @Override
  public int hashCode() {
    return Objects.hash(profile, applications);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedMarketplaceTutorMeResponse {\n");
    sb.append("    profile: ").append(toIndentedString(profile)).append("\n");
    sb.append("    applications: ").append(toIndentedString(applications)).append("\n");
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

