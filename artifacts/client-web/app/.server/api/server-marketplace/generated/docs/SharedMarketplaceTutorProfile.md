
# SharedMarketplaceTutorProfile


## Properties

Name | Type
------------ | -------------
`displayName` | string
`bio` | string
`languages` | Array&lt;string&gt;
`locations` | [Array&lt;SharedMarketplaceLocation&gt;](SharedMarketplaceLocation.md)
`hourlyRate` | number
`availability` | [Array&lt;SharedMarketplaceTutorAvailability&gt;](SharedMarketplaceTutorAvailability.md)
`published` | boolean
`id` | string
`userId` | string

## Example

```typescript
import type { SharedMarketplaceTutorProfile } from ''

// TODO: Update the object below with actual values
const example = {
  "displayName": null,
  "bio": null,
  "languages": null,
  "locations": null,
  "hourlyRate": null,
  "availability": null,
  "published": null,
  "id": null,
  "userId": null,
} satisfies SharedMarketplaceTutorProfile

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as SharedMarketplaceTutorProfile
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


