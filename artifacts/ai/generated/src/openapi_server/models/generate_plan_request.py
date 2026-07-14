# coding: utf-8

from __future__ import annotations
import pprint
import re  # noqa: F401
import json

from pydantic import BaseModel, Field, StrictStr
from typing import Any, ClassVar, Dict, List

try:
    from typing import Self
except ImportError:
    from typing_extensions import Self


class GeneratePlanRequest(BaseModel):
    """
    GeneratePlanRequest
    """  # noqa: E501

    learning_goal_id: StrictStr = Field(alias="learningGoalId")
    __properties: ClassVar[List[str]] = ["learningGoalId"]

    model_config = {
        "populate_by_name": True,
        "validate_assignment": True,
        "protected_namespaces": (),
    }

    def to_str(self) -> str:
        """Returns the string representation of the model using alias"""
        return pprint.pformat(self.model_dump(by_alias=True))

    def to_json(self) -> str:
        """Returns the JSON representation of the model using alias"""
        return json.dumps(self.to_dict())

    @classmethod
    def from_json(cls, json_str: str) -> Self:
        """Create an instance of GeneratePlanRequest from a JSON string"""
        return cls.from_dict(json.loads(json_str))

    def to_dict(self) -> Dict[str, Any]:
        """Return the dictionary representation of the model using alias."""
        return self.model_dump(by_alias=True, exclude={}, exclude_none=True)

    @classmethod
    def from_dict(cls, obj: Dict) -> Self:
        """Create an instance of GeneratePlanRequest from a dict"""
        if obj is None:
            return None
        if not isinstance(obj, dict):
            return cls.model_validate(obj)
        return cls.model_validate({"learningGoalId": obj.get("learningGoalId")})
