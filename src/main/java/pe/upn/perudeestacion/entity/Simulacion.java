package pe.upn.perudeestacion.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "simulacion")
public class Simulacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "departamento_id", nullable = false)
    private Departamento departamento;

    @Column(name = "adultos", nullable = false)
    private Integer adultos;

    @Column(name = "ninos", nullable = false)
    private Integer ninos;

    @Column(name = "adultos_mayores", nullable = false)
    private Integer adultosMayores;

    @Column(name = "dias", nullable = false)
    private Integer dias;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_vivienda", nullable = false, length = 15)
    private TipoVivienda tipoVivienda;

    @Enumerated(EnumType.STRING)
    @Column(name = "capacidad_economica", nullable = false, length = 5)
    private CapacidadEconomica capacidadEconomica;

    @Column(name = "kcal_total", nullable = false)
    private Integer kcalTotal;

    @Column(name = "costo_estimado", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoEstimado;

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn;

    public Simulacion() {
    }

    public Long getId() {
        return id;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public Integer getAdultos() {
        return adultos;
    }

    public void setAdultos(Integer adultos) {
        this.adultos = adultos;
    }

    public Integer getNinos() {
        return ninos;
    }

    public void setNinos(Integer ninos) {
        this.ninos = ninos;
    }

    public Integer getAdultosMayores() {
        return adultosMayores;
    }

    public void setAdultosMayores(Integer adultosMayores) {
        this.adultosMayores = adultosMayores;
    }

    public Integer getDias() {
        return dias;
    }

    public void setDias(Integer dias) {
        this.dias = dias;
    }

    public TipoVivienda getTipoVivienda() {
        return tipoVivienda;
    }

    public void setTipoVivienda(TipoVivienda tipoVivienda) {
        this.tipoVivienda = tipoVivienda;
    }

    public CapacidadEconomica getCapacidadEconomica() {
        return capacidadEconomica;
    }

    public void setCapacidadEconomica(CapacidadEconomica capacidadEconomica) {
        this.capacidadEconomica = capacidadEconomica;
    }

    public Integer getKcalTotal() {
        return kcalTotal;
    }

    public void setKcalTotal(Integer kcalTotal) {
        this.kcalTotal = kcalTotal;
    }

    public BigDecimal getCostoEstimado() {
        return costoEstimado;
    }

    public void setCostoEstimado(BigDecimal costoEstimado) {
        this.costoEstimado = costoEstimado;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}
