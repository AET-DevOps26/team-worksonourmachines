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
 * SharedMarketplaceTutorProfileInput
 */

@JsonTypeName("Shared.Marketplace.TutorProfileInput")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class SharedMarketplaceTutorProfileInput {

  private String displayName;

  private String bio;

  @Valid
  private List<String> languages = new ArrayList<>();

  @Valid
  private List<SharedMarketplaceLocation> locations = new ArrayList<>();

  private Float hourlyRate;

  @Valid
  private List<@Valid SharedMarketplaceTutorAvailability> availability = new ArrayList<>();

  private Boolean published = false;

  public SharedMarketplaceTutorProfileInput() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SharedMarketplaceTutorProfileInput(String displayName, String bio, List<String> languages, List<SharedMarketplaceLocation> locations, Float hourlyRate, List<@Valid SharedMarketplaceTutorAvailability> availability) {
    this.displayName = displayName;
    this.bio = bio;
    this.languages = languages;
    this.locations = locations;
    this.hourlyRate = hourlyRate;
    this.availability = availability;
  }

  public SharedMarketplaceTutorProfileInput displayName(String displayName) {
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

  public SharedMarketplaceTutorProfileInput bio(String bio) {
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

  public SharedMarketplaceTutorProfileInput languages(List<String> languages) {
    this.languages = languages;
    return this;
  }

  public SharedMarketplaceTutorProfileInput addLanguagesItem(String languagesItem) {
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

  public SharedMarketplaceTutorProfileInput locations(List<SharedMarketplaceLocation> locations) {
    this.locations = locations;
    return this;
  }

  public SharedMarketplaceTutorProfileInput addLocationsItem(SharedMarketplaceLocation locationsItem) {
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

  public SharedMarketplaceTutorProfileInput hourlyRate(Float hourlyRate) {
    this.hourlyRate = hourlyRate;
    return this;
  }

  /**
   * Get hourlyRate
   * @return hourlyRate
   */
  @NotNull 
  @JsonProperty("hourlyRate")
  public Float getHourlyRate() {
    return hourlyRate;
  }

  @JsonProperty("hourlyRate")
  public void setHourlyRate(Float hourlyRate) {
    this.hourlyRate = hourlyRate;
  }

  public SharedMarketplaceTutorProfileInput availability(List<@Valid SharedMarketplaceTutorAvailability> availability) {
    this.availability = availability;
    return this;
  }

  public SharedMarketplaceTutorProfileInput addAvailabilityItem(SharedMarketplaceTutorAvailability availabilityItem) {
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

  public SharedMarketplaceTutorProfileInput published(Boolean published) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SharedMarketplaceTutorProfileInput sharedMarketplaceTutorProfileInput = (SharedMarketplaceTutorProfileInput) o;
    return Objects.equals(this.displayName, sharedMarketplaceTutorProfileInput.displayName) &&
        Objects.equals(this.bio, sharedMarketplaceTutorProfileInput.bio) &&
        Objects.equals(this.languages, sharedMarketplaceTutorProfileInput.languages) &&
        Objects.equals(this.locations, sharedMarketplaceTutorProfileInput.locations) &&
        Objects.equals(this.hourlyRate, sharedMarketplaceTutorProfileInput.hourlyRate) &&
        Objects.equals(this.availability, sharedMarketplaceTutorProfileInput.availability) &&
        Objects.equals(this.published, sharedMarketplaceTutorProfileInput.published);
  }

  @Override
  public int hashCode() {
    return Objects.hash(displayName, bio, languages, locations, hourlyRate, availability, published);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedMarketplaceTutorProfileInput {\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
    sb.append("    bio: ").append(toIndentedString(bio)).append("\n");
    sb.append("    languages: ").append(toIndentedString(languages)).append("\n");
    sb.append("    locations: ").append(toIndentedString(locations)).append("\n");
    sb.append("    hourlyRate: ").append(toIndentedString(hourlyRate)).append("\n");
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

