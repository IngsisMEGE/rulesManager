package repository

import model.RuleSCA
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RuleSCARepository : JpaRepository<RuleSCA, Long>
