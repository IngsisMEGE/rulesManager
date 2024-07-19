package printscript.rulesManager.model

import jakarta.persistence.*

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

    @Column
    var value: String = "",
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}