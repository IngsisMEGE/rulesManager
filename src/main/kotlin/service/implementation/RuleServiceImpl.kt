package service.implementation

import model.RuleFormat
import model.RuleLinting
import model.RuleSCA
import org.springframework.stereotype.Service
import repository.RuleFormatRepository
import repository.RuleLintingRepository
import repository.RuleSCARepository
import service.RuleService

@Service
class RuleServiceImpl(
    private val ruleFormatRepository: RuleFormatRepository,
    private val ruleLintingRepository: RuleLintingRepository,
    private val ruleSCARepository: RuleSCARepository,
) : RuleService {
    override fun getLintRules(): List<RuleLinting> = ruleLintingRepository.findAll()

    override fun getFormatRules(): List<RuleFormat> = ruleFormatRepository.findAll()

    override fun getSCARules(): List<RuleSCA> = ruleSCARepository.findAll()

    // override fun getLintRules(userId: String) = ruleLintingRepository.findAllByUserId(userId)

    // override fun getFormatRules(userId: String) = ruleFormatRepository.findAllByUserId(userId)

    // override fun getSCARules(userId: String) = ruleSCARepository.findAllByUserId(userId)
}
