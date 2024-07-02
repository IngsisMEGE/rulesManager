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
    val id: Long,
    val name: String,
    val value: String,
    val ruleType: String,
)
