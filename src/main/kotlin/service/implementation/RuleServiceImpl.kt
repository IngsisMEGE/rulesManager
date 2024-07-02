package service.implementation

import dto.RuleDTO
import dto.SimpleRuleDTO
import model.Rule
import model.RuleType
import org.springframework.stereotype.Service
import repository.RuleRepository
import service.RuleService
import java.time.LocalDateTime
import java.util.Locale

@Service
class RuleServiceImpl(
    private val ruleRepository: RuleRepository,
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
        userEmail: String,
        rule: RuleDTO,
    ): SimpleRuleDTO {
        val ruleType = RuleType.valueOf(rule.ruleType.uppercase(Locale.getDefault()))
        val ruleToUpdate = ruleRepository.findById(rule.id).orElseThrow { throw Exception("Rule not found") }

        ruleToUpdate.name = rule.name
        ruleToUpdate.value = rule.value
        ruleToUpdate.type = ruleType
        ruleToUpdate.updatedAt = LocalDateTime.now()

        val updatedRule = ruleRepository.save(ruleToUpdate)
        return ruleToSimpleRuleDTO(updatedRule)
    }

    override fun updateRuleOnUse(
        userEmail: String,
        rule: RuleDTO,
    ): SimpleRuleDTO {
        val ruleToUpdate = ruleRepository.findById(rule.id).orElseThrow { throw Exception("Rule not found") }

        ruleToUpdate.onUse = rule.onUse
        ruleToUpdate.updatedAt = LocalDateTime.now()

        val updatedRule = ruleRepository.save(ruleToUpdate)
        return ruleToSimpleRuleDTO(updatedRule)
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
