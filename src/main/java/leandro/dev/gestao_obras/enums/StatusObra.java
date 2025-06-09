package leandro.dev.gestao_obras.enums;

public enum StatusObra {
    EM_PLANEJAMENTO("Em Planejamento"),
    EM_ANDAMENTO("Em andamento"),
    CONCUIDA("Concluida"),
    PARALISADA("Paralisada"),
    CANCELADA("Cancelada");

    private final String descricao;

    StatusObra(String descricao){
        this.descricao = descricao;
    }
    public String getDescricao(){
        return descricao;
    }

}
