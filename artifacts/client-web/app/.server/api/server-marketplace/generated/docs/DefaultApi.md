# DefaultApi

All URIs are relative to *https://api.tutormatch.localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**approveTutorApplication**](DefaultApi.md#approvetutorapplication) | **POST** /v1/admin/tutor-applications/{id}/approve | Approve tutor application (admin) |
| [**createAdminModule**](DefaultApi.md#createadminmodule) | **POST** /v1/admin/modules | Create module (admin) |
| [**getModule**](DefaultApi.md#getmodule) | **GET** /v1/modules/{code} | Get module by code |
| [**getMyTutorProfile**](DefaultApi.md#getmytutorprofile) | **GET** /v1/tutors/me | Get my tutor profile |
| [**getTutor**](DefaultApi.md#gettutor) | **GET** /v1/tutors/{id} | Get tutor profile |
| [**listAdminModules**](DefaultApi.md#listadminmodules) | **GET** /v1/admin/modules | List modules (admin) |
| [**listAdminTutorApplications**](DefaultApi.md#listadmintutorapplications) | **GET** /v1/admin/tutor-applications | List tutor applications (admin) |
| [**listModules**](DefaultApi.md#listmodules) | **GET** /v1/modules | List modules |
| [**listMyTutorApplications**](DefaultApi.md#listmytutorapplications) | **GET** /v1/tutor-applications/me | List my tutor applications |
| [**listTutors**](DefaultApi.md#listtutors) | **GET** /v1/tutors | Discover tutors |
| [**rejectTutorApplication**](DefaultApi.md#rejecttutorapplication) | **POST** /v1/admin/tutor-applications/{id}/reject | Reject tutor application (admin) |
| [**submitTutorApplication**](DefaultApi.md#submittutorapplication) | **POST** /v1/tutor-applications | Submit tutor application |
| [**updateAdminModule**](DefaultApi.md#updateadminmodule) | **PUT** /v1/admin/modules/{code} | Update module (admin) |
| [**updateMyTutorProfile**](DefaultApi.md#updatemytutorprofile) | **PUT** /v1/tutors/me | Update my tutor profile |



## approveTutorApplication

> SharedMarketplaceApproveApplicationResponse approveTutorApplication(id)

Approve tutor application (admin)

Approves a pending tutor application and grants module coverage.

### Example

```ts
import {
  Configuration,
  DefaultApi,
} from '';
import type { ApproveTutorApplicationRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // To configure OAuth2 access token for authorization: KeycloakAdminAuth accessCode
    accessToken: "YOUR ACCESS TOKEN",
  });
  const api = new DefaultApi(config);

  const body = {
    // string
    id: id_example,
  } satisfies ApproveTutorApplicationRequest;

  try {
    const data = await api.approveTutorApplication(body);
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

[**SharedMarketplaceApproveApplicationResponse**](SharedMarketplaceApproveApplicationResponse.md)

### Authorization

[KeycloakAdminAuth accessCode](../README.md#KeycloakAdminAuth-accessCode)

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


## createAdminModule

> SharedMarketplaceModuleDetail createAdminModule(sharedMarketplaceAdminModuleInput)

Create module (admin)

Creates a new course module.

### Example

```ts
import {
  Configuration,
  DefaultApi,
} from '';
import type { CreateAdminModuleRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // To configure OAuth2 access token for authorization: KeycloakAdminAuth accessCode
    accessToken: "YOUR ACCESS TOKEN",
  });
  const api = new DefaultApi(config);

  const body = {
    // SharedMarketplaceAdminModuleInput
    sharedMarketplaceAdminModuleInput: ...,
  } satisfies CreateAdminModuleRequest;

  try {
    const data = await api.createAdminModule(body);
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
| **sharedMarketplaceAdminModuleInput** | [SharedMarketplaceAdminModuleInput](SharedMarketplaceAdminModuleInput.md) |  | |

### Return type

[**SharedMarketplaceModuleDetail**](SharedMarketplaceModuleDetail.md)

### Authorization

[KeycloakAdminAuth accessCode](../README.md#KeycloakAdminAuth-accessCode)

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


## getModule

> SharedMarketplaceModuleDetail getModule(code)

Get module by code

Returns module details including topics for the given module code.

### Example

```ts
import {
  Configuration,
  DefaultApi,
} from '';
import type { GetModuleRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // To configure OAuth2 access token for authorization: KeycloakAuth accessCode
    accessToken: "YOUR ACCESS TOKEN",
  });
  const api = new DefaultApi(config);

  const body = {
    // string
    code: code_example,
  } satisfies GetModuleRequest;

  try {
    const data = await api.getModule(body);
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
| **code** | `string` |  | [Defaults to `undefined`] |

### Return type

[**SharedMarketplaceModuleDetail**](SharedMarketplaceModuleDetail.md)

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


## getMyTutorProfile

> SharedMarketplaceTutorMeResponse getMyTutorProfile()

Get my tutor profile

Returns the authenticated user\&#39;s tutor profile and module applications, if any.

### Example

```ts
import {
  Configuration,
  DefaultApi,
} from '';
import type { GetMyTutorProfileRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // To configure OAuth2 access token for authorization: KeycloakAuth accessCode
    accessToken: "YOUR ACCESS TOKEN",
  });
  const api = new DefaultApi(config);

  try {
    const data = await api.getMyTutorProfile();
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

[**SharedMarketplaceTutorMeResponse**](SharedMarketplaceTutorMeResponse.md)

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


## getTutor

> SharedMarketplaceTutorDetail getTutor(id)

Get tutor profile

Returns the public profile of a published tutor, including coverage and availability.

### Example

```ts
import {
  Configuration,
  DefaultApi,
} from '';
import type { GetTutorRequest } from '';

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
  } satisfies GetTutorRequest;

  try {
    const data = await api.getTutor(body);
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

[**SharedMarketplaceTutorDetail**](SharedMarketplaceTutorDetail.md)

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


## listAdminModules

> Array&lt;SharedMarketplaceModuleDetail&gt; listAdminModules()

List modules (admin)

Returns all modules with topics for administration.

### Example

```ts
import {
  Configuration,
  DefaultApi,
} from '';
import type { ListAdminModulesRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // To configure OAuth2 access token for authorization: KeycloakAdminAuth accessCode
    accessToken: "YOUR ACCESS TOKEN",
  });
  const api = new DefaultApi(config);

  try {
    const data = await api.listAdminModules();
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

[**Array&lt;SharedMarketplaceModuleDetail&gt;**](SharedMarketplaceModuleDetail.md)

### Authorization

[KeycloakAdminAuth accessCode](../README.md#KeycloakAdminAuth-accessCode)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | The request has succeeded. |  -  |
| **401** | Access is unauthorized. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## listAdminTutorApplications

> Array&lt;SharedMarketplaceTutorApplication&gt; listAdminTutorApplications(status)

List tutor applications (admin)

Returns tutor applications for admin review, optionally filtered by status.

### Example

```ts
import {
  Configuration,
  DefaultApi,
} from '';
import type { ListAdminTutorApplicationsRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // To configure OAuth2 access token for authorization: KeycloakAdminAuth accessCode
    accessToken: "YOUR ACCESS TOKEN",
  });
  const api = new DefaultApi(config);

  const body = {
    // SharedMarketplaceApplicationStatus (optional)
    status: ...,
  } satisfies ListAdminTutorApplicationsRequest;

  try {
    const data = await api.listAdminTutorApplications(body);
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
| **status** | `SharedMarketplaceApplicationStatus` |  | [Optional] [Defaults to `undefined`] [Enum: pending, approved, rejected] |

### Return type

[**Array&lt;SharedMarketplaceTutorApplication&gt;**](SharedMarketplaceTutorApplication.md)

### Authorization

[KeycloakAdminAuth accessCode](../README.md#KeycloakAdminAuth-accessCode)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | The request has succeeded. |  -  |
| **401** | Access is unauthorized. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## listModules

> ModulePage listModules(page, pageSize, q)

List modules

Returns a paginated list of course modules, optionally filtered by search query.

### Example

```ts
import {
  Configuration,
  DefaultApi,
} from '';
import type { ListModulesRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // To configure OAuth2 access token for authorization: KeycloakAuth accessCode
    accessToken: "YOUR ACCESS TOKEN",
  });
  const api = new DefaultApi(config);

  const body = {
    // number (optional)
    page: 56,
    // number (optional)
    pageSize: 56,
    // string (optional)
    q: q_example,
  } satisfies ListModulesRequest;

  try {
    const data = await api.listModules(body);
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
| **page** | `number` |  | [Optional] [Defaults to `1`] |
| **pageSize** | `number` |  | [Optional] [Defaults to `20`] |
| **q** | `string` |  | [Optional] [Defaults to `undefined`] |

### Return type

[**ModulePage**](ModulePage.md)

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


## listMyTutorApplications

> Array&lt;SharedMarketplaceTutorApplication&gt; listMyTutorApplications()

List my tutor applications

Returns all module applications submitted by the authenticated user.

### Example

```ts
import {
  Configuration,
  DefaultApi,
} from '';
import type { ListMyTutorApplicationsRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // To configure OAuth2 access token for authorization: KeycloakAuth accessCode
    accessToken: "YOUR ACCESS TOKEN",
  });
  const api = new DefaultApi(config);

  try {
    const data = await api.listMyTutorApplications();
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

[**Array&lt;SharedMarketplaceTutorApplication&gt;**](SharedMarketplaceTutorApplication.md)

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


## listTutors

> TutorPage listTutors(page, pageSize, q, moduleId, topicId, languages, locations, minRate, maxRate, weekdays, sort)

Discover tutors

Returns a paginated, filterable list of published tutor profiles.

### Example

```ts
import {
  Configuration,
  DefaultApi,
} from '';
import type { ListTutorsRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // To configure OAuth2 access token for authorization: KeycloakAuth accessCode
    accessToken: "YOUR ACCESS TOKEN",
  });
  const api = new DefaultApi(config);

  const body = {
    // number (optional)
    page: 56,
    // number (optional)
    pageSize: 56,
    // string (optional)
    q: q_example,
    // string (optional)
    moduleId: moduleId_example,
    // string (optional)
    topicId: topicId_example,
    // Array<string> (optional)
    languages: ...,
    // Array<SharedMarketplaceLocation> (optional)
    locations: ...,
    // number (optional)
    minRate: 3.4,
    // number (optional)
    maxRate: 3.4,
    // Array<SharedMarketplaceWeekday> (optional)
    weekdays: ...,
    // SharedMarketplaceTutorSort (optional)
    sort: ...,
  } satisfies ListTutorsRequest;

  try {
    const data = await api.listTutors(body);
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
| **page** | `number` |  | [Optional] [Defaults to `1`] |
| **pageSize** | `number` |  | [Optional] [Defaults to `20`] |
| **q** | `string` |  | [Optional] [Defaults to `undefined`] |
| **moduleId** | `string` |  | [Optional] [Defaults to `undefined`] |
| **topicId** | `string` |  | [Optional] [Defaults to `undefined`] |
| **languages** | `Array<string>` |  | [Optional] |
| **locations** | `Array<SharedMarketplaceLocation>` |  | [Optional] |
| **minRate** | `number` |  | [Optional] [Defaults to `undefined`] |
| **maxRate** | `number` |  | [Optional] [Defaults to `undefined`] |
| **weekdays** | `Array<SharedMarketplaceWeekday>` |  | [Optional] |
| **sort** | `SharedMarketplaceTutorSort` |  | [Optional] [Defaults to `undefined`] [Enum: rate_asc, rate_desc, name] |

### Return type

[**TutorPage**](TutorPage.md)

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


## rejectTutorApplication

> SharedMarketplaceTutorApplication rejectTutorApplication(id, sharedMarketplaceRejectApplicationRequest)

Reject tutor application (admin)

Rejects a pending tutor application with an optional reason.

### Example

```ts
import {
  Configuration,
  DefaultApi,
} from '';
import type { RejectTutorApplicationRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // To configure OAuth2 access token for authorization: KeycloakAdminAuth accessCode
    accessToken: "YOUR ACCESS TOKEN",
  });
  const api = new DefaultApi(config);

  const body = {
    // string
    id: id_example,
    // SharedMarketplaceRejectApplicationRequest
    sharedMarketplaceRejectApplicationRequest: ...,
  } satisfies RejectTutorApplicationRequest;

  try {
    const data = await api.rejectTutorApplication(body);
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
| **sharedMarketplaceRejectApplicationRequest** | [SharedMarketplaceRejectApplicationRequest](SharedMarketplaceRejectApplicationRequest.md) |  | |

### Return type

[**SharedMarketplaceTutorApplication**](SharedMarketplaceTutorApplication.md)

### Authorization

[KeycloakAdminAuth accessCode](../README.md#KeycloakAdminAuth-accessCode)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | The request has succeeded. |  -  |
| **401** | Access is unauthorized. |  -  |
| **404** | The server cannot find the requested resource. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## submitTutorApplication

> SharedMarketplaceTutorApplication submitTutorApplication(sharedMarketplaceSubmitTutorApplicationRequest)

Submit tutor application

Submits a module tutoring application, optionally including an initial tutor profile on first apply.

### Example

```ts
import {
  Configuration,
  DefaultApi,
} from '';
import type { SubmitTutorApplicationRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // To configure OAuth2 access token for authorization: KeycloakAuth accessCode
    accessToken: "YOUR ACCESS TOKEN",
  });
  const api = new DefaultApi(config);

  const body = {
    // SharedMarketplaceSubmitTutorApplicationRequest
    sharedMarketplaceSubmitTutorApplicationRequest: ...,
  } satisfies SubmitTutorApplicationRequest;

  try {
    const data = await api.submitTutorApplication(body);
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
| **sharedMarketplaceSubmitTutorApplicationRequest** | [SharedMarketplaceSubmitTutorApplicationRequest](SharedMarketplaceSubmitTutorApplicationRequest.md) |  | |

### Return type

[**SharedMarketplaceTutorApplication**](SharedMarketplaceTutorApplication.md)

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


## updateAdminModule

> SharedMarketplaceModuleDetail updateAdminModule(code, sharedMarketplaceAdminModuleUpdateInput)

Update module (admin)

Updates an existing module identified by code.

### Example

```ts
import {
  Configuration,
  DefaultApi,
} from '';
import type { UpdateAdminModuleRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // To configure OAuth2 access token for authorization: KeycloakAdminAuth accessCode
    accessToken: "YOUR ACCESS TOKEN",
  });
  const api = new DefaultApi(config);

  const body = {
    // string
    code: code_example,
    // SharedMarketplaceAdminModuleUpdateInput
    sharedMarketplaceAdminModuleUpdateInput: ...,
  } satisfies UpdateAdminModuleRequest;

  try {
    const data = await api.updateAdminModule(body);
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
| **code** | `string` |  | [Defaults to `undefined`] |
| **sharedMarketplaceAdminModuleUpdateInput** | [SharedMarketplaceAdminModuleUpdateInput](SharedMarketplaceAdminModuleUpdateInput.md) |  | |

### Return type

[**SharedMarketplaceModuleDetail**](SharedMarketplaceModuleDetail.md)

### Authorization

[KeycloakAdminAuth accessCode](../README.md#KeycloakAdminAuth-accessCode)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | The request has succeeded. |  -  |
| **401** | Access is unauthorized. |  -  |
| **404** | The server cannot find the requested resource. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## updateMyTutorProfile

> SharedMarketplaceTutorProfile updateMyTutorProfile(sharedMarketplaceTutorProfileInput)

Update my tutor profile

Updates the authenticated tutor\&#39;s profile fields and publish status.

### Example

```ts
import {
  Configuration,
  DefaultApi,
} from '';
import type { UpdateMyTutorProfileRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // To configure OAuth2 access token for authorization: KeycloakAuth accessCode
    accessToken: "YOUR ACCESS TOKEN",
  });
  const api = new DefaultApi(config);

  const body = {
    // SharedMarketplaceTutorProfileInput
    sharedMarketplaceTutorProfileInput: ...,
  } satisfies UpdateMyTutorProfileRequest;

  try {
    const data = await api.updateMyTutorProfile(body);
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
| **sharedMarketplaceTutorProfileInput** | [SharedMarketplaceTutorProfileInput](SharedMarketplaceTutorProfileInput.md) |  | |

### Return type

[**SharedMarketplaceTutorProfile**](SharedMarketplaceTutorProfile.md)

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

