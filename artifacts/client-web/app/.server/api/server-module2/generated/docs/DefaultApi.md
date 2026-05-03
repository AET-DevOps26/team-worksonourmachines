# DefaultApi

All URIs are relative to *https://module2.example.com*

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
  const api = new DefaultApi();

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

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | The request has succeeded. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

