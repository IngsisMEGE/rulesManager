package service

import dto.FormatRulesDTO
import dto.SCARulesDTO
import org.springframework.security.oauth2.jwt.Jwt
import reactor.core.publisher.Mono

interface SnippetManagerService {
    fun updateSnippetsSCA(
        rules: SCARulesDTO,
        userData: Jwt,
    ): Mono<String>

    fun updateSnippetFormat(
        rules: FormatRulesDTO,
        userData: Jwt,
    ): Mono<String>
}
