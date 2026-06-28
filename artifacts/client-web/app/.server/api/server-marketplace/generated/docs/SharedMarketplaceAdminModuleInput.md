
# SharedMarketplaceAdminModuleInput


## Properties

Name | Type
------------ | -------------
`code` | string
`title` | string
`description` | string
`difficultyHint` | string
`topics` | [Array&lt;SharedMarketplaceTopicInput&gt;](SharedMarketplaceTopicInput.md)

## Example

```typescript
import type { SharedMarketplaceAdminModuleInput } from ''

// TODO: Update the object below with actual values
const example = {
  "code": null,
  "title": null,
  "description": null,
  "difficultyHint": null,
  "topics": null,
} satisfies SharedMarketplaceAdminModuleInput

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as SharedMarketplaceAdminModuleInput
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


