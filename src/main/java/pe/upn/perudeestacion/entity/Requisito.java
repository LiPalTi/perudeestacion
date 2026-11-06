package pe.upn.perudeestacion.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "requisito")
public class Requisito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "programa_id", nullable = false)
    private ProgramaApoyo programa;

    @Column(name = "descripcion", nullable = false, length = 300)
    private String descripcion;

    @Column(name = "orden", nullable = false)
    private Integer orden;

    public Requisito() {
    }

    public Long getId() {
        return id;
    }

    public ProgramaApoyo getPrograma() {
        return programa;
    }

    public void setPrograma(ProgramaApoyo programa) {
        this.programa = programa;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }
}
