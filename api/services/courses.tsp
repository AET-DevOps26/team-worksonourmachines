using Http;
using TypeSpec.Versioning;

enum CoursesVersions {
  v1: "v1",
}

@service(#{ title: "Courses API" })
@server("https://courses.example.com", "Courses API")
@versioned(CoursesVersions)
@route("/v1")
namespace Courses {
  @get
  @route("/test")
  op test(): {
    @header contentType: "application/json";
    @statusCode statusCode: 200;
    message: string;
  };
}
