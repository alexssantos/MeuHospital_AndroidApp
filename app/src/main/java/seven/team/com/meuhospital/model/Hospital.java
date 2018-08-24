package seven.team.com.meuhospital.model;

public class Hospital {

    private int IdHospital;
    private String Nome;
    private String Tipo;
    private double Distancia;

    public Hospital(int idHospital, String nome, String tipo, double distancia) {
        IdHospital = idHospital;
        Nome = nome;
        Tipo = tipo;
        Distancia = distancia;
    }

    public Hospital() {
    }


    public int getIdHospital() {
        return IdHospital;
    }

    public void setIdHospital(int idHospital) {
        IdHospital = idHospital;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public String getTipo() {
        return Tipo;
    }

    public void setTipo(String tipo) {
        Tipo = tipo;
    }

    public double getDistancia() {
        return Distancia;
    }

    public void setDistancia(double distancia) {
        Distancia = distancia;
    }
}
