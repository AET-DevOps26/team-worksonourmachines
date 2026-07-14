# coding: utf-8

from __future__ import annotations
import pprint
import re  # noqa: F401
import json

from pydantic import BaseModel, Field, StrictFloat, StrictStr
from typing import Any, ClassVar, Dict, List, Optional

try:
    from typing import Self
except ImportError:
    from typing_extensions import Self


class PlanMilestone(BaseModel):
    """
    PlanMilestone
    """  # noqa: E501

    title: StrictStr
    due_date: StrictStr = Field(alias="dueDate")
    topic_id: StrictStr = Field(alias="topicId")
    tutor_id: StrictStr = Field(alias="tutorId")
    estimated_cost: float = Field(alias="estimatedCost")
    __properties: ClassVar[List[str]] = ["title", "dueDate", "topicId", "tutorId", "estimatedCost"]

    model_config = {
        "populate_by_name": True,
        "validate_assignment": True,
        "protected_namespaces": (),
    }

    def to_str(self) -> str:
        return pprint.pformat(self.model_dump(by_alias=True))

    def to_json(self) -> str:
        return json.dumps(self.to_dict())

    @classmethod
    def from_json(cls, json_str: str) -> Self:
        return cls.from_dict(json.loads(json_str))

    def to_dict(self) -> Dict[str, Any]:
        return self.model_dump(by_alias=True, exclude={}, exclude_none=True)

    @classmethod
    def from_dict(cls, obj: Dict) -> Self:
        if obj is None:
            return None
        if not isinstance(obj, dict):
            return cls.model_validate(obj)
        return cls.model_validate({
            "title": obj.get("title"),
            "dueDate": obj.get("dueDate"),
            "topicId": str(obj["topicId"]) if obj.get("topicId") is not None else None,
            "tutorId": str(obj["tutorId"]) if obj.get("tutorId") is not None else None,
            "estimatedCost": obj.get("estimatedCost"),
        })


class ProposedTutor(BaseModel):
    """
    ProposedTutor
    """  # noqa: E501

    id: StrictStr
    display_name: StrictStr = Field(alias="displayName")
    hourly_rate: float = Field(alias="hourlyRate")
    __properties: ClassVar[List[str]] = ["id", "displayName", "hourlyRate"]

    model_config = {
        "populate_by_name": True,
        "validate_assignment": True,
        "protected_namespaces": (),
    }

    def to_str(self) -> str:
        return pprint.pformat(self.model_dump(by_alias=True))

    def to_json(self) -> str:
        return json.dumps(self.to_dict())

    @classmethod
    def from_json(cls, json_str: str) -> Self:
        return cls.from_dict(json.loads(json_str))

    def to_dict(self) -> Dict[str, Any]:
        return self.model_dump(by_alias=True, exclude={}, exclude_none=True)

    @classmethod
    def from_dict(cls, obj: Dict) -> Self:
        if obj is None:
            return None
        if not isinstance(obj, dict):
            return cls.model_validate(obj)
        return cls.model_validate({
            "id": str(obj["id"]) if obj.get("id") is not None else None,
            "displayName": obj.get("displayName"),
            "hourlyRate": obj.get("hourlyRate"),
        })


class PlanSuggestion(BaseModel):
    """
    PlanSuggestion
    """  # noqa: E501

    tier: StrictStr
    description: StrictStr
    total_estimated_cost: float = Field(alias="totalEstimatedCost")
    proposed_tutors: List[ProposedTutor] = Field(alias="proposedTutors")
    milestones: List[PlanMilestone]
    __properties: ClassVar[List[str]] = [
        "tier", "description", "totalEstimatedCost", "proposedTutors", "milestones"
    ]

    model_config = {
        "populate_by_name": True,
        "validate_assignment": True,
        "protected_namespaces": (),
    }

    def to_str(self) -> str:
        return pprint.pformat(self.model_dump(by_alias=True))

    def to_json(self) -> str:
        return json.dumps(self.to_dict())

    @classmethod
    def from_json(cls, json_str: str) -> Self:
        return cls.from_dict(json.loads(json_str))

    def to_dict(self) -> Dict[str, Any]:
        _dict = self.model_dump(by_alias=True, exclude={}, exclude_none=True)
        if self.proposed_tutors:
            _dict["proposedTutors"] = [t.to_dict() for t in self.proposed_tutors]
        if self.milestones:
            _dict["milestones"] = [m.to_dict() for m in self.milestones]
        return _dict

    @classmethod
    def from_dict(cls, obj: Dict) -> Self:
        if obj is None:
            return None
        if not isinstance(obj, dict):
            return cls.model_validate(obj)
        return cls.model_validate({
            "tier": obj.get("tier"),
            "description": obj.get("description"),
            "totalEstimatedCost": obj.get("totalEstimatedCost"),
            "proposedTutors": [ProposedTutor.from_dict(t) for t in obj.get("proposedTutors", [])],
            "milestones": [PlanMilestone.from_dict(m) for m in obj.get("milestones", [])],
        })


class GeneratePlanResponse(BaseModel):
    """
    GeneratePlanResponse
    """  # noqa: E501

    learning_goal_id: StrictStr = Field(alias="learningGoalId")
    suggestions: List[PlanSuggestion]
    __properties: ClassVar[List[str]] = ["learningGoalId", "suggestions"]

    model_config = {
        "populate_by_name": True,
        "validate_assignment": True,
        "protected_namespaces": (),
    }

    def to_str(self) -> str:
        return pprint.pformat(self.model_dump(by_alias=True))

    def to_json(self) -> str:
        return json.dumps(self.to_dict())

    @classmethod
    def from_json(cls, json_str: str) -> Self:
        return cls.from_dict(json.loads(json_str))

    def to_dict(self) -> Dict[str, Any]:
        _dict = self.model_dump(by_alias=True, exclude={}, exclude_none=True)
        if self.suggestions:
            _dict["suggestions"] = [s.to_dict() for s in self.suggestions]
        return _dict

    @classmethod
    def from_dict(cls, obj: Dict) -> Self:
        if obj is None:
            return None
        if not isinstance(obj, dict):
            return cls.model_validate(obj)
        return cls.model_validate({
            "learningGoalId": obj.get("learningGoalId"),
            "suggestions": [PlanSuggestion.from_dict(s) for s in obj.get("suggestions", [])],
        })
