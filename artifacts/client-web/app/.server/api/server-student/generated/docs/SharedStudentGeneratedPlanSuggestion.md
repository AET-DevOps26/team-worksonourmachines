
# SharedStudentGeneratedPlanSuggestion


## Properties

Name | Type
------------ | -------------
`tier` | [SharedStudentPlanTier](SharedStudentPlanTier.md)
`description` | string
`totalEstimatedCost` | number
`proposedTutors` | [Array&lt;SharedStudentGeneratedPlanTutor&gt;](SharedStudentGeneratedPlanTutor.md)
`milestones` | [Array&lt;SharedStudentGeneratedPlanMilestone&gt;](SharedStudentGeneratedPlanMilestone.md)

## Example

```typescript
import type { SharedStudentGeneratedPlanSuggestion } from ''

// TODO: Update the object below with actual values
const example = {
  "tier": null,
  "description": null,
  "totalEstimatedCost": null,
  "proposedTutors": null,
  "milestones": null,
} satisfies SharedStudentGeneratedPlanSuggestion

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as SharedStudentGeneratedPlanSuggestion
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


