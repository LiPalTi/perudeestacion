package pe.upn.perudeestacion.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "impacto_region")
public class ImpactoRegion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alerta_id", nullable = false)
    private AlertaNino alerta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "departamento_id", nullable = false)
    private Departamento departamento;

    @Column(name = "sector", nullable = false, length = 60)
    private String sector;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_riesgo", nullable = false, length = 5)
    private NivelRiesgo nivelRiesgo;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @OneToMany(mappedBy = "impacto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImpactoAlimento> alimentos = new ArrayList<>();

    public ImpactoRegion() {
    }

    public Long getId() {
        return id;
    }

    public AlertaNino getAlerta() {
        return alerta;
    }

    public void setAlerta(AlertaNino alerta) {
        this.alerta = alerta;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public NivelRiesgo getNivelRiesgo() {
        return nivelRiesgo;
    }

    public void setNivelRiesgo(NivelRiesgo nivelRiesgo) {
        this.nivelRiesgo = nivelRiesgo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<ImpactoAlimento> getAlimentos() {
        return alimentos;
    }

    public void setAlimentos(List<ImpactoAlimento> alimentos) {
        this.alimentos = alimentos;
    }
}
