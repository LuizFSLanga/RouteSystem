package model;

public class Motorista extends Pessoa{
    private String documento;
    private String veiculo;
    private int rendimentoKmLitro;

    public Motorista() {
    }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public String getVeiculo() { return veiculo; }
    public void setVeiculo(String veiculo) { this.veiculo = veiculo; }

    public int getRendimentoKmLitro() { return rendimentoKmLitro;}
    public void setRendimentoKmLitro(int rendimentoKmLitro) { this.rendimentoKmLitro = rendimentoKmLitro;}
    
    @Override
    public String toString() {
        return "Motorista [id=" + id + ", nome=" + nome + ", telefone=" + telefone + ", rendimentoKmLitro=" + rendimentoKmLitro + ", veiculo=" + veiculo + ", rendimentoKmLitro=" + rendimentoKmLitro + "]";
    }
}