package dto

import org.springframework.data.annotation.ReadOnlyProperty
import org.springframework.security.oauth2.jwt.Jwt

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

data class FormatSnippetWithRulesDTO(
    val snippetId: Long,
    val formatRules: List<SimpleRuleDTO>,
    val lintingRules: List<SimpleRuleDTO>,
)

data class FormatSnippetWithRulesRedisDTO(
    val formatSnippetWithRules: FormatSnippetWithRulesDTO,
    val userData: Jwt,
)

data class SCASnippetWithRulesDTO(
    val snippetId: Long,
    val scaRules: List<SimpleRuleDTO>,
    val lintingRules: List<SimpleRuleDTO>,
)

data class SCASnippetWithRulesRedisDTO(
    val scaSnippetWithRules: SCASnippetWithRulesDTO,
    val userData: Jwt,
)
