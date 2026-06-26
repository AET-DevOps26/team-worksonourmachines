namespace Shared.StudyFocus {
  @doc("Ratings for study-skill dimensions on a 1 (low) to 5 (high) scale.")
  model StudyFocus {
    @doc("Recalling definitions, formulas, and terminology.")
    @minValue(1)
    @maxValue(5)
    memorization: int32;

    @doc("Proofs, logic, derivations, and symbolic manipulation.")
    @minValue(1)
    @maxValue(5)
    formalReasoning: int32;

    @doc("Building mental models and understanding why concepts work.")
    @minValue(1)
    @maxValue(5)
    conceptualUnderstanding: int32;

    @doc("Applying theory to exercises and exam-style problems.")
    @minValue(1)
    @maxValue(5)
    problemSolving: int32;
  }
}
