import "@typespec/http";

using TypeSpec.Http;

@doc("Keycloak OAuth2 client credentials auth for Scalar/API testing")
model KeycloakBearerAuth
  is OAuth2Auth<
    [
      {
        type: OAuth2FlowType.clientCredentials;
        tokenUrl: "https://auth.tutormatch.localhost/realms/tutormatch/protocol/openid-connect/token";
      }
    ],
    ["openid", "profile", "email", "roles"]
  >;

//import "@typespec/http";
//
//using TypeSpec.Http;
//
//@doc("Keycloak access token issued to the BFF client")
//model KeycloakBearerAuth {
//  type: AuthType.http;
//  scheme: "Bearer";
//}
