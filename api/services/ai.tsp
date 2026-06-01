using Http;
using TypeSpec.Versioning;

enum AiVersions {
  v1: "v1",
}

@service(#{ title: "AI API" })
@server("https://ai.example.local", "AI API")
@versioned(AiVersions)
@route("/v1")
namespace Ai {
  @get
  @route("/test")
  op test(): {
    @header contentType: "application/json";
    @statusCode statusCode: 200;
    message: string;
  };

  @post
  @route("/chat")
  op chat(
    @body body: {
      prompt: string;
    },
  ): {
    @header contentType: "application/json";
    @statusCode statusCode: 200;
    message: string;
  };
}
