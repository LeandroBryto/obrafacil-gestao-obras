package leandro.dev.gestao_obras.enums;

public enum StatusChecklistItem {
    PENDENTE("PENDENTE"),
    EM_ANDAMENTO("Em andamento"),
    CONCLUIDO("Concluido"),
    COM_PROBLEMAS("Com Problemas");

    private final String descricao;

    StatusChecklistItem(String descricao){
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
