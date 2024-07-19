package dto

import org.springframework.data.annotation.ReadOnlyProperty
import java.time.LocalDateTime

data class RuleDTO(
    val id: Long,
    val name: String,
    val value: String,
    val ruleType: String,
    val isActive: Boolean,
    @ReadOnlyProperty
    val updatedAt: LocalDateTime,
)

data class SimpleRuleDTO(
    val name: String,
    val value: String,
)

data class FormatRulesDTO(
    val formatRules: List<SimpleRuleDTO>,
)

data class SCARulesDTO(
    val scaRules: List<SimpleRuleDTO>,
)
