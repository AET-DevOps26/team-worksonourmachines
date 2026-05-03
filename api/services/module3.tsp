using Http;
using TypeSpec.Versioning;

enum Module3Versions {
  v1: "v1",
}

@service(#{ title: "Module3 API" })
@server("https://module3.example.com", "Module3 API")
@versioned(Module3Versions)
@route("/v1")
namespace Module3 {
  @get
  @route("/test")
  op test(): {
    @header contentType: "application/json";
    @statusCode statusCode: 200;
    message: string;
  };
}
