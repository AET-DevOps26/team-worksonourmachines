package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import org.openapitools.model.SharedMarketplaceApplicationStatus;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SharedMarketplaceTutorApplication
 */

@JsonTypeName("Shared.Marketplace.TutorApplication")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class SharedMarketplaceTutorApplication {

  private String id;

  private String userId;

  private String moduleId;

  private String moduleCode;

  private String moduleTitle;

  private SharedMarketplaceApplicationStatus status;

  private String certificateRef;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime submittedAt;

  private @Nullable String rejectionReason;

  public SharedMarketplaceTutorApplication() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SharedMarketplaceTutorApplication(String id, String userId, String moduleId, String moduleCode, String moduleTitle, SharedMarketplaceApplicationStatus status, String certificateRef, OffsetDateTime submittedAt) {
    this.id = id;
    this.userId = userId;
    this.moduleId = moduleId;
    this.moduleCode = moduleCode;
    this.moduleTitle = moduleTitle;
    this.status = status;
    this.certificateRef = certificateRef;
    this.submittedAt = submittedAt;
  }

  public SharedMarketplaceTutorApplication id(String id) {
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

  public SharedMarketplaceTutorApplication userId(String userId) {
    this.userId = userId;
    return this;
  }

  /**
   * Get userId
   * @return userId
   */
  @NotNull 
  @Schema(name = "userId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("userId")
  public String getUserId() {
    return userId;
  }

  @JsonProperty("userId")
  public void setUserId(String userId) {
    this.userId = userId;
  }

  public SharedMarketplaceTutorApplication moduleId(String moduleId) {
    this.moduleId = moduleId;
    return this;
  }

  /**
   * Get moduleId
   * @return moduleId
   */
  @NotNull 
  @Schema(name = "moduleId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("moduleId")
  public String getModuleId() {
    return moduleId;
  }

  @JsonProperty("moduleId")
  public void setModuleId(String moduleId) {
    this.moduleId = moduleId;
  }

  public SharedMarketplaceTutorApplication moduleCode(String moduleCode) {
    this.moduleCode = moduleCode;
    return this;
  }

  /**
   * Get moduleCode
   * @return moduleCode
   */
  @NotNull 
  @Schema(name = "moduleCode", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("moduleCode")
  public String getModuleCode() {
    return moduleCode;
  }

  @JsonProperty("moduleCode")
  public void setModuleCode(String moduleCode) {
    this.moduleCode = moduleCode;
  }

  public SharedMarketplaceTutorApplication moduleTitle(String moduleTitle) {
    this.moduleTitle = moduleTitle;
    return this;
  }

  /**
   * Get moduleTitle
   * @return moduleTitle
   */
  @NotNull 
  @Schema(name = "moduleTitle", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("moduleTitle")
  public String getModuleTitle() {
    return moduleTitle;
  }

  @JsonProperty("moduleTitle")
  public void setModuleTitle(String moduleTitle) {
    this.moduleTitle = moduleTitle;
  }

  public SharedMarketplaceTutorApplication status(SharedMarketplaceApplicationStatus status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  @NotNull @Valid 
  @Schema(name = "status", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("status")
  public SharedMarketplaceApplicationStatus getStatus() {
    return status;
  }

  @JsonProperty("status")
  public void setStatus(SharedMarketplaceApplicationStatus status) {
    this.status = status;
  }

  public SharedMarketplaceTutorApplication certificateRef(String certificateRef) {
    this.certificateRef = certificateRef;
    return this;
  }

  /**
   * Get certificateRef
   * @return certificateRef
   */
  @NotNull 
  @Schema(name = "certificateRef", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("certificateRef")
  public String getCertificateRef() {
    return certificateRef;
  }

  @JsonProperty("certificateRef")
  public void setCertificateRef(String certificateRef) {
    this.certificateRef = certificateRef;
  }

  public SharedMarketplaceTutorApplication submittedAt(OffsetDateTime submittedAt) {
    this.submittedAt = submittedAt;
    return this;
  }

  /**
   * Get submittedAt
   * @return submittedAt
   */
  @NotNull @Valid 
  @Schema(name = "submittedAt", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("submittedAt")
  public OffsetDateTime getSubmittedAt() {
    return submittedAt;
  }

  @JsonProperty("submittedAt")
  public void setSubmittedAt(OffsetDateTime submittedAt) {
    this.submittedAt = submittedAt;
  }

  public SharedMarketplaceTutorApplication rejectionReason(@Nullable String rejectionReason) {
    this.rejectionReason = rejectionReason;
    return this;
  }

  /**
   * Get rejectionReason
   * @return rejectionReason
   */
  
  @Schema(name = "rejectionReason", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rejectionReason")
  public @Nullable String getRejectionReason() {
    return rejectionReason;
  }

  @JsonProperty("rejectionReason")
  public void setRejectionReason(@Nullable String rejectionReason) {
    this.rejectionReason = rejectionReason;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SharedMarketplaceTutorApplication sharedMarketplaceTutorApplication = (SharedMarketplaceTutorApplication) o;
    return Objects.equals(this.id, sharedMarketplaceTutorApplication.id) &&
        Objects.equals(this.userId, sharedMarketplaceTutorApplication.userId) &&
        Objects.equals(this.moduleId, sharedMarketplaceTutorApplication.moduleId) &&
        Objects.equals(this.moduleCode, sharedMarketplaceTutorApplication.moduleCode) &&
        Objects.equals(this.moduleTitle, sharedMarketplaceTutorApplication.moduleTitle) &&
        Objects.equals(this.status, sharedMarketplaceTutorApplication.status) &&
        Objects.equals(this.certificateRef, sharedMarketplaceTutorApplication.certificateRef) &&
        Objects.equals(this.submittedAt, sharedMarketplaceTutorApplication.submittedAt) &&
        Objects.equals(this.rejectionReason, sharedMarketplaceTutorApplication.rejectionReason);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, userId, moduleId, moduleCode, moduleTitle, status, certificateRef, submittedAt, rejectionReason);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedMarketplaceTutorApplication {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    moduleId: ").append(toIndentedString(moduleId)).append("\n");
    sb.append("    moduleCode: ").append(toIndentedString(moduleCode)).append("\n");
    sb.append("    moduleTitle: ").append(toIndentedString(moduleTitle)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    certificateRef: ").append(toIndentedString(certificateRef)).append("\n");
    sb.append("    submittedAt: ").append(toIndentedString(submittedAt)).append("\n");
    sb.append("    rejectionReason: ").append(toIndentedString(rejectionReason)).append("\n");
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

