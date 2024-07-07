package service

import dto.RuleDTO
import dto.SimpleRuleDTO
import model.Rule
import model.RuleType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever
import org.springframework.security.oauth2.jwt.Jwt
import repository.RuleRepository
import service.implementation.RuleServiceImpl
import java.util.*

class RuleServiceTest {
    private val ruleRepository = mock(RuleRepository::class.java)
    private val snippetManagerService: SnippetManagerService = mock(SnippetManagerService::class.java)
    private val ruleService = RuleServiceImpl(ruleRepository, snippetManagerService)

    val testJwt = "test"
    val userEmail =
        Jwt.withTokenValue(testJwt)
            .header("alg", "RS256") // Add the algorithm header (you may adjust this based on your JWT)
            .claim("email", "test@test.com") // Extract other claims as needed
            .build()

    @Test
    fun `updateRule updates rules and triggers SCA and Format updates`() {
        val rules =
            listOf(
                RuleDTO(1, "rule1", "newValue1", "SCA", true, "", ""),
                RuleDTO(2, "rule2", "newValue2", "LINTING", true, "", ""),
            )
        val existingRule1 = Rule("rule1", true, RuleType.SCA, "value1")
        val existingRule2 = Rule("rule2", true, RuleType.LINTING, "value2")

        whenever(ruleRepository.findById(1)).thenReturn(Optional.of(existingRule1))
        whenever(ruleRepository.findById(2)).thenReturn(Optional.of(existingRule2))
        whenever(ruleRepository.save(any())).thenAnswer { it.getArgument(0) }

        val updatedRules = ruleService.updateRule(userEmail, rules)

        assertEquals(updatedRules.get(0).value, "newValue1")
        assertEquals(updatedRules.get(1).value, "newValue2")

        assertEquals(2, updatedRules.size)
    }

    @Test
    fun `updateRuleOnUse updates rules and triggers SCA and Format updates`() {
        val rules =
            listOf(
                RuleDTO(1, "rule1", "value1", "SCA", true, "", ""),
                RuleDTO(2, "rule2", "value2", "LINTING", true, "", ""),
            )
        val existingRule1 = Rule("rule1", true, RuleType.SCA, "value1")
        val existingRule2 = Rule("rule2", true, RuleType.LINTING, "value2")

        whenever(ruleRepository.findById(1)).thenReturn(Optional.of(existingRule1))
        whenever(ruleRepository.findById(2)).thenReturn(Optional.of(existingRule2))
        whenever(ruleRepository.save(any())).thenAnswer { it.getArgument(0) }

        val updatedRules = ruleService.updateRuleOnUse(userEmail, rules)

        verify(ruleRepository, times(2)).save(any())
        verify(snippetManagerService, times(1))

        assertEquals(2, updatedRules.size)
    }

    @Test
    fun `getLintRules for user returns expected rules`() {
        val userEmail = "test@example.com"
        val expectedRules = listOf(Rule("rule4", true, RuleType.LINTING, "value4"))
        whenever(ruleRepository.findUserLintingRules(userEmail)).thenReturn(expectedRules)

        val actualRules = ruleService.getLintRules(userEmail)

        assertEquals(expectedRules.map { ruleToSimpleRuleDTO(it) }, actualRules)
    }

    @Test
    fun `getFormatRules for user returns expected rules`() {
        val userEmail = "test@example.com"
        val expectedRules = listOf(Rule("rule5", true, RuleType.FORMATING, "value5"))
        whenever(ruleRepository.findUserFormatingRules(userEmail)).thenReturn(expectedRules)

        val actualRules = ruleService.getFormatRules(userEmail)

        assertEquals(expectedRules.map { ruleToSimpleRuleDTO(it) }, actualRules)
    }

    private fun ruleToSimpleRuleDTO(rule: Rule): SimpleRuleDTO {
        return SimpleRuleDTO(
            name = rule.name,
            value = rule.value,
        )
    }
}
