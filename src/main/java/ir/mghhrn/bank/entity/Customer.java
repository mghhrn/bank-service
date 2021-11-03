package ir.mghhrn.bank.entity;

import ir.mghhrn.bank.enums.IdentificationType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(schema = "public", name = "customer")
public class Customer {

    @Id
    @SequenceGenerator(name = "customerSeq", sequenceName="customer_sequence", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "identification_number", nullable = false)
    private String identificationNumber;

    @Column(name = "identification_type", nullable = false)
    private IdentificationType identificationType;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "finger_print_in_base64", nullable = false)
    private String fingerPrintInBase64;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
