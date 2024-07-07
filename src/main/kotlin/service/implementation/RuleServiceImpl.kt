package service.implementation

import dto.FormatRulesDTO
import dto.RuleDTO
import dto.SCARulesDTO
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
        return editRules(userEmail, rules) { ruleDTO, ruleToUpdate ->
            val ruleType = RuleType.valueOf(ruleDTO.ruleType.uppercase(Locale.getDefault()))
            ruleToUpdate.name = ruleDTO.name
            ruleToUpdate.value = ruleDTO.value
            ruleToUpdate.type = ruleType
        }
    }

    override fun updateRuleOnUse(
        userEmail: Jwt,
        rules: List<RuleDTO>,
    ): List<SimpleRuleDTO> {
        return editRules(userEmail, rules) { ruleDTO, ruleToUpdate ->
            ruleToUpdate.onUse = ruleDTO.onUse
        }
    }

    private fun editRules(
        userEmail: Jwt,
        rules: List<RuleDTO>,
        updateRuleProperties: (RuleDTO, Rule) -> Unit,
    ): List<SimpleRuleDTO> {
        var runSCA = false
        var runFormat = false

        val updatedRules =
            rules.map { ruleDTO ->
                val ruleToUpdate = ruleRepository.findById(ruleDTO.id).orElseThrow { Exception("Rule not found") }

                updateRuleProperties(ruleDTO, ruleToUpdate)
                ruleToUpdate.updatedAt = LocalDateTime.now()

                if (ruleToUpdate.type == RuleType.SCA || ruleToUpdate.type == RuleType.LINTING) {
                    runSCA = true
                }
                if (ruleToUpdate.type == RuleType.LINTING) {
                    runFormat = true
                }

                val updatedRule = ruleRepository.save(ruleToUpdate)
                ruleToSimpleRuleDTO(updatedRule)
            }

        if (runSCA) updateStatusSCA(userEmail)
        if (runFormat) updateStatusFormat(userEmail)

        return updatedRules
    }

    private fun ruleToSimpleRuleDTO(rule: Rule): SimpleRuleDTO {
        return SimpleRuleDTO(
            name = rule.name,
            value = rule.value,
        )
    }

    private fun updateStatusSCA(userData: Jwt) {
        val email = userData.claims["email"].toString()
        val rules = SCARulesDTO(getSCARules(email), getLintRules(email))
        snippetManagerService.updateSnippetsSCA(rules, userData)
    }

    private fun updateStatusFormat(userData: Jwt) {
        val email = userData.claims["email"].toString()
        val rules = FormatRulesDTO(getFormatRules(email), getLintRules(email))
        snippetManagerService.updateSnippetFormat(rules, userData)
    }
}
