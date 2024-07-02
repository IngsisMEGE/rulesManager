package controller

import dto.RuleDTO
import dto.SimpleRuleDTO
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
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
    fun updateUserLintRules(
        @AuthenticationPrincipal userData: Jwt,
        rule: RuleDTO,
    ): SimpleRuleDTO {
        try {
            val userMail = userData.claims["email"].toString()
            return ruleService.updateRule(userMail, rule)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }

    @PutMapping("/update/user/onUse")
    fun updateUserLintRulesOnUse(
        @AuthenticationPrincipal userData: Jwt,
        rule: RuleDTO,
    ): SimpleRuleDTO {
        try {
            val userMail = userData.claims["email"].toString()
            return ruleService.updateRuleOnUse(userMail, rule)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }
}
