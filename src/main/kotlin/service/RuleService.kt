package service

import dto.RuleDTO
import dto.SimpleRuleDTO
import org.springframework.security.oauth2.jwt.Jwt

interface RuleService {
    fun getUserRules(userEmail: String): List<SimpleRuleDTO>

    fun getLintRules(userEmail: String): List<SimpleRuleDTO>

    fun getFormatRules(userEmail: String): List<SimpleRuleDTO>

    fun getSCARules(userEmail: String): List<SimpleRuleDTO>

    fun updateRule(
        userEmail: Jwt,
        rules: List<RuleDTO>,
    ): List<SimpleRuleDTO>

    fun updateRuleOnUse(
        userEmail: Jwt,
        rules: List<RuleDTO>,
    ): List<SimpleRuleDTO>
}
