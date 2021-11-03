package ir.mghhrn.bank.entity;

import ir.mghhrn.bank.enums.AuthenticationMode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(schema = "public", name = "card")
public class Card {

    @Id
    @SequenceGenerator(name = "cardSeq", sequenceName="card_sequence", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "authentication_mode", nullable = false)
    private AuthenticationMode authenticationMode;

    @Column(name = "card_number", nullable = false)
    private String cardNumber;

    @Column(name = "pin_code", nullable = false)
    private String pinCode;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Column(name = "is_blocked", nullable = false)
    private Boolean blocked = false;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", insertable = false, updatable = false, nullable = false)
    private Account account;
}
