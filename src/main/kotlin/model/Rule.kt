package model

import jakarta.persistence.*

@Entity
@Table(name = "format_rules")
data class Rule(
    @Column
    val name: String,
    @Column
    val onUse: Boolean,

    @Column
    val type: RuleType,

    @Column(nullable = true)
    val value: String
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @OneToMany(mappedBy = "rule")
    var ruleUsers: List<RuleUser> = mutableListOf()
}
