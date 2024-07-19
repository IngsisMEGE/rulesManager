package printscript.rulesManager.loader

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import printscript.rulesManager.model.CommonRule
import printscript.rulesManager.model.RuleType
import printscript.rulesManager.repository.CommonRuleRepository

@Component
class RulesLoader(private val commonRuleRepository: CommonRuleRepository) : CommandLineRunner {
    override fun run(vararg args: String?) {
        if (commonRuleRepository.findAll().isEmpty()) {
            commonRuleRepository.saveAll(
                listOf(
                    CommonRule(
                        name = "DotFront",
                        type = RuleType.FORMATING,
                        value = "1",
                    ),
                    CommonRule(
                        name = "DotBack",
                        type = RuleType.FORMATING,
                        value = "1",
                    ),
                    CommonRule(
                        name = "EqualFront",
                        type = RuleType.FORMATING,
                        value = "1",
                    ),
                    CommonRule(
                        name = "EqualBack",
                        type = RuleType.FORMATING,
                        value = "1",
                    ),
                    CommonRule(
                        name = "amountOfLines",
                        type = RuleType.FORMATING,
                        value = "1",
                    ),
                    CommonRule(
                        name = "Indentation",
                        type = RuleType.FORMATING,
                        value = "4",
                    ),
                    CommonRule(
                        name = "CamelCaseFormat",
                        type = RuleType.SCA,
                        value = "true",
                    ),
                    CommonRule(
                        name = "SnakeCaseFormat",
                        type = RuleType.SCA,
                        value = "false",
                    ),
                    CommonRule(
                        name = "MethodNoExpression",
                        type = RuleType.SCA,
                        value = "false",
                    ),
                    CommonRule(
                        name = "InputNoExpression",
                        type = RuleType.SCA,
                        value = "false",
                    ),
                ),
            )
        }
    }
}
