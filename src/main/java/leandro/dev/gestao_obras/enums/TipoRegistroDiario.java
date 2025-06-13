package leandro.dev.gestao_obras.enums;

public enum TipoRegistroDiario {
    OCORRENCIA("Ocorrência"),
    OBSERVACAO("Observação"),
    VISITA_TERNICA("Visita Técnica"),
    ENTREGA_MATERIAL("Entega Material"),
    INICIO_ATIVIDADE("Inicio Atividade"),
    FIM_ATIVIDADE("Fim de Atividade"),
    OUTRO("outro");

    private final String descricao;

    TipoRegistroDiario(String descricao){
        this.descricao = descricao;
    }

    public String getDescricao(){
        return descricao;
    }
}
