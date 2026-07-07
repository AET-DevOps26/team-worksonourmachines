import "../shared/security.tsp";
import "../shared/errors.tsp";
import "../shared/pagination.tsp";

using TypeSpec.Http;
using TypeSpec.Versioning;
using Shared.Communication;
using Shared.Errors;
using Shared.Pagination;

namespace Shared.Communication {
  model ConversationPartner {
    userId: string;
    displayName: string;
    tutorId?: string;
  }

  model ConversationSummary {
    id: string;
    partner: ConversationPartner;
    lastMessage: string;
    lastMessageAt: utcDateTime;
    updatedAt: utcDateTime;
  }

  model ConversationDetail {
    id: string;
    partner: ConversationPartner;
    createdAt: utcDateTime;
    updatedAt: utcDateTime;
  }

  model ChatMessage {
    id: string;
    senderId: string;
    content: string;
    sentAt: utcDateTime;
  }

  model StartConversationRequest {
    participantUserId: string;
  }

  model SendMessageRequest {
    content: string;
  }
}

enum CommunicationVersions {
  v1: "v1",
}

@service(#{ title: "Communication API" })
@server("https://communication.example.local", "Communication API")
@versioned(CommunicationVersions)
@useAuth(KeycloakBearerAuth)
@route("/v1")
namespace Communication {
  model MessagePage {
    items: ChatMessage[];
    ...PageMeta;
  }

  @summary("List conversations")
  @doc("Returns conversation summaries for the authenticated user, ordered by recent activity.")
  @get
  @route("/conversations")
  op listConversations(): ConversationSummary[] | UnauthorizedError;

  @summary("Start conversation")
  @doc("Starts a new conversation with another user or returns the existing one.")
  @post
  @route("/conversations")
  op startConversation(@body body: StartConversationRequest):
    | ConversationDetail
    | BadRequestError
    | UnauthorizedError;

  @summary("Get conversation")
  @doc("Returns conversation metadata and the chat partner for the authenticated user.")
  @get
  @route("/conversations/{id}")
  op getConversation(@path id: string):
    | ConversationDetail
    | NotFoundError
    | UnauthorizedError;

  @summary("List messages")
  @doc("Returns a paginated list of messages in a conversation.")
  @get
  @route("/conversations/{id}/messages")
  op listMessages(@path id: string, ...PageParams):
    | MessagePage
    | NotFoundError
    | UnauthorizedError;

  @summary("Send message")
  @doc("Posts a new message to a conversation.")
  @post
  @route("/conversations/{id}/messages")
  op sendMessage(@path id: string, @body body: SendMessageRequest):
    | ChatMessage
    | NotFoundError
    | BadRequestError
    | UnauthorizedError;
}
