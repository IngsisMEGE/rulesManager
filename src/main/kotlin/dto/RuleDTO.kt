package dto

import org.springframework.data.annotation.ReadOnlyProperty

data class RuleDTO(
    val id: Long,
    val name: String,
    val value: String,
    val ruleType: String,
    val onUse: Boolean,
    @ReadOnlyProperty
    val createdAt: String,
    @ReadOnlyProperty
    val updatedAt: String,
)

data class SimpleRuleDTO(
    val name: String,
    val value: String,
)

data class FormatRulesDTO(
    val formatRules: List<SimpleRuleDTO>,
    val lintingRules: List<SimpleRuleDTO>,
)

data class SCARulesDTO(
    val scaRules: List<SimpleRuleDTO>,
    val lintingRules: List<SimpleRuleDTO>,
)
