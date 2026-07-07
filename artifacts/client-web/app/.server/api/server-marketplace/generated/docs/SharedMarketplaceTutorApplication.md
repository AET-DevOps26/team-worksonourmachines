
# SharedMarketplaceTutorApplication


## Properties

Name | Type
------------ | -------------
`id` | string
`userId` | string
`moduleId` | string
`moduleCode` | string
`moduleTitle` | string
`status` | [SharedMarketplaceApplicationStatus](SharedMarketplaceApplicationStatus.md)
`certificateRef` | string
`submittedAt` | Date
`rejectionReason` | string

## Example

```typescript
import type { SharedMarketplaceTutorApplication } from ''

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "userId": null,
  "moduleId": null,
  "moduleCode": null,
  "moduleTitle": null,
  "status": null,
  "certificateRef": null,
  "submittedAt": null,
  "rejectionReason": null,
} satisfies SharedMarketplaceTutorApplication

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as SharedMarketplaceTutorApplication
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


