package pe.upn.perudeestacion.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "impacto_alimento")
public class ImpactoAlimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "impacto_id", nullable = false)
    private ImpactoRegion impacto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alimento_id", nullable = false)
    private Alimento alimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "riesgo_alza", nullable = false, length = 5)
    private NivelRiesgo riesgoAlza;

    public ImpactoAlimento() {
    }

    public Long getId() {
        return id;
    }

    public ImpactoRegion getImpacto() {
        return impacto;
    }

    public void setImpacto(ImpactoRegion impacto) {
        this.impacto = impacto;
    }

    public Alimento getAlimento() {
        return alimento;
    }

    public void setAlimento(Alimento alimento) {
        this.alimento = alimento;
    }

    public NivelRiesgo getRiesgoAlza() {
        return riesgoAlza;
    }

    public void setRiesgoAlza(NivelRiesgo riesgoAlza) {
        this.riesgoAlza = riesgoAlza;
    }
}
