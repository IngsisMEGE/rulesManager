package service.implementation

import dto.FormatRulesDTO
import dto.RuleDTO
import dto.SCARulesDTO
import dto.SimpleRuleDTO
import model.Rule
import model.RuleType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
    private val logger: Logger = LoggerFactory.getLogger(RuleServiceImpl::class.java)

    override fun getUserRules(userEmail: String): List<SimpleRuleDTO> {
        logger.debug("Entering getUserRules for user")
        try {
            val rules = ruleRepository.findAllUserRules(userEmail)
            logger.info("Fetched ${rules.size} rules for user")
            val result = rules.map { ruleToSimpleRuleDTO(it) }
            logger.debug("Exiting getUserRules")
            return result
        } catch (e: Exception) {
            logger.error("Error fetching user rules", e)
            throw e
        }
    }

    override fun getLintRules(userEmail: String): List<SimpleRuleDTO> {
        logger.debug("Entering getLintRules for user")
        try {
            val rules = ruleRepository.findUserLintingRules(userEmail)
            val result = rules.map { ruleToSimpleRuleDTO(it) }
            logger.debug("Exiting getLintRules")
            return result
        } catch (e: Exception) {
            logger.error("Error fetching lint rules", e)
            throw e
        }
    }

    override fun getFormatRules(userEmail: String): List<SimpleRuleDTO> {
        logger.debug("Entering getFormatRules for user")
        try {
            val rules = ruleRepository.findUserFormatingRules(userEmail)
            val result = rules.map { ruleToSimpleRuleDTO(it) }
            logger.debug("Exiting getFormatRules")
            return result
        } catch (e: Exception) {
            logger.error("Error fetching format rules", e)
            throw e
        }
    }

    override fun getSCARules(userEmail: String): List<SimpleRuleDTO> {
        logger.debug("Entering getSCARules for user")
        try {
            val rules = ruleRepository.findUserScaRules(userEmail)
            val result = rules.map { ruleToSimpleRuleDTO(it) }
            logger.debug("Exiting getSCARules")
            return result
        } catch (e: Exception) {
            logger.error("Error fetching SCA rules", e)
            throw e
        }
    }

    override fun updateRule(
        userEmail: Jwt,
        rules: List<RuleDTO>,
    ): List<SimpleRuleDTO> {
        logger.debug("Entering updateRule for user with rules")
        try {
            val updatedRules =
                editRules(userEmail, rules) { ruleDTO, ruleToUpdate ->
                    val ruleType = RuleType.valueOf(ruleDTO.ruleType.uppercase(Locale.getDefault()))
                    ruleToUpdate.name = ruleDTO.name
                    ruleToUpdate.value = ruleDTO.value
                    ruleToUpdate.type = ruleType
                }
            logger.debug("Exiting updateRule")
            return updatedRules
        } catch (e: Exception) {
            logger.error("Error updating rules for user", e)
            throw e
        }
    }

    override fun updateRuleOnUse(
        userEmail: Jwt,
        rules: List<RuleDTO>,
    ): List<SimpleRuleDTO> {
        logger.debug("Entering updateRuleOnUse for user with rules")
        try {
            val updatedRules =
                editRules(userEmail, rules) { ruleDTO, ruleToUpdate ->
                    ruleToUpdate.onUse = ruleDTO.onUse
                }
            logger.debug("Exiting updateRuleOnUse")
            return updatedRules
        } catch (e: Exception) {
            logger.error("Error updating rules on use for user", e)
            throw e
        }
    }

    private fun editRules(
        userEmail: Jwt,
        rules: List<RuleDTO>,
        updateRuleProperties: (RuleDTO, Rule) -> Unit,
    ): List<SimpleRuleDTO> {
        logger.debug("Entering editRules for user with rules")
        var runSCA = false
        var runFormat = false

        val updatedRules =
            rules.map { ruleDTO ->
                val ruleToUpdate =
                    ruleRepository.findById(ruleDTO.id).orElseThrow {
                        logger.error("Rule not found with id: ${ruleDTO.id}")
                        throw Exception("Rule not found")
                    }

                updateRuleProperties(ruleDTO, ruleToUpdate)
                ruleToUpdate.updatedAt = LocalDateTime.now()

                if (ruleToUpdate.type == RuleType.SCA || ruleToUpdate.type == RuleType.LINTING) {
                    runSCA = true
                }
                if (ruleToUpdate.type == RuleType.LINTING) {
                    runFormat = true
                }

                val updatedRule = ruleRepository.save(ruleToUpdate)
                logger.info("Updated rule with id: ${ruleDTO.id}")
                ruleToSimpleRuleDTO(updatedRule)
            }

        if (runSCA) updateStatusSCA(userEmail)
        if (runFormat) updateStatusFormat(userEmail)

        logger.debug("Exiting editRules")
        return updatedRules
    }

    private fun ruleToSimpleRuleDTO(rule: Rule): SimpleRuleDTO {
        return SimpleRuleDTO(
            name = rule.name,
            value = rule.value,
        )
    }

    private fun updateStatusSCA(userData: Jwt) {
        logger.debug("Entering updateStatusSCA for user")
        try {
            val email = userData.claims["email"].toString()
            val rules = SCARulesDTO(getSCARules(email), getLintRules(email))
            snippetManagerService.updateSnippetsSCA(rules, userData)
            logger.debug("Exiting updateStatusSCA")
        } catch (e: Exception) {
            logger.error("Error updating status SCA", e)
            throw e
        }
    }

    private fun updateStatusFormat(userData: Jwt) {
        logger.debug("Entering updateStatusFormat for user")
        try {
            val email = userData.claims["email"].toString()
            val rules = FormatRulesDTO(getFormatRules(email), getLintRules(email))
            snippetManagerService.updateSnippetFormat(rules, userData)
            logger.debug("Exiting updateStatusFormat")
        } catch (e: Exception) {
            logger.error("Error updating status format", e)
            throw e
        }
    }
}
