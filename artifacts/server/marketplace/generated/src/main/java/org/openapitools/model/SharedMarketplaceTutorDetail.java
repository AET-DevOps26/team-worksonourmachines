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
import org.openapitools.model.SharedMarketplaceRatingSummary;
import org.openapitools.model.SharedMarketplaceTutorAvailability;
import org.openapitools.model.SharedMarketplaceTutorCoverage;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SharedMarketplaceTutorDetail
 */

@JsonTypeName("Shared.Marketplace.TutorDetail")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class SharedMarketplaceTutorDetail {

  private String id;

  private String userId;

  private String displayName;

  private Integer hourlyRate;

  @Valid
  private List<String> languages = new ArrayList<>();

  @Valid
  private List<SharedMarketplaceLocation> locations = new ArrayList<>();

  private SharedMarketplaceRatingSummary ratingSummary;

  @Valid
  private List<@Valid SharedMarketplaceTutorCoverage> coverages = new ArrayList<>();

  private String bio;

  @Valid
  private List<@Valid SharedMarketplaceTutorAvailability> availability = new ArrayList<>();

  private Boolean published;

  public SharedMarketplaceTutorDetail() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SharedMarketplaceTutorDetail(String id, String userId, String displayName, Integer hourlyRate, List<String> languages, List<SharedMarketplaceLocation> locations, SharedMarketplaceRatingSummary ratingSummary, List<@Valid SharedMarketplaceTutorCoverage> coverages, String bio, List<@Valid SharedMarketplaceTutorAvailability> availability, Boolean published) {
    this.id = id;
    this.userId = userId;
    this.displayName = displayName;
    this.hourlyRate = hourlyRate;
    this.languages = languages;
    this.locations = locations;
    this.ratingSummary = ratingSummary;
    this.coverages = coverages;
    this.bio = bio;
    this.availability = availability;
    this.published = published;
  }

  public SharedMarketplaceTutorDetail id(String id) {
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

  public SharedMarketplaceTutorDetail userId(String userId) {
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

  public SharedMarketplaceTutorDetail displayName(String displayName) {
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

  public SharedMarketplaceTutorDetail hourlyRate(Integer hourlyRate) {
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

  public SharedMarketplaceTutorDetail languages(List<String> languages) {
    this.languages = languages;
    return this;
  }

  public SharedMarketplaceTutorDetail addLanguagesItem(String languagesItem) {
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

  public SharedMarketplaceTutorDetail locations(List<SharedMarketplaceLocation> locations) {
    this.locations = locations;
    return this;
  }

  public SharedMarketplaceTutorDetail addLocationsItem(SharedMarketplaceLocation locationsItem) {
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

  public SharedMarketplaceTutorDetail ratingSummary(SharedMarketplaceRatingSummary ratingSummary) {
    this.ratingSummary = ratingSummary;
    return this;
  }

  /**
   * Get ratingSummary
   * @return ratingSummary
   */
  @NotNull @Valid 
  @JsonProperty("ratingSummary")
  public SharedMarketplaceRatingSummary getRatingSummary() {
    return ratingSummary;
  }

  @JsonProperty("ratingSummary")
  public void setRatingSummary(SharedMarketplaceRatingSummary ratingSummary) {
    this.ratingSummary = ratingSummary;
  }

  public SharedMarketplaceTutorDetail coverages(List<@Valid SharedMarketplaceTutorCoverage> coverages) {
    this.coverages = coverages;
    return this;
  }

  public SharedMarketplaceTutorDetail addCoveragesItem(SharedMarketplaceTutorCoverage coveragesItem) {
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

  public SharedMarketplaceTutorDetail bio(String bio) {
    this.bio = bio;
    return this;
  }

  /**
   * Get bio
   * @return bio
   */
  @NotNull 
  @JsonProperty("bio")
  public String getBio() {
    return bio;
  }

  @JsonProperty("bio")
  public void setBio(String bio) {
    this.bio = bio;
  }

  public SharedMarketplaceTutorDetail availability(List<@Valid SharedMarketplaceTutorAvailability> availability) {
    this.availability = availability;
    return this;
  }

  public SharedMarketplaceTutorDetail addAvailabilityItem(SharedMarketplaceTutorAvailability availabilityItem) {
    if (this.availability == null) {
      this.availability = new ArrayList<>();
    }
    this.availability.add(availabilityItem);
    return this;
  }

  /**
   * Get availability
   * @return availability
   */
  @NotNull @Valid 
  @JsonProperty("availability")
  public List<@Valid SharedMarketplaceTutorAvailability> getAvailability() {
    return availability;
  }

  @JsonProperty("availability")
  public void setAvailability(List<@Valid SharedMarketplaceTutorAvailability> availability) {
    this.availability = availability;
  }

  public SharedMarketplaceTutorDetail published(Boolean published) {
    this.published = published;
    return this;
  }

  /**
   * Get published
   * @return published
   */
  @NotNull 
  @JsonProperty("published")
  public Boolean getPublished() {
    return published;
  }

  @JsonProperty("published")
  public void setPublished(Boolean published) {
    this.published = published;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SharedMarketplaceTutorDetail sharedMarketplaceTutorDetail = (SharedMarketplaceTutorDetail) o;
    return Objects.equals(this.id, sharedMarketplaceTutorDetail.id) &&
        Objects.equals(this.userId, sharedMarketplaceTutorDetail.userId) &&
        Objects.equals(this.displayName, sharedMarketplaceTutorDetail.displayName) &&
        Objects.equals(this.hourlyRate, sharedMarketplaceTutorDetail.hourlyRate) &&
        Objects.equals(this.languages, sharedMarketplaceTutorDetail.languages) &&
        Objects.equals(this.locations, sharedMarketplaceTutorDetail.locations) &&
        Objects.equals(this.ratingSummary, sharedMarketplaceTutorDetail.ratingSummary) &&
        Objects.equals(this.coverages, sharedMarketplaceTutorDetail.coverages) &&
        Objects.equals(this.bio, sharedMarketplaceTutorDetail.bio) &&
        Objects.equals(this.availability, sharedMarketplaceTutorDetail.availability) &&
        Objects.equals(this.published, sharedMarketplaceTutorDetail.published);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, userId, displayName, hourlyRate, languages, locations, ratingSummary, coverages, bio, availability, published);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedMarketplaceTutorDetail {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
    sb.append("    hourlyRate: ").append(toIndentedString(hourlyRate)).append("\n");
    sb.append("    languages: ").append(toIndentedString(languages)).append("\n");
    sb.append("    locations: ").append(toIndentedString(locations)).append("\n");
    sb.append("    ratingSummary: ").append(toIndentedString(ratingSummary)).append("\n");
    sb.append("    coverages: ").append(toIndentedString(coverages)).append("\n");
    sb.append("    bio: ").append(toIndentedString(bio)).append("\n");
    sb.append("    availability: ").append(toIndentedString(availability)).append("\n");
    sb.append("    published: ").append(toIndentedString(published)).append("\n");
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

