
# SharedCommunicationConversationSummary


## Properties

Name | Type
------------ | -------------
`id` | string
`partner` | [SharedCommunicationConversationPartner](SharedCommunicationConversationPartner.md)
`lastMessage` | string
`lastMessageAt` | Date
`updatedAt` | Date

## Example

```typescript
import type { SharedCommunicationConversationSummary } from ''

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "partner": null,
  "lastMessage": null,
  "lastMessageAt": null,
  "updatedAt": null,
} satisfies SharedCommunicationConversationSummary

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as SharedCommunicationConversationSummary
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


