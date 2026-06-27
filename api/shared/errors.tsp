import "@typespec/http";

using TypeSpec.Http;

namespace Shared.Errors;

model ErrorBody {
  code: string;
  message: string;
}

@error
model NotFoundError {
  ...NotFoundResponse;
  ...ErrorBody;
}

@error
model BadRequestError {
  ...BadRequestResponse;
  ...ErrorBody;
}

@error
model UnauthorizedError {
  ...UnauthorizedResponse;
  ...ErrorBody;
}

@error
model ForbiddenError {
  ...ForbiddenResponse;
  ...ErrorBody;
}
