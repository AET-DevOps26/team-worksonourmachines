using Http;
using TypeSpec.Versioning;

enum Module2Versions {
  v1: "v1",
}

@service(#{ title: "Module2 API" })
@server("https://module2.example.com", "Module2 API")
@versioned(Module2Versions)
@route("/v1")
namespace Module2 {
  @get
  @route("/test")
  op test(): {
    @header contentType: "application/json";
    @statusCode statusCode: 200;
    message: string;
  };
}
