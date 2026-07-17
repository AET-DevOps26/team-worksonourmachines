# DefaultApi

All URIs are relative to *https://communication.example.local*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createWsTicket**](DefaultApi.md#createwsticket) | **POST** /v1/conversations/ws-ticket | Create WebSocket ticket |
| [**getConversation**](DefaultApi.md#getconversation) | **GET** /v1/conversations/{id} | Get conversation |
| [**listConversations**](DefaultApi.md#listconversations) | **GET** /v1/conversations | List conversations |
| [**listMessages**](DefaultApi.md#listmessages) | **GET** /v1/conversations/{id}/messages | List messages |
| [**sendMessage**](DefaultApi.md#sendmessage) | **POST** /v1/conversations/{id}/messages | Send message |
| [**startConversation**](DefaultApi.md#startconversation) | **POST** /v1/conversations | Start conversation |



## createWsTicket

> SharedCommunicationWsTicket createWsTicket()

Create WebSocket ticket

Issues a short-lived ticket (Redis TTL) for authenticating a STOMP WebSocket CONNECT.

### Example

```ts
import {
  Configuration,
  DefaultApi,
} from '';
import type { CreateWsTicketRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // To configure OAuth2 access token for authorization: KeycloakClientAuth application
    accessToken: "YOUR ACCESS TOKEN",
  });
  const api = new DefaultApi(config);

  try {
    const data = await api.createWsTicket();
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

[**SharedCommunicationWsTicket**](SharedCommunicationWsTicket.md)

### Authorization

[KeycloakClientAuth application](../README.md#KeycloakClientAuth-application)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | The request has succeeded. |  -  |
| **401** | Access is unauthorized. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getConversation

> SharedCommunicationConversationDetail getConversation(id)

Get conversation

Returns conversation metadata and the chat partner for the authenticated user.

### Example

```ts
import {
  Configuration,
  DefaultApi,
} from '';
import type { GetConversationRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // To configure OAuth2 access token for authorization: KeycloakClientAuth application
    accessToken: "YOUR ACCESS TOKEN",
  });
  const api = new DefaultApi(config);

  const body = {
    // string
    id: id_example,
  } satisfies GetConversationRequest;

  try {
    const data = await api.getConversation(body);
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

[**SharedCommunicationConversationDetail**](SharedCommunicationConversationDetail.md)

### Authorization

[KeycloakClientAuth application](../README.md#KeycloakClientAuth-application)

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


## listConversations

> Array&lt;SharedCommunicationConversationSummary&gt; listConversations()

List conversations

Returns conversation summaries for the authenticated user, ordered by recent activity.

### Example

```ts
import {
  Configuration,
  DefaultApi,
} from '';
import type { ListConversationsRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // To configure OAuth2 access token for authorization: KeycloakClientAuth application
    accessToken: "YOUR ACCESS TOKEN",
  });
  const api = new DefaultApi(config);

  try {
    const data = await api.listConversations();
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

[**Array&lt;SharedCommunicationConversationSummary&gt;**](SharedCommunicationConversationSummary.md)

### Authorization

[KeycloakClientAuth application](../README.md#KeycloakClientAuth-application)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | The request has succeeded. |  -  |
| **401** | Access is unauthorized. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## listMessages

> MessagePage listMessages(id, page, pageSize)

List messages

Returns a paginated list of messages in a conversation.

### Example

```ts
import {
  Configuration,
  DefaultApi,
} from '';
import type { ListMessagesRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // To configure OAuth2 access token for authorization: KeycloakClientAuth application
    accessToken: "YOUR ACCESS TOKEN",
  });
  const api = new DefaultApi(config);

  const body = {
    // string
    id: id_example,
    // number (optional)
    page: 56,
    // number (optional)
    pageSize: 56,
  } satisfies ListMessagesRequest;

  try {
    const data = await api.listMessages(body);
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
| **page** | `number` |  | [Optional] [Defaults to `1`] |
| **pageSize** | `number` |  | [Optional] [Defaults to `20`] |

### Return type

[**MessagePage**](MessagePage.md)

### Authorization

[KeycloakClientAuth application](../README.md#KeycloakClientAuth-application)

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


## sendMessage

> SharedCommunicationChatMessage sendMessage(id, sharedCommunicationSendMessageRequest)

Send message

Posts a new message to a conversation.

### Example

```ts
import {
  Configuration,
  DefaultApi,
} from '';
import type { SendMessageRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // To configure OAuth2 access token for authorization: KeycloakClientAuth application
    accessToken: "YOUR ACCESS TOKEN",
  });
  const api = new DefaultApi(config);

  const body = {
    // string
    id: id_example,
    // SharedCommunicationSendMessageRequest
    sharedCommunicationSendMessageRequest: ...,
  } satisfies SendMessageRequest;

  try {
    const data = await api.sendMessage(body);
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
| **sharedCommunicationSendMessageRequest** | [SharedCommunicationSendMessageRequest](SharedCommunicationSendMessageRequest.md) |  | |

### Return type

[**SharedCommunicationChatMessage**](SharedCommunicationChatMessage.md)

### Authorization

[KeycloakClientAuth application](../README.md#KeycloakClientAuth-application)

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


## startConversation

> SharedCommunicationConversationDetail startConversation(sharedCommunicationStartConversationRequest)

Start conversation

Starts a new conversation with another user or returns the existing one.

### Example

```ts
import {
  Configuration,
  DefaultApi,
} from '';
import type { StartConversationRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // To configure OAuth2 access token for authorization: KeycloakClientAuth application
    accessToken: "YOUR ACCESS TOKEN",
  });
  const api = new DefaultApi(config);

  const body = {
    // SharedCommunicationStartConversationRequest
    sharedCommunicationStartConversationRequest: ...,
  } satisfies StartConversationRequest;

  try {
    const data = await api.startConversation(body);
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
| **sharedCommunicationStartConversationRequest** | [SharedCommunicationStartConversationRequest](SharedCommunicationStartConversationRequest.md) |  | |

### Return type

[**SharedCommunicationConversationDetail**](SharedCommunicationConversationDetail.md)

### Authorization

[KeycloakClientAuth application](../README.md#KeycloakClientAuth-application)

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

