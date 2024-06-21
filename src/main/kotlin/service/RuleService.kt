package service

import model.RuleFormat
import model.RuleLinting
import model.RuleSCA

interface RuleService {
    fun getLintRules(): List<RuleLinting>

    fun getFormatRules(): List<RuleFormat>

    fun getSCARules(): List<RuleSCA>

    // fun getLintRules(userId: String): List<RuleLinting>

    // fun getFormatRules(userId: String): List<RuleFormat>

    // fun getSCARules(userId: String): List<RuleSCA>
}
