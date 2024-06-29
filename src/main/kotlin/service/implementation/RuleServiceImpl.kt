package service.implementation

import model.Rule
import org.springframework.stereotype.Service
import repository.RuleRepository
import service.RuleService

@Service
class RuleServiceImpl(
    private val ruleRepository: RuleRepository,
) : RuleService {
    override fun getLintRules(): List<Rule> = ruleRepository.findAllLintingRules()

    override fun getFormatRules(): List<Rule> = ruleRepository.findAllFormatingRules()

    override fun getSCARules(): List<Rule> = ruleRepository.findAllScaRules()

    override fun getLintRules(userEmail: String) = ruleRepository.findUserLintingRules(userEmail)

    override fun getFormatRules(userEmail: String) = ruleRepository.findUserFormatingRules(userEmail)

    override fun getSCARules(userEmail: String) = ruleRepository.findUserScaRules(userEmail)
}
