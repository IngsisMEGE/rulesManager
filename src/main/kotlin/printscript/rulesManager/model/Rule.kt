package printscript.rulesManager.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "rules")
class Rule(
    @Column
    var name: String = "",
    @Column
    var isActive: Boolean = false,
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    var type: RuleType = RuleType.FORMATING,
    @Column(nullable = true)
    var value: String = "",
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @OneToMany(mappedBy = "rule")
    var ruleUsers: List<RuleUser> = emptyList()
}
