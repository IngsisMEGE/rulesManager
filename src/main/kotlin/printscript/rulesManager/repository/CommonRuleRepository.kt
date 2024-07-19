package printscript.rulesManager.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import printscript.rulesManager.model.CommonRule

@Repository
interface CommonRuleRepository : JpaRepository<CommonRule, Long>
