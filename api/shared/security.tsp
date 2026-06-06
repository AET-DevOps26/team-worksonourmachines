import "@typespec/http";

using TypeSpec.Http;

@doc("Keycloak access token issued to the BFF client")
model KeycloakBearerAuth {
  type: AuthType.http;
  scheme: "Bearer";
}
