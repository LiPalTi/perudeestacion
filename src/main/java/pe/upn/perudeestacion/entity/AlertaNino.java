package pe.upn.perudeestacion.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "alerta_nino")
public class AlertaNino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comunicado", nullable = false, length = 40)
    private String comunicado;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "estado", nullable = false, length = 40)
    private String estado;

    @Column(name = "magnitud", nullable = false, length = 40)
    private String magnitud;

    @Column(name = "vigente", nullable = false)
    private Boolean vigente;

    public AlertaNino() {
    }

    public Long getId() {
        return id;
    }

    public String getComunicado() {
        return comunicado;
    }

    public void setComunicado(String comunicado) {
        this.comunicado = comunicado;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMagnitud() {
        return magnitud;
    }

    public void setMagnitud(String magnitud) {
        this.magnitud = magnitud;
    }

    public Boolean getVigente() {
        return vigente;
    }

    public void setVigente(Boolean vigente) {
        this.vigente = vigente;
    }
}
