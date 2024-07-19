package printscript.rulesManager.service

import org.springframework.security.oauth2.jwt.Jwt
import printscript.rulesManager.dto.RuleDTO
import printscript.rulesManager.dto.SimpleRuleDTO

interface RuleService {
    fun getUserRules(userData: Jwt): List<SimpleRuleDTO>

    fun getFormatRules(userData: Jwt): List<SimpleRuleDTO>

    fun getSCARules(userData: Jwt): List<SimpleRuleDTO>

    fun getUserFormatRules(userData: Jwt): List<RuleDTO>

    fun getUserSCARules(userData: Jwt): List<RuleDTO>

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
