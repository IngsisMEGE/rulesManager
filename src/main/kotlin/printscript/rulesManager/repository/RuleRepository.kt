package printscript.rulesManager.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import printscript.rulesManager.model.Rule
import printscript.rulesManager.model.RuleType

@Repository
interface RuleRepository : JpaRepository<Rule, Long> {
    @Query("SELECT rule FROM Rule rule JOIN rule.ruleUsers ru WHERE ru.userEmail = :userId ")
    fun findAllUserRules(
        @Param("userId") userEmail: String,
    ): List<Rule>

    @Query("SELECT rule FROM Rule rule JOIN rule.ruleUsers ru WHERE ru.userEmail = :userId AND rule.type = :type")
    fun findUserFormatingRules(
        @Param("userId") userEmail: String,
        @Param("type") type: RuleType = RuleType.FORMATING,
    ): List<Rule>

    @Query("SELECT rule FROM Rule rule JOIN rule.ruleUsers ru WHERE ru.userEmail = :userId AND rule.type = :type")
    fun findUserScaRules(
        @Param("userId") userEmail: String,
        @Param("type") type: RuleType = RuleType.SCA,
    ): List<Rule>
}
