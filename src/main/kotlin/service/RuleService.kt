package service

import model.Rule

interface RuleService {
    fun getLintRules(): List<Rule>

    fun getFormatRules(): List<Rule>

    fun getSCARules(): List<Rule>

    // fun getLintRules(userId: String): List<RuleLinting>

    // fun getFormatRules(userId: String): List<Rule>

    // fun getSCARules(userId: String): List<RuleSCA>
}
