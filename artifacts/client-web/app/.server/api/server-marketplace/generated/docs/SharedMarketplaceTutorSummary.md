
# SharedMarketplaceTutorSummary


## Properties

Name | Type
------------ | -------------
`id` | string
`userId` | string
`displayName` | string
`hourlyRate` | number
`languages` | Array&lt;string&gt;
`locations` | [Array&lt;SharedMarketplaceLocation&gt;](SharedMarketplaceLocation.md)
`coverages` | [Array&lt;SharedMarketplaceTutorCoverage&gt;](SharedMarketplaceTutorCoverage.md)

## Example

```typescript
import type { SharedMarketplaceTutorSummary } from ''

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "userId": null,
  "displayName": null,
  "hourlyRate": null,
  "languages": null,
  "locations": null,
  "coverages": null,
} satisfies SharedMarketplaceTutorSummary

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as SharedMarketplaceTutorSummary
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


