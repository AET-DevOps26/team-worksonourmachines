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
import org.openapitools.model.SharedMarketplaceTutorAvailability;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SharedMarketplaceTutorProfile
 */

@JsonTypeName("Shared.Marketplace.TutorProfile")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class SharedMarketplaceTutorProfile {

  private String displayName;

  private String bio;

  @Valid
  private List<String> languages = new ArrayList<>();

  @Valid
  private List<SharedMarketplaceLocation> locations = new ArrayList<>();

  private Integer hourlyRate;

  @Valid
  private List<@Valid SharedMarketplaceTutorAvailability> availability = new ArrayList<>();

  private Boolean published = false;

  private String id;

  private String userId;

  public SharedMarketplaceTutorProfile() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SharedMarketplaceTutorProfile(String displayName, String bio, List<String> languages, List<SharedMarketplaceLocation> locations, Integer hourlyRate, List<@Valid SharedMarketplaceTutorAvailability> availability, String id, String userId) {
    this.displayName = displayName;
    this.bio = bio;
    this.languages = languages;
    this.locations = locations;
    this.hourlyRate = hourlyRate;
    this.availability = availability;
    this.id = id;
    this.userId = userId;
  }

  public SharedMarketplaceTutorProfile displayName(String displayName) {
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

  public SharedMarketplaceTutorProfile bio(String bio) {
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

  public SharedMarketplaceTutorProfile languages(List<String> languages) {
    this.languages = languages;
    return this;
  }

  public SharedMarketplaceTutorProfile addLanguagesItem(String languagesItem) {
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

  public SharedMarketplaceTutorProfile locations(List<SharedMarketplaceLocation> locations) {
    this.locations = locations;
    return this;
  }

  public SharedMarketplaceTutorProfile addLocationsItem(SharedMarketplaceLocation locationsItem) {
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

  public SharedMarketplaceTutorProfile hourlyRate(Integer hourlyRate) {
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

  public SharedMarketplaceTutorProfile availability(List<@Valid SharedMarketplaceTutorAvailability> availability) {
    this.availability = availability;
    return this;
  }

  public SharedMarketplaceTutorProfile addAvailabilityItem(SharedMarketplaceTutorAvailability availabilityItem) {
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

  public SharedMarketplaceTutorProfile published(Boolean published) {
    this.published = published;
    return this;
  }

  /**
   * Get published
   * @return published
   */
  
  @JsonProperty("published")
  public Boolean getPublished() {
    return published;
  }

  @JsonProperty("published")
  public void setPublished(Boolean published) {
    this.published = published;
  }

  public SharedMarketplaceTutorProfile id(String id) {
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

  public SharedMarketplaceTutorProfile userId(String userId) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SharedMarketplaceTutorProfile sharedMarketplaceTutorProfile = (SharedMarketplaceTutorProfile) o;
    return Objects.equals(this.displayName, sharedMarketplaceTutorProfile.displayName) &&
        Objects.equals(this.bio, sharedMarketplaceTutorProfile.bio) &&
        Objects.equals(this.languages, sharedMarketplaceTutorProfile.languages) &&
        Objects.equals(this.locations, sharedMarketplaceTutorProfile.locations) &&
        Objects.equals(this.hourlyRate, sharedMarketplaceTutorProfile.hourlyRate) &&
        Objects.equals(this.availability, sharedMarketplaceTutorProfile.availability) &&
        Objects.equals(this.published, sharedMarketplaceTutorProfile.published) &&
        Objects.equals(this.id, sharedMarketplaceTutorProfile.id) &&
        Objects.equals(this.userId, sharedMarketplaceTutorProfile.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(displayName, bio, languages, locations, hourlyRate, availability, published, id, userId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedMarketplaceTutorProfile {\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
    sb.append("    bio: ").append(toIndentedString(bio)).append("\n");
    sb.append("    languages: ").append(toIndentedString(languages)).append("\n");
    sb.append("    locations: ").append(toIndentedString(locations)).append("\n");
    sb.append("    hourlyRate: ").append(toIndentedString(hourlyRate)).append("\n");
    sb.append("    availability: ").append(toIndentedString(availability)).append("\n");
    sb.append("    published: ").append(toIndentedString(published)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
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

