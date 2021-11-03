package ir.mghhrn.bank.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(schema = "public", name = "atm")
public class ATM {

    @Id
    @SequenceGenerator(name = "atmSeq", sequenceName="atm_sequence", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @Column(name = "serial_number", nullable = false)
    private String serialNumber;

    @Column(name = "location_address", nullable = false)
    private String locationAddress;
}
