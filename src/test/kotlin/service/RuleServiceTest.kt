package service

import dto.SimpleRuleDTO
import model.Rule
import model.RuleType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import repository.RuleRepository
import service.implementation.RuleServiceImpl

class RuleServiceTest {
    private val ruleRepository = mock(RuleRepository::class.java)
    private val ruleService = RuleServiceImpl(ruleRepository)

    @Test
    fun `getLintRules for user returns expected rules`() {
        val userEmail = "test@example.com"
        val expectedRules = listOf(Rule("rule4", true, RuleType.LINTING, "value4"))
        `when`(ruleRepository.findUserLintingRules(userEmail)).thenReturn(expectedRules)

        val actualRules = ruleService.getLintRules(userEmail)

        assertEquals(expectedRules.map { ruleToSimpleRuleDTO(it) }, actualRules)
    }

    @Test
    fun `getFormatRules for user returns expected rules`() {
        val userEmail = "test@example.com"
        val expectedRules = listOf(Rule("rule5", true, RuleType.FORMATING, "value5"))
        `when`(ruleRepository.findUserFormatingRules(userEmail)).thenReturn(expectedRules)

        val actualRules = ruleService.getFormatRules(userEmail)

        assertEquals(expectedRules.map { ruleToSimpleRuleDTO(it) }, actualRules)
    }

    private fun ruleToSimpleRuleDTO(rule: Rule): SimpleRuleDTO {
        return SimpleRuleDTO(
            id = rule.id,
            name = rule.name,
            value = rule.value,
            ruleType = rule.type.name,
        )
    }
}
