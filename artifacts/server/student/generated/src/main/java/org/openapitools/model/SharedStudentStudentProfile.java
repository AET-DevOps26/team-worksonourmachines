package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openapitools.model.SharedStudyFocusStudyFocus;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SharedStudentStudentProfile
 */

@JsonTypeName("Shared.Student.StudentProfile")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class SharedStudentStudentProfile {

  private String displayName;

  private String bio;

  @Valid
  private List<@Pattern(regexp = ".*\\S.*")@Size(min = 1)String> languages = new ArrayList<>();

  private @Nullable SharedStudyFocusStudyFocus studyFocus;

  public SharedStudentStudentProfile() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SharedStudentStudentProfile(String displayName, String bio, List<@Pattern(regexp = ".*\\S.*")@Size(min = 1)String> languages) {
    this.displayName = displayName;
    this.bio = bio;
    this.languages = languages;
  }

  public SharedStudentStudentProfile displayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

  /**
   * Get displayName
   * @return displayName
   */
  @NotNull @Pattern(regexp = ".*\\S.*") @Size(min = 1) 
  @JsonProperty("displayName")
  public String getDisplayName() {
    return displayName;
  }

  @JsonProperty("displayName")
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public SharedStudentStudentProfile bio(String bio) {
    this.bio = bio;
    return this;
  }

  /**
   * Get bio
   * @return bio
   */
  @NotNull @Pattern(regexp = ".*\\S.*") @Size(min = 1) 
  @JsonProperty("bio")
  public String getBio() {
    return bio;
  }

  @JsonProperty("bio")
  public void setBio(String bio) {
    this.bio = bio;
  }

  public SharedStudentStudentProfile languages(List<@Pattern(regexp = ".*\\S.*")@Size(min = 1)String> languages) {
    this.languages = languages;
    return this;
  }

  public SharedStudentStudentProfile addLanguagesItem(String languagesItem) {
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
  @NotNull @Size(min = 1) 
  @JsonProperty("languages")
  public List<@Pattern(regexp = ".*\\S.*")@Size(min = 1)String> getLanguages() {
    return languages;
  }

  @JsonProperty("languages")
  public void setLanguages(List<@Pattern(regexp = ".*\\S.*")@Size(min = 1)String> languages) {
    this.languages = languages;
  }

  public SharedStudentStudentProfile studyFocus(@Nullable SharedStudyFocusStudyFocus studyFocus) {
    this.studyFocus = studyFocus;
    return this;
  }

  /**
   * Self-assessed strength in each study skill (1 = needs work, 5 = confident).
   * @return studyFocus
   */
  @Valid 
  @JsonProperty("studyFocus")
  public @Nullable SharedStudyFocusStudyFocus getStudyFocus() {
    return studyFocus;
  }

  @JsonProperty("studyFocus")
  public void setStudyFocus(@Nullable SharedStudyFocusStudyFocus studyFocus) {
    this.studyFocus = studyFocus;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SharedStudentStudentProfile sharedStudentStudentProfile = (SharedStudentStudentProfile) o;
    return Objects.equals(this.displayName, sharedStudentStudentProfile.displayName) &&
        Objects.equals(this.bio, sharedStudentStudentProfile.bio) &&
        Objects.equals(this.languages, sharedStudentStudentProfile.languages) &&
        Objects.equals(this.studyFocus, sharedStudentStudentProfile.studyFocus);
  }

  @Override
  public int hashCode() {
    return Objects.hash(displayName, bio, languages, studyFocus);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SharedStudentStudentProfile {\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
    sb.append("    bio: ").append(toIndentedString(bio)).append("\n");
    sb.append("    languages: ").append(toIndentedString(languages)).append("\n");
    sb.append("    studyFocus: ").append(toIndentedString(studyFocus)).append("\n");
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

