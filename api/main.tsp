import "@typespec/http";
import "@typespec/openapi3";

using Http;

@service(#{ title: "Courses API" })
@server("https://courses.example.com", "Courses API")
@route("/test")
namespace Courses {
  @get
  op test(): {
    @header contentType: "application/json";
    @statusCode statusCode: 200;
    message: string;
  };
}

@service(#{ title: "Module2 API" })
@server("https://module2.example.com", "Module2 API")
@route("/module2")
namespace Module2 {
  @get
  op test(): {
    @header contentType: "application/json";
    @statusCode statusCode: 200;
    message: string;
  };
}