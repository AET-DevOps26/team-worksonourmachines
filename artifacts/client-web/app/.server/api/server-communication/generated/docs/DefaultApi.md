# DefaultApi

All URIs are relative to *https://communication.example.local*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**test**](DefaultApi.md#test) | **GET** /v1/test |  |



## test

> Test200Response test()



### Example

```ts
import {
  Configuration,
  DefaultApi,
} from '';
import type { TestRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: KeycloakBearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new DefaultApi(config);

  try {
    const data = await api.test();
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

[**Test200Response**](Test200Response.md)

### Authorization

[KeycloakBearerAuth](../README.md#KeycloakBearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | The request has succeeded. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

