package printscript.rulesManager.service.implementation

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import printscript.rulesManager.dto.FormatRulesDTO
import printscript.rulesManager.dto.RuleDTO
import printscript.rulesManager.dto.SCARulesDTO
import printscript.rulesManager.dto.SimpleRuleDTO
import printscript.rulesManager.model.CommonRule
import printscript.rulesManager.model.Rule
import printscript.rulesManager.model.RuleType
import printscript.rulesManager.repository.CommonRuleRepository
import printscript.rulesManager.repository.RuleRepository
import printscript.rulesManager.service.RuleService
import printscript.rulesManager.service.SnippetManagerService
import java.time.LocalDateTime
import java.util.Locale

@Service
class RuleServiceImpl(
    private val ruleRepository: RuleRepository,
    private val snippetManagerService: SnippetManagerService,
    private val commonRuleRepository: CommonRuleRepository,
) : RuleService {
    private val logger: Logger = LoggerFactory.getLogger(RuleServiceImpl::class.java)

    override fun getUserRules(userData: Jwt): List<SimpleRuleDTO> {
        logger.debug("Entering getUserRules for user")
        val userEmail = userData.claims["email"].toString()
        val rules = ruleRepository.findAllUserRules(userEmail)
        logger.info("Fetched ${rules.size} rules for user")
        val result = rules.map { ruleToSimpleRuleDTO(it) }
        logger.debug("Exiting getUserRules")
        if (result.isEmpty()) {
            return commonRuleRepository.findAll().map { commmonRuleToSimpleRuleDTO(it) }
        }
        return result
    }

    override fun getFormatRules(userData: Jwt): List<SimpleRuleDTO> {
        logger.debug("Entering getFormatRules for user")
        val userEmail = userData.claims["email"].toString()
        val rules = ruleRepository.findUserFormatingRules(userEmail)
        val result = rules.map { ruleToSimpleRuleDTO(it) }
        logger.debug("Exiting getFormatRules")
        if (result.isEmpty()) {
            val commonRules = commonRuleRepository.findAll()
            return commonRules.filter { it.type == RuleType.FORMATING }.map { commmonRuleToSimpleRuleDTO(it) }
        }
        return result
    }

    override fun getSCARules(userData: Jwt): List<SimpleRuleDTO> {
        logger.debug("Entering getSCARules for user")
        val userEmail = userData.claims["email"].toString()
        val rules = ruleRepository.findUserScaRules(userEmail)
        val result = rules.map { ruleToSimpleRuleDTO(it) }
        logger.debug("Exiting getSCARules")
        if (result.isEmpty()) {
            val commonRules = commonRuleRepository.findAll()
            return commonRules.filter { it.type == RuleType.SCA }.map { commmonRuleToSimpleRuleDTO(it) }
        }
        return result
    }

    override fun getUserFormatRules(userData: Jwt): List<RuleDTO> {
        logger.debug("Entering getUserFormatRules for user")
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
        if (result.isEmpty()) {
            val commonRules = commonRuleRepository.findAll()
            return commonRules.filter { it.type == RuleType.FORMATING }
                .map {
                    RuleDTO(
                        id = it.id,
                        name = it.name,
                        value = it.value,
                        ruleType = it.type.name,
                        isActive = it.isActive,
                        updatedAt = it.updatedAt,
                    )
                }
        }
        return result
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
                    ruleRepository.findById(ruleDTO.id).orElseGet {
                        logger.info("Creating new rule with id: ${ruleDTO.id}")
                        Rule(
                            ruleDTO.name,
                            ruleDTO.isActive,
                            RuleType.valueOf(ruleDTO.ruleType.uppercase(Locale.getDefault())),
                            ruleDTO.value,
                        ).apply {
                            id = ruleDTO.id
                            updatedAt = LocalDateTime.now()
                        }
                    }

                updateRuleProperties(ruleDTO, ruleToUpdate)
                ruleToUpdate.updatedAt = LocalDateTime.now()

                if (ruleToUpdate.type == RuleType.SCA) {
                    runSCA = true
                }
                if (ruleToUpdate.type == RuleType.FORMATING) {
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
                    ruleRepository.findById(ruleDTO.id).orElseGet {
                        logger.info("Creating new rule with id: ${ruleDTO.id}")
                        Rule(
                            ruleDTO.name,
                            ruleDTO.isActive,
                            RuleType.valueOf(ruleDTO.ruleType.uppercase(Locale.getDefault())),
                            ruleDTO.value,
                        ).apply {
                            id = ruleDTO.id
                            updatedAt = LocalDateTime.now()
                        }
                    }

                updateRuleProperties(ruleDTO, ruleToUpdate)
                ruleToUpdate.updatedAt = LocalDateTime.now()

                if (ruleToUpdate.type == RuleType.SCA) {
                    runSCA = true
                }
                if (ruleToUpdate.type == RuleType.FORMATING) {
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
            val rules = SCARulesDTO(getSCARules(userData))
            snippetManagerService.updateSnippetsSCA(rules, userData)
            logger.debug("Exiting updateStatusSCA")
        } catch (e: Exception) {
            logger.error("Error updating status SCA", e)
            throw e
        }
    }

    private fun commmonRuleToSimpleRuleDTO(rule: CommonRule): SimpleRuleDTO {
        return SimpleRuleDTO(
            name = rule.name,
            value = rule.value,
        )
    }

    private fun updateStatusFormat(userData: Jwt) {
        logger.debug("Entering updateStatusFormat for user")
        try {
            val rules = FormatRulesDTO(getFormatRules(userData))
            snippetManagerService.updateSnippetFormat(rules, userData)
            logger.debug("Exiting updateStatusFormat")
        } catch (e: Exception) {
            logger.error("Error updating status format", e)
            throw e
        }
    }
}
