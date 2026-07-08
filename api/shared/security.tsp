import "@typespec/http";

using TypeSpec.Http;

@doc("Keycloak OAuth2 client credentials auth for Scalar/API testing")
model KeycloakClientAuth
  is OAuth2Auth<
    [
      {
        type: OAuth2FlowType.clientCredentials;
        tokenUrl: "https://auth.tutormatch.localhost/realms/tutormatch/protocol/openid-connect/token";
      }
    ],
    ["openid", "profile", "email", "roles"]
  >;

@doc("Keycloak OAuth2 authorization code auth for user login")
model KeycloakAuth
  is OAuth2Auth<
    [
      {
        type: OAuth2FlowType.authorizationCode;
        authorizationUrl: "https://auth.tutormatch.localhost/realms/tutormatch/protocol/openid-connect/auth";
        tokenUrl: "https://auth.tutormatch.localhost/realms/tutormatch/protocol/openid-connect/token";
        refreshUrl: "https://auth.tutormatch.localhost/realms/tutormatch/protocol/openid-connect/token";
      }
    ],
    ["openid", "basic", "profile", "email", "roles"]
  >;
