package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openapitools.model.SharedMarketplaceLocation;
import org.openapitools.model.SharedMarketplaceTutorCoverage;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SharedMarketplaceTutorSummary
 */

@JsonTypeName("Shared.Marketplace.TutorSummary")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class SharedMarketplaceTutorSummary {

  private String id;

  private String userId;

  private String displayName;

  private Integer hourlyRate;

  @Valid
  private List<String> languages = new ArrayList<>();

  @Valid
  private List<SharedMarketplaceLocation> locations = new ArrayList<>();

  @Valid
  private List<@Valid SharedMarketplaceTutorCoverage> coverages = new ArrayList<>();

  public SharedMarketplaceTutorSummary() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SharedMarketplaceTutorSummary(String id, String userId, String displayName, Integer hourlyRate, List<String> languages, List<SharedMarketplaceLocation> locations, List<@Valid SharedMarketplaceTutorCoverage> coverages) {
    this.id = id;
    this.userId = userId;
    this.displayName = displayName;
    this.hourlyRate = hourlyRate;
    this.languages = languages;
    this.locations = locations;
    this.coverages = coverages;
  }

  public SharedMarketplaceTutorSummary id(String id) {
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

  public SharedMarketplaceTutorSummary userId(String userId) {
    this.userId = userId;
    return this;
  }

  /**
   * Get userId
   * @return userId
   */
  @NotNull 
  @JsonProperty("userId")
  public String getUserId() {
    return userId;
  }

  @JsonProperty("userId")
  public void setUserId(String userId) {
    this.userId = userId;
  }

  public SharedMarketplaceTutorSummary displayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

  /**
   * Get displayName
   * @return displayName
   */
  @NotNull 
  @JsonProperty("displayName")
  public String getDisplayName() {
    return displayName;
  }

  @JsonProperty("displayName")
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public SharedMarketplaceTutorSummary hourlyRate(Integer hourlyRate) {
    this.hourlyRate = hourlyRate;
    return this;
  }

  /**
   * Get hourlyRate
   * @return hourlyRate
   */
  @NotNull 
  @JsonProperty("hourlyRate")
  public Integer getHourlyRate() {
    return hourlyRate;
  }

  @JsonProperty("hourlyRate")
  public void setHourlyRate(Integer hourlyRate) {
    this.hourlyRate = hourlyRate;
  }

  public SharedMarketplaceTutorSummary languages(List<String> languages) {
    this.languages = languages;
    return this;
  }

  public SharedMarketplaceTutorSummary addLanguagesItem(String languagesItem) {
    if (this.languages == null) {
      this.languages = new ArrayList<>();
    }
    this.languages.add(languagesItem);
    return this;
  }

  /**
   * Get languages
   * @return languages
   */
  @NotNull 
  @JsonProperty("languages")
  public List<String> getLanguages() {
    return languages;
  }

  @JsonProperty("languages")
  public void setLanguages(List<String> languages) {
    this.languages = languages;
  }

  public SharedMarketplaceTutorSummary locations(List<SharedMarketplaceLocation> locations) {
    this.locations = locations;
    return this;
  }

  public SharedMarketplaceTutorSummary addLocationsItem(SharedMarketplaceLocation locationsItem) {
    if (this.locations == null) {
      this.locations = new ArrayList<>();
    }
    this.locations.add(locationsItem);
    return this;
  }

  /**
   * Get locations
   * @return locations
   */
  @NotNull @Valid 
  @JsonProperty("locations")
  public List<SharedMarketplaceLocation> getLocations() {
    return locations;
  }

  @JsonProperty("locations")
  public void setLocations(List<SharedMarketplaceLocation> locations) {
    this.locations = locations;
  }

  public SharedMarketplaceTutorSummary coverages(List<@Valid SharedMarketplaceTutorCoverage> coverages) {
    this.coverages = coverages;
    return this;
  }

  public SharedMarketplaceTutorSummary addCoveragesItem(SharedMarketplaceTutorCoverage coveragesItem) {
    if (this.coverages == null) {
      this.coverages = new ArrayList<>();
    }
    this.coverages.add(coveragesItem);
    return this;
  }

  /**
   * Get coverages
   * @return coverages
   */
  @NotNull @Valid 
  @JsonProperty("coverages")
  public List<@Valid SharedMarketplaceTutorCoverage> getCoverages() {
    return coverages;
  }

  @JsonProperty("coverages")
  public void setCoverages(List<@Valid SharedMarketplaceTutorCoverage> coverages) {
    this.coverages = coverages;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SharedMarketplaceTutorSummary sharedMarketplaceTutorSummary = (SharedMarketplaceTutorSummary) o;
    return Objects.equals(this.id, sharedMarketplaceTutorSummary.id) &&
        Objects.equals(this.userId, sharedMarketplaceTutorSummary.userId) &&
        Objects.equals(this.displayName, sharedMarketplaceTutorSummary.displayName) &&
        Objects.equals(this.hourlyRate, sharedMarketplaceTutorSummary.hourlyRate) &&
        Objects.equals(this.languages, sharedMarketplaceTutorSummary.languages) &&
        Objects.equals(this.locations, sharedMarketplaceTutorSummary.locations) &&
        Objects.equals(this.coverages, sharedMarketplaceTutorSummary.coverages);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, userId, displayName, hourlyRate, languages, locations, coverages);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedMarketplaceTutorSummary {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
    sb.append("    hourlyRate: ").append(toIndentedString(hourlyRate)).append("\n");
    sb.append("    languages: ").append(toIndentedString(languages)).append("\n");
    sb.append("    locations: ").append(toIndentedString(locations)).append("\n");
    sb.append("    coverages: ").append(toIndentedString(coverages)).append("\n");
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

