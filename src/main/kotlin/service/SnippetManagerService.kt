package service

import org.springframework.security.oauth2.jwt.Jwt
import reactor.core.publisher.Mono

interface SnippetManagerService {
    fun snippetsPending(userData: Jwt): Mono<String>
}
