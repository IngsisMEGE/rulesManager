package printscript.rulesManager.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "common_rules")
class CommonRule(
    @Column
    var name: String = "",
    @Column
    var isActive: Boolean = true,
    @Enumerated(EnumType.STRING)
    @Column
    var type: RuleType = RuleType.FORMATING,
    @Column(name = "rule_value")
    var ruleValue: String = "",
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, unique = true)
    var id: Long = 0
}
