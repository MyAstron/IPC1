package cris.sic.refugio.modelo;

// Modelo para representar un animal en el refugio
public class Animal {
    private String codigo;
    private String nombre;
    private String especie;
    private int edad;
    private String estadoClinico;
    private String estadoAdopcion;

    public Animal() {}

    public Animal(String codigo, String nombre, String especie, int edad, String estadoClinico, String estadoAdopcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.especie = especie;
        this.edad = edad;
        this.estadoClinico = estadoClinico;
        this.estadoAdopcion = estadoAdopcion;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public String getEstadoClinico() { return estadoClinico; }
    public void setEstadoClinico(String estadoClinico) { this.estadoClinico = estadoClinico; }

    public String getEstadoAdopcion() { return estadoAdopcion; }
    public void setEstadoAdopcion(String estadoAdopcion) { this.estadoAdopcion = estadoAdopcion; }
}
