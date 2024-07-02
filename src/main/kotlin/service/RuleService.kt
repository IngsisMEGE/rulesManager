package service

import dto.RuleDTO
import dto.SimpleRuleDTO

interface RuleService {
    fun getUserRules(userEmail: String): List<SimpleRuleDTO>

    fun getLintRules(userEmail: String): List<SimpleRuleDTO>

    fun getFormatRules(userEmail: String): List<SimpleRuleDTO>

    fun getSCARules(userEmail: String): List<SimpleRuleDTO>

    fun updateRule(
        userEmail: String,
        rule: RuleDTO,
    ): SimpleRuleDTO

    fun updateRuleOnUse(
        userEmail: String,
        rule: RuleDTO,
    ): SimpleRuleDTO
}
