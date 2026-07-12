
# SharedStudentLearningGoalInput

The template for omitting properties.

## Properties

Name | Type
------------ | -------------
`moduleId` | string
`description` | string
`targetDate` | Date
`selfAssessedLevel` | number
`budgetEur` | number
`locations` | [Array&lt;SharedMarketplaceLocation&gt;](SharedMarketplaceLocation.md)

## Example

```typescript
import type { SharedStudentLearningGoalInput } from ''

// TODO: Update the object below with actual values
const example = {
  "moduleId": null,
  "description": null,
  "targetDate": null,
  "selfAssessedLevel": null,
  "budgetEur": null,
  "locations": null,
} satisfies SharedStudentLearningGoalInput

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as SharedStudentLearningGoalInput
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


