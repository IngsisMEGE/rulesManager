package printscript.rulesManager.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import printscript.rulesManager.model.CommonRule

@EnableJpaRepositories
interface CommonRuleRepository : JpaRepository<CommonRule, Long>
