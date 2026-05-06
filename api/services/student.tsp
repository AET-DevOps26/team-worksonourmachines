using Http;
using TypeSpec.Versioning;

enum StudentVersions {
  v1: "v1",
}

@service(#{ title: "Student API" })
@server("https://student.example.local", "Student API")
@versioned(StudentVersions)
@route("/v1")
namespace Student {
  @get
  @route("/test")
  op test(): {
    @header contentType: "application/json";
    @statusCode statusCode: 200;
    message: string;
  };
}
