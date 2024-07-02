package model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "rules")
data class Rule(
    @Column
    var name: String,
    @Column
    var onUse: Boolean,
    @Column
    var type: RuleType,
    @Column(nullable = true)
    var value: String,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @OneToMany(mappedBy = "rule")
    var ruleUsers: List<RuleUser> = mutableListOf()
}
