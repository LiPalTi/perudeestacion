package pe.upn.perudeestacion.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "departamento")
public class Departamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, length = 3, unique = true)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 60)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "region_natural", nullable = false, length = 10)
    private RegionNatural regionNatural;

    public Departamento() {
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public RegionNatural getRegionNatural() {
        return regionNatural;
    }

    public void setRegionNatural(RegionNatural regionNatural) {
        this.regionNatural = regionNatural;
    }
}
