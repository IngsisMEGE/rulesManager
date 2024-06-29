package service

import model.Rule

interface RuleService {
    fun getLintRules(): List<Rule>

    fun getFormatRules(): List<Rule>

    fun getSCARules(): List<Rule>

    fun getLintRules(userEmail: String): List<Rule>

    fun getFormatRules(userEmail: String): List<Rule>

    fun getSCARules(userEmail: String): List<Rule>
}
