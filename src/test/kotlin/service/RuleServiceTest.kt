package service

import dto.FormatRulesDTO
import dto.RuleDTO
import dto.SCARulesDTO
import dto.SimpleRuleDTO
import model.Rule
import model.RuleType
import org.junit.jupiter.api.Assertions.assertEquals
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
import reactor.core.publisher.Mono
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

    private val testJwt = Jwt.withTokenValue("test")
        .header("alg", "RS256")
        .claim("email", "test@test.com")
        .build()

    @Test
    fun `updateRule updates rules and triggers SCA update`() {
        val rules = listOf(
            RuleDTO(1, "rule1", "newValue1", "SCA", true, LocalDateTime.now()),
            RuleDTO(2, "rule2", "newValue2", "LINTING", true, LocalDateTime.now())
        )
        val existingRule1 = Rule("rule1", true, RuleType.SCA, "value1")
        val existingRule2 = Rule("rule2", true, RuleType.FORMATING, "value2")

        whenever(ruleRepository.findById(1)).thenReturn(Optional.of(existingRule1))
        whenever(ruleRepository.findById(2)).thenReturn(Optional.of(existingRule2))
        whenever(ruleRepository.save(any())).thenAnswer { invocation ->
            invocation.getArgument<Rule>(0)
        }
        whenever(snippetManagerService.updateSnippetsSCA(any(), any())).thenReturn(Mono.just("Success"))

        val updatedRules = ruleService.updateRule(testJwt, rules)

        assertEquals("newValue1", updatedRules[0].value)
        assertEquals("newValue2", updatedRules[1].value)
        assertEquals(2, updatedRules.size)

        verify(snippetManagerService, times(1)).updateSnippetsSCA(any(SCARulesDTO::class.java), eq(testJwt))
    }

    @Test
    fun `updateRuleOnUse updates rules and triggers SCA and Format updates`() {
        val rules = listOf(
            RuleDTO(1, "rule1", "value1", "SCA", true, LocalDateTime.now()),
            RuleDTO(2, "rule2", "value2", "FORMATING", true, LocalDateTime.now())
        )
        val existingRule1 = Rule("rule1", true, RuleType.SCA, "value1")
        val existingRule2 = Rule("rule2", true, RuleType.FORMATING, "value2")

        whenever(ruleRepository.findById(1)).thenReturn(Optional.of(existingRule1))
        whenever(ruleRepository.findById(2)).thenReturn(Optional.of(existingRule2))
        whenever(ruleRepository.save(any())).thenAnswer { invocation ->
            invocation.getArgument<Rule>(0)
        }
        whenever(snippetManagerService.updateSnippetsSCA(any(), any())).thenReturn(Mono.just("Success"))
        whenever(snippetManagerService.updateSnippetFormat(any(), any())).thenReturn(Mono.just("Success"))

        val updatedRules = ruleService.updateRuleOnUse(testJwt, rules)

        verify(ruleRepository, times(2)).save(any())
        verify(snippetManagerService, times(1)).updateSnippetsSCA(any(SCARulesDTO::class.java), eq(testJwt))
        verify(snippetManagerService, times(1)).updateSnippetFormat(any(FormatRulesDTO::class.java), eq(testJwt))

        assertEquals(2, updatedRules.size)
    }


    @Test
    fun `getFormatRules for user returns expected rules`() {
        val expectedRules = listOf(Rule("rule5", true, RuleType.FORMATING, "value5"))
        whenever(ruleRepository.findUserFormatingRules("test@test.com")).thenReturn(expectedRules)

        val actualRules = ruleService.getFormatRules(testJwt)

        assertEquals(expectedRules.map { ruleToSimpleRuleDTO(it) }, actualRules)
    }

    @Test
    fun `updateRule throws exception when rule not found`() {
        val rules = listOf(
            RuleDTO(1, "rule1", "newValue1", "SCA", true, LocalDateTime.now())
        )

        whenever(ruleRepository.findById(1)).thenReturn(Optional.empty())

        val exception = org.junit.jupiter.api.assertThrows<NoSuchElementException> {
            ruleService.updateRule(testJwt, rules)
        }

        assertEquals("No value present", exception.message)
    }

    @Test
    fun `updateRuleOnUse throws exception when rule not found`() {
        val rules = listOf(
            RuleDTO(1, "rule1", "newValue1", "SCA", true, LocalDateTime.now())
        )

        whenever(ruleRepository.findById(1)).thenReturn(Optional.empty())

        val exception = org.junit.jupiter.api.assertThrows<NoSuchElementException> {
            ruleService.updateRuleOnUse(testJwt, rules)
        }

        assertEquals("No value present", exception.message)
    }

    private fun ruleToSimpleRuleDTO(rule: Rule): SimpleRuleDTO {
        return SimpleRuleDTO(
            name = rule.name,
            value = rule.value
        )
    }
}
