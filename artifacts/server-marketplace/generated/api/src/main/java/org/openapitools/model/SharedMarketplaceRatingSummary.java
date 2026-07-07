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
 * SharedMarketplaceRatingSummary
 */

@JsonTypeName("Shared.Marketplace.RatingSummary")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class SharedMarketplaceRatingSummary {

  private Float average;

  private Integer count;

  public SharedMarketplaceRatingSummary() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SharedMarketplaceRatingSummary(Float average, Integer count) {
    this.average = average;
    this.count = count;
  }

  public SharedMarketplaceRatingSummary average(Float average) {
    this.average = average;
    return this;
  }

  /**
   * Get average
   * @return average
   */
  @NotNull 
  @Schema(name = "average", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("average")
  public Float getAverage() {
    return average;
  }

  @JsonProperty("average")
  public void setAverage(Float average) {
    this.average = average;
  }

  public SharedMarketplaceRatingSummary count(Integer count) {
    this.count = count;
    return this;
  }

  /**
   * Get count
   * @return count
   */
  @NotNull 
  @Schema(name = "count", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("count")
  public Integer getCount() {
    return count;
  }

  @JsonProperty("count")
  public void setCount(Integer count) {
    this.count = count;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SharedMarketplaceRatingSummary sharedMarketplaceRatingSummary = (SharedMarketplaceRatingSummary) o;
    return Objects.equals(this.average, sharedMarketplaceRatingSummary.average) &&
        Objects.equals(this.count, sharedMarketplaceRatingSummary.count);
  }

  @Override
  public int hashCode() {
    return Objects.hash(average, count);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedMarketplaceRatingSummary {\n");
    sb.append("    average: ").append(toIndentedString(average)).append("\n");
    sb.append("    count: ").append(toIndentedString(count)).append("\n");
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

