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
        logger.info("Entering getUserRules for user")
        val userEmail = userData.claims["email"].toString()
        val rules = ruleRepository.findAllUserRules(userEmail)
        logger.info("Fetched ${rules.size} rules for user")
        val result = rules.map { ruleToSimpleRuleDTO(it) }
        logger.info("Exiting getUserRules")
        if (result.isEmpty()) {
            return commonRuleRepository.findAll().map { commmonRuleToSimpleRuleDTO(it) }
        }
        return result
    }

    override fun getFormatRules(userData: Jwt): List<SimpleRuleDTO> {
        logger.info("Entering getFormatRules for user")
        val userEmail = userData.claims["email"].toString()
        val rules = ruleRepository.findUserFormatingRules(userEmail)
        val result = rules.map { ruleToSimpleRuleDTO(it) }
        logger.info("Exiting getFormatRules")
        if (result.isEmpty()) {
            val commonRules = commonRuleRepository.findAll()
            return commonRules.filter { it.type == RuleType.FORMATING }.map { commmonRuleToSimpleRuleDTO(it) }
        }
        return result
    }

    override fun getSCARules(userData: Jwt): List<SimpleRuleDTO> {
        logger.info("Entering getSCARules for user")
        val userEmail = userData.claims["email"].toString()
        val rules = ruleRepository.findUserScaRules(userEmail)
        val result = rules.map { ruleToSimpleRuleDTO(it) }
        logger.info("Exiting getSCARules")
        if (result.isEmpty()) {
            val commonRules = commonRuleRepository.findAll()
            return commonRules.filter { it.type == RuleType.SCA }.map { commmonRuleToSimpleRuleDTO(it) }
        }
        return result
    }

    override fun getUserSCARules(userData: Jwt): List<RuleDTO> {
        return try {
            logger.info("Entering getUserSCARules for user")
            val userEmail = userData.claims["email"].toString()
            val userSpecificRules = ruleRepository.findUserScaRules(userEmail)

            if (userSpecificRules.isEmpty()) {
                commonRuleRepository.findAll()
                    .filter { it.type == RuleType.SCA }
                    .map { commonRuleToRuleDTO(it) }
            } else {
                userSpecificRules.map { ruleToRuleDTO(it) }
            }.also {
                logger.info("Exiting getUserSCARules with result size: ${it.size}")
            }
        } catch (e: Exception) {
            logger.error("Error in getUserSCARules", e)
            throw e
        }
    }

    override fun getUserFormatRules(userData: Jwt): List<RuleDTO> {
        return try {
            logger.info("Entering getUserFormatRules for user")
            val userEmail = userData.claims["email"].toString()
            val userSpecificRules = ruleRepository.findUserFormatingRules(userEmail)

            if (userSpecificRules.isEmpty()) {
                commonRuleRepository.findAll()
                    .filter { it.type == RuleType.FORMATING }
                    .map { commonRuleToRuleDTO(it) }
            } else {
                userSpecificRules.map { ruleToRuleDTO(it) }
            }.also {
                logger.info("Exiting getUserFormatRules with result size: ${it.size}")
            }
        } catch (e: Exception) {
            logger.error("Error in getUserFormatRules", e)
            throw e
        }
    }

    override fun updateRule(
        userData: Jwt,
        rules: List<RuleDTO>,
    ): List<SimpleRuleDTO> {
        logger.info("Entering updateRule for user with rules")
        try {
            val updatedRules =
                editRules(userData, rules) { ruleDTO, ruleToUpdate ->
                    val ruleType = RuleType.valueOf(ruleDTO.ruleType.uppercase(Locale.getDefault()))
                    ruleToUpdate.name = ruleDTO.name
                    ruleToUpdate.content = ruleDTO.value
                    ruleToUpdate.type = ruleType
                }
            logger.info("Exiting updateRule")
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
        logger.info("Entering updateRuleOnUse for user with rules")
        try {
            val updatedRules =
                editRules(userData, rules) { ruleDTO, ruleToUpdate ->
                    ruleToUpdate.isActive = ruleDTO.isActive
                }
            logger.info("Exiting updateRuleOnUse")
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
        logger.info("Entering updateRules for user with rules")
        try {
            val updatedRules: List<RuleDTO> =
                editRulesWithRuleDTO(userData, rules) { ruleDTO, ruleToUpdate ->
                    val ruleType = RuleType.valueOf(ruleDTO.ruleType.uppercase(Locale.getDefault()))
                    ruleToUpdate.name = ruleDTO.name
                    ruleToUpdate.content = ruleDTO.value
                    ruleToUpdate.type = ruleType
                }
            logger.info("Exiting updateRules")
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
        logger.info("Entering editRules for user with rules")
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

        logger.info("Exiting editRules")
        return updatedRules
    }

    private fun editRulesWithRuleDTO(
        userData: Jwt,
        rules: List<RuleDTO>,
        updateRuleProperties: (RuleDTO, Rule) -> Unit,
    ): List<RuleDTO> {
        logger.info("Entering editRulesWithRuleDTO for user with rules")
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
                    value = updatedRule.content,
                    ruleType = updatedRule.type.name,
                    isActive = updatedRule.isActive,
                    updatedAt = updatedRule.updatedAt,
                )
            }

        if (runSCA) updateStatusSCA(userData)
        if (runFormat) updateStatusFormat(userData)

        logger.info("Exiting editRulesWithRuleDTO")
        return updatedRules
    }

    private fun ruleToSimpleRuleDTO(rule: Rule): SimpleRuleDTO {
        return SimpleRuleDTO(
            name = rule.name,
            value = rule.content,
        )
    }

    private fun updateStatusSCA(userData: Jwt) {
        logger.info("Entering updateStatusSCA for user")
        try {
            val rules = SCARulesDTO(getSCARules(userData))
            snippetManagerService.updateSnippetsSCA(rules, userData)
            logger.info("Exiting updateStatusSCA")
        } catch (e: Exception) {
            logger.error("Error updating status SCA", e)
            throw e
        }
    }

    private fun commmonRuleToSimpleRuleDTO(rule: CommonRule): SimpleRuleDTO {
        return SimpleRuleDTO(
            name = rule.name,
            value = rule.ruleValue,
        )
    }

    private fun ruleToRuleDTO(rule: Rule): RuleDTO =
        RuleDTO(
            id = rule.id,
            name = rule.name,
            value = rule.content,
            ruleType = rule.type.name,
            isActive = rule.isActive,
            updatedAt = rule.updatedAt,
        )

    private fun commonRuleToRuleDTO(commonRule: CommonRule): RuleDTO =
        RuleDTO(
            id = commonRule.id,
            name = commonRule.name,
            value = commonRule.ruleValue,
            ruleType = commonRule.type.name,
            isActive = commonRule.isActive,
            updatedAt = commonRule.updatedAt,
        )

    private fun updateStatusFormat(userData: Jwt) {
        logger.info("Entering updateStatusFormat for user")
        try {
            val rules = FormatRulesDTO(getFormatRules(userData))
            snippetManagerService.updateSnippetFormat(rules, userData)
            logger.info("Exiting updateStatusFormat")
        } catch (e: Exception) {
            logger.error("Error updating status format", e)
            throw e
        }
    }
}
