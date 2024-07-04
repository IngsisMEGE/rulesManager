package service.implementation

import dto.RuleDTO
import dto.SimpleRuleDTO
import model.Rule
import model.RuleType
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import repository.RuleRepository
import service.RuleService
import service.SnippetManagerService
import java.time.LocalDateTime
import java.util.Locale

@Service
class RuleServiceImpl(
    private val ruleRepository: RuleRepository,
    private val snippetManagerService: SnippetManagerService,
) : RuleService {
    override fun getUserRules(userEmail: String): List<SimpleRuleDTO> {
        val rules = ruleRepository.findAllUserRules(userEmail)
        return rules.map { ruleToSimpleRuleDTO(it) }
    }

    override fun getLintRules(userEmail: String): List<SimpleRuleDTO> {
        val rules = ruleRepository.findUserLintingRules(userEmail)
        return rules.map { ruleToSimpleRuleDTO(it) }
    }

    override fun getFormatRules(userEmail: String): List<SimpleRuleDTO> {
        val rules = ruleRepository.findUserFormatingRules(userEmail)
        return rules.map { ruleToSimpleRuleDTO(it) }
    }

    override fun getSCARules(userEmail: String): List<SimpleRuleDTO> {
        val rules = ruleRepository.findUserScaRules(userEmail)
        return rules.map { ruleToSimpleRuleDTO(it) }
    }

    override fun updateRule(
        userEmail: Jwt,
        rules: List<RuleDTO>,
    ): List<SimpleRuleDTO> {
        snippetManagerService.snippetsPending(userEmail).block()
        return rules.map { ruleDTO ->
            val ruleType = RuleType.valueOf(ruleDTO.ruleType.uppercase(Locale.getDefault()))
            val ruleToUpdate = ruleRepository.findById(ruleDTO.id).orElseThrow { throw Exception("Rule not found") }

            ruleToUpdate.name = ruleDTO.name
            ruleToUpdate.value = ruleDTO.value
            ruleToUpdate.type = ruleType
            ruleToUpdate.updatedAt = LocalDateTime.now()

            val updatedRule = ruleRepository.save(ruleToUpdate)
            ruleToSimpleRuleDTO(updatedRule)
        }
    }

    override fun updateRuleOnUse(
        userEmail: Jwt,
        rules: List<RuleDTO>,
    ): List<SimpleRuleDTO> {
        snippetManagerService.snippetsPending(userEmail).block()
        return rules.map { ruleDTO ->
            val ruleToUpdate = ruleRepository.findById(ruleDTO.id).orElseThrow { throw Exception("Rule not found") }

            ruleToUpdate.onUse = ruleDTO.onUse
            ruleToUpdate.updatedAt = LocalDateTime.now()

            val updatedRule = ruleRepository.save(ruleToUpdate)
            ruleToSimpleRuleDTO(updatedRule)
        }
    }

    private fun ruleToSimpleRuleDTO(rule: Rule): SimpleRuleDTO {
        return SimpleRuleDTO(
            name = rule.name,
            value = rule.value,
        )
    }
}
