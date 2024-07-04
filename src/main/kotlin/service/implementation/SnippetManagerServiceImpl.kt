package service.implementation

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import service.SnippetManagerService

class SnippetManagerServiceImpl(private val webClient: WebClient, private val dotenv: Dotenv) : SnippetManagerService {
    private val snippetManagerURL = dotenv["SNIPPET_MANAGER_URL"]

    override fun snippetsPending(userData: Jwt): Mono<String> {
        return webClient.post()
            .uri("$snippetManagerURL/pending/user/")
            .header("Authorization", "Bearer ${userData.tokenValue}")
            .bodyValue("")
            .retrieve()
            .bodyToMono<String>()
    }
}
