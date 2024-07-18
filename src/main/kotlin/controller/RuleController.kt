package controller

import com.newrelic.agent.deps.org.slf4j.MDC
import dto.RuleDTO
import dto.SimpleRuleDTO
import logs.CorrIdFilter.Companion.CORRELATION_ID_KEY
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import service.RuleService

@RestController
@RequestMapping("/rules")
class RuleController(private val ruleService: RuleService) {
    @GetMapping("/get/user/all")
    fun getUserRules(
        @AuthenticationPrincipal userData: Jwt,
    ): List<SimpleRuleDTO> {
        try {
            val userMail = userData.claims["email"].toString()
            return ruleService.getUserRules(userMail)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }

    @GetMapping("/get/user/lint")
    fun getUserLintRules(
        @AuthenticationPrincipal userData: Jwt,
    ): List<SimpleRuleDTO> {
        try {
            val userMail = userData.claims["email"].toString()
            return ruleService.getLintRules(userMail)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }

    @GetMapping("/get/user/format")
    fun getUserFormatRules(
        @AuthenticationPrincipal userData: Jwt,
    ): List<SimpleRuleDTO> {
        try {
            val userMail = userData.claims["email"].toString()
            return ruleService.getFormatRules(userMail)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }

    @GetMapping("/get/user/sca")
    fun getUserSCARules(
        @AuthenticationPrincipal userData: Jwt,
    ): List<SimpleRuleDTO> {
        try {
            val userMail = userData.claims["email"].toString()
            return ruleService.getSCARules(userMail)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }

    @PutMapping("/update/user/")
    fun updateUserRules(
        @AuthenticationPrincipal userData: Jwt,
        @RequestBody rules: List<RuleDTO>,
    ): List<SimpleRuleDTO> {
        try {
            return ruleService.updateRule(userData, rules)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }

    @PutMapping("/update/user/onUse")
    fun updateUserRulesOnUse(
        @AuthenticationPrincipal userData: Jwt,
        @RequestBody rules: List<RuleDTO>,
    ): List<SimpleRuleDTO> {
        try {
            return ruleService.updateRuleOnUse(userData, rules)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }

    private fun getHeaders(): HttpHeaders {
        return HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("X-Correlation-Id", MDC.get(CORRELATION_ID_KEY))
        }
    }
}
