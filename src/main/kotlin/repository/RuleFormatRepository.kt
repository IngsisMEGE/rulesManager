package repository

import model.RuleFormat
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RuleFormatRepository : JpaRepository<RuleFormat, Long>
