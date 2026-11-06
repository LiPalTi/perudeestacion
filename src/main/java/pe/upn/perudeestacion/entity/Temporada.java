package pe.upn.perudeestacion.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "temporada", uniqueConstraints = @UniqueConstraint(
        name = "uq_temporada", columnNames = {"alimento_id", "departamento_id", "mes"}))
// Named Query (JPQL con parametros nombrados). Spring Data la enlaza con el metodo
// TemporadaRepository.buscarPorDepartamentoYMes por convencion de nombre "Entidad.metodo".
@NamedQuery(name = "Temporada.buscarPorDepartamentoYMes", query = """
        SELECT t FROM Temporada t
        JOIN FETCH t.alimento a
        JOIN FETCH t.departamento d
        WHERE UPPER(d.codigo) = UPPER(:codigo) AND t.mes = :mes""")
public class Temporada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alimento_id", nullable = false)
    private Alimento alimento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "departamento_id", nullable = false)
    private Departamento departamento;

    @Column(name = "mes", nullable = false)
    private Integer mes;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel", nullable = false, length = 5)
    private NivelTemporada nivel;

    public Temporada() {
    }

    public Long getId() {
        return id;
    }

    public Alimento getAlimento() {
        return alimento;
    }

    public void setAlimento(Alimento alimento) {
        this.alimento = alimento;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public NivelTemporada getNivel() {
        return nivel;
    }

    public void setNivel(NivelTemporada nivel) {
        this.nivel = nivel;
    }
}
