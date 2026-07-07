package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets Shared.Marketplace.Weekday
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public enum SharedMarketplaceWeekday {
  
  MONDAY("monday"),
  
  TUESDAY("tuesday"),
  
  WEDNESDAY("wednesday"),
  
  THURSDAY("thursday"),
  
  FRIDAY("friday"),
  
  SATURDAY("saturday"),
  
  SUNDAY("sunday");

  private final String value;

  SharedMarketplaceWeekday(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static SharedMarketplaceWeekday fromValue(String value) {
    for (SharedMarketplaceWeekday b : SharedMarketplaceWeekday.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

