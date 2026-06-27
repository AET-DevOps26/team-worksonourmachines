
# SharedMarketplaceTutorProfileInput


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

## Example

```typescript
import type { SharedMarketplaceTutorProfileInput } from ''

// TODO: Update the object below with actual values
const example = {
  "displayName": null,
  "bio": null,
  "languages": null,
  "locations": null,
  "hourlyRate": null,
  "availability": null,
  "published": null,
} satisfies SharedMarketplaceTutorProfileInput

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as SharedMarketplaceTutorProfileInput
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


