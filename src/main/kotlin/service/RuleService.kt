package service

import dto.RuleDTO
import dto.SimpleRuleDTO
import org.springframework.security.oauth2.jwt.Jwt

interface RuleService {
    fun getUserRules(userData: Jwt): List<SimpleRuleDTO>

    fun getFormatRules(userData: Jwt): List<SimpleRuleDTO>

    fun getSCARules(userData: Jwt): List<SimpleRuleDTO>

    fun getUserFormatRules(userData: Jwt): List<RuleDTO>

    fun updateRule(
        userData: Jwt,
        rules: List<RuleDTO>,
    ): List<SimpleRuleDTO>

    fun updateRuleOnUse(
        userData: Jwt,
        rules: List<RuleDTO>,
    ): List<SimpleRuleDTO>

    fun updateRules(
        userData: Jwt,
        rules: List<RuleDTO>,
    ): List<RuleDTO>
}
