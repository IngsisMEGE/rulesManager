package model

import jakarta.persistence.*

@Entity
@Table(name = "user_rules")
class RuleUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(name = "user_email")
    var userEmail: String = ""

    @ManyToOne
    @JoinColumn(name = "rule_id")
    var rule: Rule? = null
}