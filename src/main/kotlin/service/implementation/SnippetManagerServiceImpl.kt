package service.implementation

import dto.FormatRulesDTO
import dto.SCARulesDTO
import exceptions.NotFoundException
import io.github.cdimascio.dotenv.Dotenv
import org.apache.coyote.BadRequestException
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import service.SnippetManagerService

@Service
class SnippetManagerServiceImpl(private val webClient: WebClient, private val dotenv: Dotenv) : SnippetManagerService {
    private val snippetManagerURL = dotenv["SNIPPET_MANAGER_URL"]

    override fun updateSnippetsSCA(
        rules: SCARulesDTO,
        userData: Jwt,
    ): Mono<String> {
        return webClient.put()
            .uri("$snippetManagerURL/pending/user/sca")
            .header("Authorization", "Bearer ${userData.tokenValue}")
            .bodyValue(rules)
            .retrieve()
            .onStatus({ status -> status.is4xxClientError }) { response ->
                response.bodyToMono<String>().flatMap { errorBody ->
                    when (response.statusCode().value()) {
                        400 -> Mono.error(BadRequestException("Bad Request Getting Snippet: $errorBody"))
                        404 -> Mono.error(NotFoundException("Not Found Getting Snippet: $errorBody"))
                        else -> Mono.error(Exception("Error Getting Snippet: $errorBody"))
                    }
                }
            }
            .bodyToMono<String>()
    }

    override fun updateSnippetFormat(
        rules: FormatRulesDTO,
        userData: Jwt,
    ): Mono<String> {
        return webClient.put()
            .uri("$snippetManagerURL/pending/user/format")
            .header("Authorization", "Bearer ${userData.tokenValue}")
            .bodyValue(rules)
            .retrieve()
            .onStatus({ status -> status.is4xxClientError }) { response ->
                response.bodyToMono<String>().flatMap { errorBody ->
                    when (response.statusCode().value()) {
                        400 -> Mono.error(BadRequestException("Bad Request Getting Snippet: $errorBody"))
                        404 -> Mono.error(NotFoundException("Not Found Getting Snippet: $errorBody"))
                        else -> Mono.error(Exception("Error Getting Snippet: $errorBody"))
                    }
                }
            }
            .bodyToMono<String>()
    }
}
