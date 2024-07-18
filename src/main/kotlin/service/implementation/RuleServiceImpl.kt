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

    override fun getUserRules(userData: Jwt): List<SimpleRuleDTO> {
        logger.debug("Entering getUserRules for user")
        try {
            val userEmail = userData.claims["email"].toString()
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

    override fun getLintRules(userData: Jwt): List<SimpleRuleDTO> {
        logger.debug("Entering getLintRules for user")
        try {
            val userEmail = userData.claims["email"].toString()
            val rules = ruleRepository.findUserLintingRules(userEmail)
            val result = rules.map { ruleToSimpleRuleDTO(it) }
            logger.debug("Exiting getLintRules")
            return result
        } catch (e: Exception) {
            logger.error("Error fetching lint rules", e)
            throw e
        }
    }

    override fun getFormatRules(userData: Jwt): List<SimpleRuleDTO> {
        logger.debug("Entering getFormatRules for user")
        try {
            val userEmail = userData.claims["email"].toString()
            val rules = ruleRepository.findUserFormatingRules(userEmail)
            val result = rules.map { ruleToSimpleRuleDTO(it) }
            logger.debug("Exiting getFormatRules")
            return result
        } catch (e: Exception) {
            logger.error("Error fetching format rules", e)
            throw e
        }
    }

    override fun getSCARules(userData: Jwt): List<SimpleRuleDTO> {
        logger.debug("Entering getSCARules for user")
        try {
            val userEmail = userData.claims["email"].toString()
            val rules = ruleRepository.findUserScaRules(userEmail)
            val result = rules.map { ruleToSimpleRuleDTO(it) }
            logger.debug("Exiting getSCARules")
            return result
        } catch (e: Exception) {
            logger.error("Error fetching SCA rules", e)
            throw e
        }
    }

    override fun getUserFormatRules(userData: Jwt): List<RuleDTO> {
        logger.debug("Entering getUserFormatRules for user")
        try {
            val userEmail = userData.claims["email"].toString()
            val rules = ruleRepository.findUserFormatingRules(userEmail)
            val result =
                rules.map {
                    RuleDTO(
                        id = it.id,
                        name = it.name,
                        value = it.value,
                        ruleType = it.type.name,
                        isActive = it.isActive,
                        updatedAt = it.updatedAt,
                    )
                }
            logger.debug("Exiting getUserFormatRules")
            return result
        } catch (e: Exception) {
            logger.error("Error fetching user format rules", e)
            throw e
        }
    }

    override fun getUserLintingRules(userData: Jwt): List<RuleDTO> {
        logger.debug("Entering getUserLintingRules for user")
        try {
            val userEmail = userData.claims["email"].toString()
            val rules = ruleRepository.findUserLintingRules(userEmail)
            val result =
                rules.map {
                    RuleDTO(
                        id = it.id,
                        name = it.name,
                        value = it.value,
                        ruleType = it.type.name,
                        isActive = it.isActive,
                        updatedAt = it.updatedAt,
                    )
                }
            logger.debug("Exiting getUserLintingRules")
            return result
        } catch (e: Exception) {
            logger.error("Error fetching user linting rules", e)
            throw e
        }
    }

    override fun updateRule(
        userData: Jwt,
        rules: List<RuleDTO>,
    ): List<SimpleRuleDTO> {
        logger.debug("Entering updateRule for user with rules")
        try {
            val updatedRules =
                editRules(userData, rules) { ruleDTO, ruleToUpdate ->
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
        userData: Jwt,
        rules: List<RuleDTO>,
    ): List<SimpleRuleDTO> {
        logger.debug("Entering updateRuleOnUse for user with rules")
        try {
            val updatedRules =
                editRules(userData, rules) { ruleDTO, ruleToUpdate ->
                    ruleToUpdate.isActive = ruleDTO.isActive
                }
            logger.debug("Exiting updateRuleOnUse")
            return updatedRules
        } catch (e: Exception) {
            logger.error("Error updating rules on use for user", e)
            throw e
        }
    }

    override fun updateRules(
        userData: Jwt,
        rules: List<RuleDTO>,
    ): List<RuleDTO> {
        logger.debug("Entering updateRules for user with rules")
        try {
            val updatedRules: List<RuleDTO> =
                editRulesWithRuleDTO(userData, rules) { ruleDTO, ruleToUpdate ->
                    val ruleType = RuleType.valueOf(ruleDTO.ruleType.uppercase(Locale.getDefault()))
                    ruleToUpdate.name = ruleDTO.name
                    ruleToUpdate.value = ruleDTO.value
                    ruleToUpdate.type = ruleType
                }
            logger.debug("Exiting updateRules")
            return updatedRules
        } catch (e: Exception) {
            logger.error("Error updating rules for user", e)
            throw e
        }
    }

    private fun editRules(
        userData: Jwt,
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

        if (runSCA) updateStatusSCA(userData)
        if (runFormat) updateStatusFormat(userData)

        logger.debug("Exiting editRules")
        return updatedRules
    }

    private fun editRulesWithRuleDTO(
        userData: Jwt,
        rules: List<RuleDTO>,
        updateRuleProperties: (RuleDTO, Rule) -> Unit,
    ): List<RuleDTO> {
        logger.debug("Entering editRulesWithRuleDTO for user with rules")
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
                RuleDTO(
                    id = updatedRule.id,
                    name = updatedRule.name,
                    value = updatedRule.value,
                    ruleType = updatedRule.type.name,
                    isActive = updatedRule.isActive,
                    updatedAt = updatedRule.updatedAt,
                )
            }

        if (runSCA) updateStatusSCA(userData)
        if (runFormat) updateStatusFormat(userData)

        logger.debug("Exiting editRulesWithRuleDTO")
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
            val rules = SCARulesDTO(getSCARules(userData), getLintRules(userData))
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
            val rules = FormatRulesDTO(getFormatRules(userData), getLintRules(userData))
            snippetManagerService.updateSnippetFormat(rules, userData)
            logger.debug("Exiting updateStatusFormat")
        } catch (e: Exception) {
            logger.error("Error updating status format", e)
            throw e
        }
    }
}
