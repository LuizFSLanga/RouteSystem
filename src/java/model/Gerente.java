package model;

public class Gerente extends Pessoa {
    private String email;

    public Gerente() {
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    @Override
    public String toString() {
        return "Gerente [id=" + id + ", nome=" + nome + ", telefone=" + telefone + ", email=" + email + "]";
    }
}