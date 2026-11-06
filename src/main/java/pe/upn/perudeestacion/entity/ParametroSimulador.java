package pe.upn.perudeestacion.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "parametro_simulador")
public class ParametroSimulador {

    @Id
    @Column(name = "clave", length = 40)
    private String clave;

    @Column(name = "valor", nullable = false, precision = 12, scale = 4)
    private BigDecimal valor;

    @Column(name = "descripcion", length = 200)
    private String descripcion;

    public ParametroSimulador() {
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
