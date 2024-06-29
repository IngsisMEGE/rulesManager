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

    // override fun getLintRules(userId: String) = ruleLintingRepository.findAllByUserId(userId)

    // override fun getFormatRules(userId: String) = ruleRepository.findAllByUserId(userId)

    // override fun getSCARules(userId: String) = ruleSCARepository.findAllByUserId(userId)
}
