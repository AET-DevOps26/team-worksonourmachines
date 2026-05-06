using Http;
using TypeSpec.Versioning;

enum MarketplaceVersions {
  v1: "v1",
}

@service(#{ title: "Marketplace API" })
@server("https://marketplace.example.local", "Marketplace API")
@versioned(MarketplaceVersions)
@route("/v1")
namespace Marketplace {
  @get
  @route("/test")
  op test(): {
    @header contentType: "application/json";
    @statusCode statusCode: 200;
    message: string;
  };
}
