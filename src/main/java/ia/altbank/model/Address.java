package ia.altbank.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class Address {
    @Column(nullable = false, length = 150)
    private String street;
    @Column(length = 50)
    private String number;
    @Column(length = 50)
    private String complement;
    @Column(nullable = false, length = 50)
    private String neighborhood;
    @Column(nullable = false, length = 50)
    private String city;
    @Column(nullable = false, length = 30)
    private String state;
    @Column(nullable = false, length = 10)
    private String zipCode;
}
