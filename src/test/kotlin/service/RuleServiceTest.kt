package service

import dto.RuleDTO
import dto.SimpleRuleDTO
import model.Rule
import model.RuleType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.security.oauth2.jwt.Jwt
import repository.RuleRepository
import service.implementation.RuleServiceImpl
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class RuleServiceTest {
    @Mock
    private lateinit var ruleRepository: RuleRepository

    @Mock
    private lateinit var snippetManagerService: SnippetManagerService

    @InjectMocks
    private lateinit var ruleService: RuleServiceImpl

    @Captor
    private lateinit var ruleCaptor: ArgumentCaptor<Rule>

    private val testJwt =
        Jwt.withTokenValue("test")
            .header("alg", "RS256")
            .claim("email", "test@test.com")
            .build()

    @Test
    fun updateRuleUpdatesRulesCorrectly() {
        val rulesToUpdate =
            listOf(
                RuleDTO(1L, "Updated Rule 1", "New Value 1", "SCA", true, LocalDateTime.now()),
                RuleDTO(2L, "Updated Rule 2", "New Value 2", "FORMATING", true, LocalDateTime.now()),
            )
        val existingRule1 = Rule("Rule 1", true, RuleType.SCA, "Value 1").apply { id = 1L }
        val existingRule2 = Rule("Rule 2", true, RuleType.FORMATING, "Value 2").apply { id = 2L }

        whenever(ruleRepository.findById(1L)).thenReturn(Optional.of(existingRule1))
        whenever(ruleRepository.findById(2L)).thenReturn(Optional.of(existingRule2))
        whenever(ruleRepository.save(any())).thenAnswer { it.arguments[0] }

        val updatedRules = ruleService.updateRule(testJwt, rulesToUpdate)

        verify(ruleRepository, times(2)).save(ruleCaptor.capture())
        val savedRules = ruleCaptor.allValues
        assertTrue(savedRules.any { it.name == "Updated Rule 1" && it.value == "New Value 1" })
        assertTrue(savedRules.any { it.name == "Updated Rule 2" && it.value == "New Value 2" })

        assertEquals(2, updatedRules.size)
    }

    @Test
    fun getFormatRulesForUserReturnsExpectedRules() {
        val expectedRules = listOf(Rule("rule5", true, RuleType.FORMATING, "value5"))
        whenever(ruleRepository.findUserFormatingRules("test@test.com")).thenReturn(expectedRules)

        val actualRules = ruleService.getFormatRules(testJwt)

        assertEquals(expectedRules.map { ruleToSimpleRuleDTO(it) }, actualRules)
    }

    @Test
    fun updateRuleThrowsExceptionWhenRuleNotFound() {
        val rules =
            listOf(
                RuleDTO(1, "rule1", "newValue1", "SCA", true, LocalDateTime.now()),
            )

        whenever(ruleRepository.findById(1)).thenReturn(Optional.empty())

        val exception =
            org.junit.jupiter.api.assertThrows<NoSuchElementException> {
                ruleService.updateRule(testJwt, rules)
            }

        assertEquals("Rule not found with id: 1", exception.message)
    }

    @Test
    fun updateRuleOnUseThrowsExceptionWhenRuleNotFound() {
        val rules =
            listOf(
                RuleDTO(1, "rule1", "newValue1", "SCA", true, LocalDateTime.now()),
            )

        whenever(ruleRepository.findById(1)).thenReturn(Optional.empty())

        val exception =
            org.junit.jupiter.api.assertThrows<NoSuchElementException> {
                ruleService.updateRuleOnUse(testJwt, rules)
            }

        assertEquals("Rule not found with id: 1", exception.message)
    }

    @Test
    fun getUserRulesReturnsCorrectRulesForUser() {
        val expectedRules = listOf(Rule("rule1", true, RuleType.SCA, "value1"))
        whenever(ruleRepository.findAllUserRules("test@test.com")).thenReturn(expectedRules)

        val actualRules = ruleService.getUserRules(testJwt)

        assertEquals(expectedRules.map { ruleToSimpleRuleDTO(it) }, actualRules)
    }

    @Test
    fun getSCARulesReturnsCorrectSCARulesForUser() {
        val expectedSCARules = listOf(Rule("SCA Rule", true, RuleType.SCA, "SCA Value"))
        whenever(ruleRepository.findUserScaRules("test@test.com")).thenReturn(expectedSCARules)

        val actualSCARules = ruleService.getSCARules(testJwt)

        assertEquals(expectedSCARules.map { ruleToSimpleRuleDTO(it) }, actualSCARules)
    }

    @Test
    fun getUserFormatRulesReturnsFormatRulesForUser() {
        val userEmail = "test@test.com"
        val expectedRules =
            listOf(
                Rule("Format Rule 1", true, RuleType.FORMATING, "Value 1").apply {
                    id = 1L
                    updatedAt = LocalDateTime.now()
                },
                Rule("Format Rule 2", true, RuleType.FORMATING, "Value 2").apply {
                    id = 2L
                    updatedAt = LocalDateTime.now()
                },
            )
        whenever(ruleRepository.findUserFormatingRules(userEmail)).thenReturn(expectedRules)

        val actualRules = ruleService.getUserFormatRules(testJwt)

        val expectedDTOs =
            expectedRules.map {
                RuleDTO(
                    id = it.id,
                    name = it.name,
                    value = it.value,
                    ruleType = it.type.name,
                    isActive = it.isActive,
                    updatedAt = it.updatedAt,
                )
            }
        assertEquals(expectedDTOs, actualRules)
    }

    private fun ruleToSimpleRuleDTO(rule: Rule): SimpleRuleDTO {
        return SimpleRuleDTO(
            name = rule.name,
            value = rule.value,
        )
    }

    private fun convertRuleDTOToRule(ruleDTO: RuleDTO): Rule {
        return Rule(
            name = ruleDTO.name,
            isActive = ruleDTO.isActive,
            type = RuleType.valueOf(ruleDTO.ruleType),
            value = ruleDTO.value,
        )
    }
}
