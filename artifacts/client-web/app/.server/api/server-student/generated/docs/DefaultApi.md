# DefaultApi

All URIs are relative to *https://api.tutormatch.localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getMyProfile**](DefaultApi.md#getmyprofile) | **GET** /v1/students/me | Get my student profile |
| [**updateMyProfile**](DefaultApi.md#updatemyprofile) | **PUT** /v1/students/me | Update my student profile |



## getMyProfile

> SharedStudentStudentProfile getMyProfile()

Get my student profile

Returns the authenticated student\&#39;s profile. New students receive an empty default profile.

### Example

```ts
import {
  Configuration,
  DefaultApi,
} from '';
import type { GetMyProfileRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // To configure OAuth2 access token for authorization: KeycloakAuth accessCode
    accessToken: "YOUR ACCESS TOKEN",
  });
  const api = new DefaultApi(config);

  try {
    const data = await api.getMyProfile();
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**SharedStudentStudentProfile**](SharedStudentStudentProfile.md)

### Authorization

[KeycloakAuth accessCode](../README.md#KeycloakAuth-accessCode)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | The request has succeeded. |  -  |
| **401** | Access is unauthorized. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## updateMyProfile

> SharedStudentStudentProfile updateMyProfile(sharedStudentStudentProfileInput)

Update my student profile

Creates or updates the authenticated student\&#39;s display name, bio, languages, and study focus.

### Example

```ts
import {
  Configuration,
  DefaultApi,
} from '';
import type { UpdateMyProfileRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // To configure OAuth2 access token for authorization: KeycloakAuth accessCode
    accessToken: "YOUR ACCESS TOKEN",
  });
  const api = new DefaultApi(config);

  const body = {
    // SharedStudentStudentProfileInput
    sharedStudentStudentProfileInput: ...,
  } satisfies UpdateMyProfileRequest;

  try {
    const data = await api.updateMyProfile(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **sharedStudentStudentProfileInput** | [SharedStudentStudentProfileInput](SharedStudentStudentProfileInput.md) |  | |

### Return type

[**SharedStudentStudentProfile**](SharedStudentStudentProfile.md)

### Authorization

[KeycloakAuth accessCode](../README.md#KeycloakAuth-accessCode)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | The request has succeeded. |  -  |
| **400** | The server could not understand the request due to invalid syntax. |  -  |
| **401** | Access is unauthorized. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

