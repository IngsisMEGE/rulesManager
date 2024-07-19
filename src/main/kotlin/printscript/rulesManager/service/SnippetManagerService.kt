package printscript.rulesManager.service

import org.springframework.security.oauth2.jwt.Jwt
import printscript.rulesManager.dto.FormatRulesDTO
import printscript.rulesManager.dto.SCARulesDTO
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
