package dto

data class RuleDto(
    val id: Long,
    val name: String,
    val value: String,
    val ruleType: String,
    val createdAt: String,
    val updatedAt: String
)
