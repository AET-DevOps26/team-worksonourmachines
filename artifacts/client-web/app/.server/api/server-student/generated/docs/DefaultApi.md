# DefaultApi

All URIs are relative to *https://api.tutormatch.localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createGoal**](DefaultApi.md#creategoal) | **POST** /v1/students/me/goals | Create a learning goal |
| [**deleteGoal**](DefaultApi.md#deletegoal) | **DELETE** /v1/students/me/goals/{id} | Delete a learning goal |
| [**generatePlan**](DefaultApi.md#generateplan) | **POST** /v1/students/me/goals/{id}/plan | Generate and persist a study plan for a learning goal |
| [**getGoal**](DefaultApi.md#getgoal) | **GET** /v1/students/me/goals/{id} | Get a learning goal |
| [**getMyProfile**](DefaultApi.md#getmyprofile) | **GET** /v1/students/me | Get my student profile |
| [**getPlan**](DefaultApi.md#getplan) | **GET** /v1/students/me/goals/{id}/plan | Get the persisted study plan for a learning goal |
| [**listMyGoals**](DefaultApi.md#listmygoals) | **GET** /v1/students/me/goals | List my learning goals |
| [**updateGoal**](DefaultApi.md#updategoal) | **PUT** /v1/students/me/goals/{id} | Update a learning goal |
| [**updateMyProfile**](DefaultApi.md#updatemyprofile) | **PUT** /v1/students/me | Update my student profile |



## createGoal

> SharedStudentLearningGoal createGoal(sharedStudentLearningGoalInput)

Create a learning goal

### Example

```ts
import {
  Configuration,
  DefaultApi,
} from '';
import type { CreateGoalRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // To configure OAuth2 access token for authorization: KeycloakAuth accessCode
    accessToken: "YOUR ACCESS TOKEN",
  });
  const api = new DefaultApi(config);

  const body = {
    // SharedStudentLearningGoalInput
    sharedStudentLearningGoalInput: ...,
  } satisfies CreateGoalRequest;

  try {
    const data = await api.createGoal(body);
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
| **sharedStudentLearningGoalInput** | [SharedStudentLearningGoalInput](SharedStudentLearningGoalInput.md) |  | |

### Return type

[**SharedStudentLearningGoal**](SharedStudentLearningGoal.md)

### Authorization

[KeycloakAuth accessCode](../README.md#KeycloakAuth-accessCode)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | The request has succeeded and a new resource has been created as a result. |  -  |
| **400** | The server could not understand the request due to invalid syntax. |  -  |
| **401** | Access is unauthorized. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## deleteGoal

> deleteGoal(id)

Delete a learning goal

### Example

```ts
import {
  Configuration,
  DefaultApi,
} from '';
import type { DeleteGoalRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // To configure OAuth2 access token for authorization: KeycloakAuth accessCode
    accessToken: "YOUR ACCESS TOKEN",
  });
  const api = new DefaultApi(config);

  const body = {
    // string
    id: id_example,
  } satisfies DeleteGoalRequest;

  try {
    const data = await api.deleteGoal(body);
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
| **id** | `string` |  | [Defaults to `undefined`] |

### Return type

`void` (Empty response body)

### Authorization

[KeycloakAuth accessCode](../README.md#KeycloakAuth-accessCode)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | There is no content to send for this request, but the headers may be useful.  |  -  |
| **401** | Access is unauthorized. |  -  |
| **404** | The server cannot find the requested resource. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## generatePlan

> SharedStudentGeneratedPlan generatePlan(id)

Generate and persist a study plan for a learning goal

Calls the AI service to generate a study plan and persists it. Replaces any existing plan for the goal.

### Example

```ts
import {
  Configuration,
  DefaultApi,
} from '';
import type { GeneratePlanRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // To configure OAuth2 access token for authorization: KeycloakAuth accessCode
    accessToken: "YOUR ACCESS TOKEN",
  });
  const api = new DefaultApi(config);

  const body = {
    // string
    id: id_example,
  } satisfies GeneratePlanRequest;

  try {
    const data = await api.generatePlan(body);
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
| **id** | `string` |  | [Defaults to `undefined`] |

### Return type

[**SharedStudentGeneratedPlan**](SharedStudentGeneratedPlan.md)

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
| **404** | The server cannot find the requested resource. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getGoal

> SharedStudentLearningGoal getGoal(id)

Get a learning goal

### Example

```ts
import {
  Configuration,
  DefaultApi,
} from '';
import type { GetGoalRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // To configure OAuth2 access token for authorization: KeycloakAuth accessCode
    accessToken: "YOUR ACCESS TOKEN",
  });
  const api = new DefaultApi(config);

  const body = {
    // string
    id: id_example,
  } satisfies GetGoalRequest;

  try {
    const data = await api.getGoal(body);
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
| **id** | `string` |  | [Defaults to `undefined`] |

### Return type

[**SharedStudentLearningGoal**](SharedStudentLearningGoal.md)

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
| **404** | The server cannot find the requested resource. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


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


## getPlan

> SharedStudentGeneratedPlan getPlan(id)

Get the persisted study plan for a learning goal

### Example

```ts
import {
  Configuration,
  DefaultApi,
} from '';
import type { GetPlanRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // To configure OAuth2 access token for authorization: KeycloakAuth accessCode
    accessToken: "YOUR ACCESS TOKEN",
  });
  const api = new DefaultApi(config);

  const body = {
    // string
    id: id_example,
  } satisfies GetPlanRequest;

  try {
    const data = await api.getPlan(body);
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
| **id** | `string` |  | [Defaults to `undefined`] |

### Return type

[**SharedStudentGeneratedPlan**](SharedStudentGeneratedPlan.md)

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
| **404** | The server cannot find the requested resource. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## listMyGoals

> Array&lt;SharedStudentLearningGoal&gt; listMyGoals()

List my learning goals

### Example

```ts
import {
  Configuration,
  DefaultApi,
} from '';
import type { ListMyGoalsRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // To configure OAuth2 access token for authorization: KeycloakAuth accessCode
    accessToken: "YOUR ACCESS TOKEN",
  });
  const api = new DefaultApi(config);

  try {
    const data = await api.listMyGoals();
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

[**Array&lt;SharedStudentLearningGoal&gt;**](SharedStudentLearningGoal.md)

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


## updateGoal

> SharedStudentLearningGoal updateGoal(id, sharedStudentLearningGoalInput)

Update a learning goal

### Example

```ts
import {
  Configuration,
  DefaultApi,
} from '';
import type { UpdateGoalRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // To configure OAuth2 access token for authorization: KeycloakAuth accessCode
    accessToken: "YOUR ACCESS TOKEN",
  });
  const api = new DefaultApi(config);

  const body = {
    // string
    id: id_example,
    // SharedStudentLearningGoalInput
    sharedStudentLearningGoalInput: ...,
  } satisfies UpdateGoalRequest;

  try {
    const data = await api.updateGoal(body);
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
| **id** | `string` |  | [Defaults to `undefined`] |
| **sharedStudentLearningGoalInput** | [SharedStudentLearningGoalInput](SharedStudentLearningGoalInput.md) |  | |

### Return type

[**SharedStudentLearningGoal**](SharedStudentLearningGoal.md)

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
| **404** | The server cannot find the requested resource. |  -  |

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

