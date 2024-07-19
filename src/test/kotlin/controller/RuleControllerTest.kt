package controller

import dto.RuleDTO
import dto.SimpleRuleDTO
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.server.ResponseStatusException
import service.RuleService
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class RuleControllerTest {
    @Mock
    private lateinit var ruleService: RuleService

    @InjectMocks
    private lateinit var ruleController: RuleController

    private val testJwt =
        Jwt.withTokenValue("test")
            .header("alg", "RS256")
            .claim("email", "test@test.com")
            .build()

    @Test
    fun getUserRulesReturnsRulesForUser() {
        val expectedRules = listOf(SimpleRuleDTO("rule1", "value1"), SimpleRuleDTO("rule2", "value2"))
        `when`(ruleService.getUserRules(testJwt)).thenReturn(expectedRules)

        val actualRules = ruleController.getUserRules(testJwt)

        assertEquals(expectedRules, actualRules)
    }

    @Test
    fun getUserRulesThrowsInternalServerErrorOnException() {
        `when`(ruleService.getUserRules(testJwt)).thenThrow(RuntimeException("Service error"))

        val exception =
            org.junit.jupiter.api.assertThrows<ResponseStatusException> {
                ruleController.getUserRules(testJwt)
            }

        assertEquals("Service error", exception.reason)
    }

    @Test
    fun getUserFormatRulesReturnsFormatRulesForUser() {
        val expectedRules = listOf(SimpleRuleDTO("rule1", "value1"))
        `when`(ruleService.getFormatRules(testJwt)).thenReturn(expectedRules)

        val actualRules = ruleController.getUserFormatRules(testJwt)

        assertEquals(expectedRules, actualRules)
    }

    @Test
    fun getUserFormatRulesThrowsInternalServerErrorOnException() {
        `when`(ruleService.getFormatRules(testJwt)).thenThrow(RuntimeException("Service error"))

        val exception =
            org.junit.jupiter.api.assertThrows<ResponseStatusException> {
                ruleController.getUserFormatRules(testJwt)
            }

        assertEquals("Service error", exception.reason)
    }

    @Test
    fun getUserSCARulesReturnsSCARulesForUser() {
        val expectedRules = listOf(SimpleRuleDTO("rule1", "value1"))
        `when`(ruleService.getSCARules(testJwt)).thenReturn(expectedRules)

        val actualRules = ruleController.getUserSCARules(testJwt)

        assertEquals(expectedRules, actualRules)
    }

    @Test
    fun getUserSCARulesThrowsInternalServerErrorOnException() {
        `when`(ruleService.getSCARules(testJwt)).thenThrow(RuntimeException("Service error"))

        val exception =
            org.junit.jupiter.api.assertThrows<ResponseStatusException> {
                ruleController.getUserSCARules(testJwt)
            }

        assertEquals("Service error", exception.reason)
    }

    @Test
    fun updateUserRulesUpdatesRulesAndReturnsUpdatedRules() {
        val rules = listOf(RuleDTO(1, "rule1", "newValue1", "SCA", true, LocalDateTime.now()))
        val expectedRules = listOf(SimpleRuleDTO("rule1", "newValue1"))
        `when`(ruleService.updateRule(testJwt, rules)).thenReturn(expectedRules)

        val actualRules = ruleController.updateUserRules(testJwt, rules)

        assertEquals(expectedRules, actualRules)
    }

    @Test
    fun updateUserRulesThrowsInternalServerErrorOnException() {
        val rules = listOf(RuleDTO(1, "rule1", "newValue1", "SCA", true, LocalDateTime.now()))
        `when`(ruleService.updateRule(testJwt, rules)).thenThrow(RuntimeException("Service error"))

        val exception =
            org.junit.jupiter.api.assertThrows<ResponseStatusException> {
                ruleController.updateUserRules(testJwt, rules)
            }

        assertEquals("Service error", exception.reason)
    }

    @Test
    fun updateUserRulesOnUseUpdatesRulesAndReturnsUpdatedRules() {
        val rules = listOf(RuleDTO(1, "rule1", "newValue1", "SCA", true, LocalDateTime.now()))
        val expectedRules = listOf(SimpleRuleDTO("rule1", "newValue1"))
        `when`(ruleService.updateRuleOnUse(testJwt, rules)).thenReturn(expectedRules)

        val actualRules = ruleController.updateUserRulesOnUse(testJwt, rules)

        assertEquals(expectedRules, actualRules)
    }

    @Test
    fun updateUserRulesOnUseThrowsInternalServerErrorOnException() {
        val rules = listOf(RuleDTO(1, "rule1", "newValue1", "SCA", true, LocalDateTime.now()))
        `when`(ruleService.updateRuleOnUse(testJwt, rules)).thenThrow(RuntimeException("Service error"))

        val exception =
            org.junit.jupiter.api.assertThrows<ResponseStatusException> {
                ruleController.updateUserRulesOnUse(testJwt, rules)
            }

        assertEquals("Service error", exception.reason)
    }

    @Test
    fun getFormattingRolesReturnsFormattingRolesForUser() {
        val expectedRules = listOf(RuleDTO(1, "rule1", "value1", "FORMATING", true, LocalDateTime.now()))
        `when`(ruleService.getUserFormatRules(testJwt)).thenReturn(expectedRules)

        val actualRules = ruleController.getFormattingRoles(testJwt)

        assertEquals(expectedRules, actualRules)
    }

    @Test
    fun getFormattingRolesThrowsInternalServerErrorOnException() {
        `when`(ruleService.getUserFormatRules(testJwt)).thenThrow(RuntimeException("Service error"))

        val exception =
            org.junit.jupiter.api.assertThrows<ResponseStatusException> {
                ruleController.getFormattingRoles(testJwt)
            }

        assertEquals("Service error", exception.reason)
    }

    @Test
    fun updateRulesUpdatesRulesAndReturnsUpdatedRules() {
        val rules = listOf(RuleDTO(1, "rule1", "newValue1", "SCA", true, LocalDateTime.now()))
        val expectedRules = listOf(RuleDTO(1, "rule1", "newValue1", "SCA", true, LocalDateTime.now()))
        `when`(ruleService.updateRules(testJwt, rules)).thenReturn(expectedRules)

        val actualRules = ruleController.updateRules(testJwt, rules)

        assertEquals(expectedRules, actualRules)
    }

    @Test
    fun updateRulesThrowsInternalServerErrorOnException() {
        val rules = listOf(RuleDTO(1, "rule1", "newValue1", "SCA", true, LocalDateTime.now()))
        `when`(ruleService.updateRules(testJwt, rules)).thenThrow(RuntimeException("Service error"))

        val exception =
            org.junit.jupiter.api.assertThrows<ResponseStatusException> {
                ruleController.updateRules(testJwt, rules)
            }

        assertEquals("Service error", exception.reason)
    }
}
