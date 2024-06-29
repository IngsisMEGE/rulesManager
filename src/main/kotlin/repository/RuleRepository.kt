package repository

import model.Rule
import model.RuleType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface RuleRepository : JpaRepository<Rule, Long>{
@Query("SELECT r FROM Rule r WHERE r.type = :type")
fun findAllLintingRules(@Param("type") type: RuleType = RuleType.LINTING): List<Rule>

@Query("SELECT r FROM Rule r WHERE r.type = :type")
fun findAllFormatingRules(@Param("type") type: RuleType = RuleType.FORMATING): List<Rule>

@Query("SELECT r FROM Rule r WHERE r.type = :type")
fun findAllScaRules(@Param("type") type: RuleType = RuleType.SCA): List<Rule>
}
