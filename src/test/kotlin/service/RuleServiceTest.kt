package service

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
    fun `getFormatRules returns expected rules`() {
        val expectedRules = listOf(Rule("rule2", true, RuleType.FORMATING, "value2"))
        `when`(ruleRepository.findAllFormatingRules()).thenReturn(expectedRules)

        val actualRules = ruleService.getFormatRules()

        assertEquals(expectedRules, actualRules)
    }

    @Test
    fun `getSCARules returns expected rules`() {
        val expectedRules = listOf(Rule("rule3", true, RuleType.SCA, "value3"))
        `when`(ruleRepository.findAllScaRules()).thenReturn(expectedRules)

        val actualRules = ruleService.getSCARules()

        assertEquals(expectedRules, actualRules)
    }

    @Test
    fun `getLintRules for user returns expected rules`() {
        val userEmail = "test@example.com"
        val expectedRules = listOf(Rule("rule4", true, RuleType.LINTING, "value4"))
        `when`(ruleRepository.findUserLintingRules(userEmail)).thenReturn(expectedRules)

        val actualRules = ruleService.getLintRules(userEmail)

        assertEquals(expectedRules, actualRules)
    }

    @Test
    fun `getFormatRules for user returns expected rules`() {
        val userEmail = "test@example.com"
        val expectedRules = listOf(Rule("rule5", true, RuleType.FORMATING, "value5"))
        `when`(ruleRepository.findUserFormatingRules(userEmail)).thenReturn(expectedRules)

        val actualRules = ruleService.getFormatRules(userEmail)

        assertEquals(expectedRules, actualRules)
    }
}
