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
  @summary("Chat with AI")
  @doc("Sends a prompt to the AI service and returns a generated response.")
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
