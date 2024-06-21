package repository

import model.Rule
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RuleRepository : JpaRepository<Rule, Long>
