package repository

import model.Rule
import model.RuleType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface RuleRepository : JpaRepository<Rule, Long> {
    @Query("SELECT r FROM Rule r WHERE r.type = :type")
    fun findAllLintingRules(
        @Param("type") type: RuleType = RuleType.LINTING,
    ): List<Rule>

    @Query("SELECT r FROM Rule r WHERE r.type = :type")
    fun findAllFormatingRules(
        @Param("type") type: RuleType = RuleType.FORMATING,
    ): List<Rule>

    @Query("SELECT r FROM Rule r WHERE r.type = :type")
    fun findAllScaRules(
        @Param("type") type: RuleType = RuleType.SCA,
    ): List<Rule>

    @Query("SELECT rule FROM Rule rule JOIN rule.ruleUsers ru WHERE ru.userEmail = :userId")
    fun findAllUserRules(
        @Param("userId") userEmail: String,
    ): List<Rule>

    @Query("SELECT rule FROM Rule rule JOIN rule.ruleUsers ru WHERE ru.userEmail = :userId AND rule.type = :type")
    fun findUserLintingRules(
        @Param("userId") userEmail: String,
        @Param("type") type: RuleType = RuleType.LINTING,
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
