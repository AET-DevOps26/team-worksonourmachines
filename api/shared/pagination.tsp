import "@typespec/http";

using TypeSpec.Http;

namespace Shared.Pagination;

model PageParams {
  @query
  @minValue(1)
  page?: int32 = 1;

  @query
  @minValue(1)
  @maxValue(100)
  pageSize?: int32 = 20;
}

model PageMeta {
  page: int32;
  pageSize: int32;
  total: int32;
}
