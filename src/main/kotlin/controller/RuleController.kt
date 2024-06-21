package controller

import model.RuleFormat
import model.RuleLinting
import model.RuleSCA
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import service.RuleService

@RestController
@RequestMapping("/rules")
class RuleController(private val ruleService: RuleService) {
    @GetMapping("/get/lint")
    fun getLintRules(): List<RuleLinting> {
        try {
            return ruleService.getLintRules()
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }

    @GetMapping("/get/format")
    fun getFormatRules(): List<RuleFormat> {
        try {
            return ruleService.getFormatRules()
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }

    @GetMapping("/get/sca")
    fun getSCARules(): List<RuleSCA> {
        try {
            return ruleService.getSCARules()
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }

    /*@GetMapping("/get/user/lint")
    fun getUserLintRules(authentication: JwtAuthenticationToken): List<RuleLinting> {
        try {
            val jwt = authentication.token
            val userId = jwt.claims["sub"] as String
            return ruleService.getLintRules(userId)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }

    @GetMapping("/get/user/format")
    fun getUserFormatRules(authentication: JwtAuthenticationToken): List<RuleFormat> {
        try {
            val jwt = authentication.token
            val userId = jwt.claims["sub"] as String
            return ruleService.getFormatRules(userId)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }

    @GetMapping("/get/user/sca")
    fun getUserSCARules(authentication: JwtAuthenticationToken): List<RuleSCA> {
        try {
            val jwt = authentication.token
            val userId = jwt.claims["sub"] as String
            return ruleService.getSCARules(userId)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }*/
}
