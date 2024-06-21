package repository

import model.RuleLinting
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RuleLintingRepository : JpaRepository<RuleLinting, Long>
