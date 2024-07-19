package printscript.rulesManager.service.implementation

import io.github.cdimascio.dotenv.Dotenv
import org.apache.coyote.BadRequestException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import printscript.rulesManager.dto.FormatRulesDTO
import printscript.rulesManager.dto.SCARulesDTO
import printscript.rulesManager.exceptions.NotFoundException
import printscript.rulesManager.logs.CorrIdFilter.Companion.CORRELATION_ID_KEY
import printscript.rulesManager.service.SnippetManagerService
import reactor.core.publisher.Mono

@Service
class SnippetManagerServiceImpl(private val webClient: WebClient, private val dotenv: Dotenv) : SnippetManagerService {
    private val logger: Logger = LoggerFactory.getLogger(SnippetManagerServiceImpl::class.java)
    private val snippetManagerURL = dotenv["SNIPPET_MANAGER_URL"]

    override fun updateSnippetsSCA(
        rules: SCARulesDTO,
        userData: Jwt,
    ): Mono<String> {
        logger.info("Entering updateSnippetsSCA for user")
        val headers =
            getHeader().apply {
                set("Authorization", "Bearer ${userData.tokenValue}")
            }
        return webClient.put()
            .uri("$snippetManagerURL/pending/user/sca")
            .headers { httpHeaders -> httpHeaders.addAll(headers) }
            .bodyValue(rules)
            .retrieve()
            .onStatus({ status -> status.is4xxClientError }) { response ->
                response.bodyToMono<String>().flatMap { errorBody ->
                    val statusCode = response.statusCode().value()
                    when (statusCode) {
                        400 -> {
                            logger.error("Bad Request Getting Snippet: $errorBody")
                            Mono.error(BadRequestException("Bad Request Getting Snippet: $errorBody"))
                        }
                        404 -> {
                            logger.error("Not Found Getting Snippet: $errorBody")
                            Mono.error(NotFoundException("Not Found Getting Snippet: $errorBody"))
                        }
                        else -> {
                            logger.error("Error Getting Snippet: $errorBody")
                            Mono.error(Exception("Error Getting Snippet: $errorBody"))
                        }
                    }
                }
            }
            .bodyToMono<String>()
            .doOnSuccess { logger.info("Successfully updated SCA snippets for user") }
            .doOnError { e -> logger.error("Error updating SCA snippets for user", e) }
            .doFinally { logger.info("Exiting updateSnippetsSCA for user") }
    }

    override fun updateSnippetFormat(
        rules: FormatRulesDTO,
        userData: Jwt,
    ): Mono<String> {
        logger.info("Entering updateSnippetFormat for user")
        val headers =
            getHeader().apply {
                set("Authorization", "Bearer ${userData.tokenValue}")
            }
        return webClient.put()
            .uri("$snippetManagerURL/pending/user/format")
            .headers { httpHeaders -> httpHeaders.addAll(headers) }
            .bodyValue(rules)
            .retrieve()
            .onStatus({ status -> status.is4xxClientError }) { response ->
                response.bodyToMono<String>().flatMap { errorBody ->
                    val statusCode = response.statusCode().value()
                    when (statusCode) {
                        400 -> {
                            logger.error("Bad Request Getting Snippet: $errorBody")
                            Mono.error(BadRequestException("Bad Request Getting Snippet: $errorBody"))
                        }
                        404 -> {
                            logger.error("Not Found Getting Snippet: $errorBody")
                            Mono.error(NotFoundException("Not Found Getting Snippet: $errorBody"))
                        }
                        else -> {
                            logger.error("Error Getting Snippet: $errorBody")
                            Mono.error(Exception("Error Getting Snippet: $errorBody"))
                        }
                    }
                }
            }
            .bodyToMono<String>()
            .doOnSuccess { logger.info("Successfully updated format snippets for user") }
            .doOnError { e -> logger.error("Error updating format snippets for user", e) }
            .doFinally { logger.info("Exiting updateSnippetFormat for user") }
    }

    private fun getHeader(): HttpHeaders {
        val correlationId = MDC.get(CORRELATION_ID_KEY)
        val headers =
            HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
                set("X-Correlation-Id", correlationId)
            }
        return headers
    }
}
