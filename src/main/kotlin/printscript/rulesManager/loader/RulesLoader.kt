package printscript.rulesManager.loader

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import printscript.rulesManager.model.CommonRule
import printscript.rulesManager.model.RuleType
import printscript.rulesManager.repository.CommonRuleRepository

@Component
class RulesLoader(private val commonRuleRepository: CommonRuleRepository) : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(RulesLoader::class.java)

    override fun run(vararg args: String?) {
        try {
            if (commonRuleRepository.findAll().isEmpty()) {
                val rules = listOf(
                    CommonRule(
                        name = "DotFront",
                        type = RuleType.FORMATING,
                        value = "1",
                        isActive = true,
                    ),
                    CommonRule(
                        name = "DotBack",
                        type = RuleType.FORMATING,
                        value = "1",
                        isActive = true,
                    ),
                    CommonRule(
                        name = "EqualFront",
                        type = RuleType.FORMATING,
                        value = "1",
                        isActive = true,
                    ),
                    CommonRule(
                        name = "EqualBack",
                        type = RuleType.FORMATING,
                        value = "1",
                        isActive = true,
                    ),
                    CommonRule(
                        name = "amountOfLines",
                        type = RuleType.FORMATING,
                        value = "1",
                        isActive = true,
                    ),
                    CommonRule(
                        name = "Indentation",
                        type = RuleType.FORMATING,
                        value = "4",
                        isActive = true,
                    ),
                    CommonRule(
                        name = "CamelCaseFormat",
                        type = RuleType.SCA,
                        value = "true",
                        isActive = true,
                    ),
                    CommonRule(
                        name = "SnakeCaseFormat",
                        type = RuleType.SCA,
                        value = "false",
                        isActive = false,
                    ),
                    CommonRule(
                        name = "MethodNoExpression",
                        type = RuleType.SCA,
                        value = "false",
                        isActive = false,
                    ),
                    CommonRule(
                        name = "InputNoExpression",
                        type = RuleType.SCA,
                        value = "false",
                        isActive = false,
                    ),
                )
                commonRuleRepository.saveAll(rules)
                logger.info("Rules inserted successfully.")
            }
        } catch (ex: Exception) {
            logger.error("Error inserting rules: ${ex.message}", ex)
        }
    }
}
