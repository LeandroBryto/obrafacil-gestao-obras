package leandro.dev.gestao_obras.enums;

public enum StatusEtapa {
    NAO_INICIADA("Não iniciada"),
    EM_ANDAMENTO("Em andamento"),
    CONCLUIDO("Concluido"),
    ATRASADA("ATRASADA"),
    COM_PROBLEMAS("Com problemas");

    private final String descricao;

    StatusEtapa(String descricao){
        this.descricao = descricao;
    }
    public String getDescricao(){
        return descricao;
    }
}
