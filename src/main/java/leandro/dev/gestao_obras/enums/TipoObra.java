package leandro.dev.gestao_obras.enums;

public enum TipoObra {

    CONSTRUCAO("contrução"),
    REFORMA("Reforma"),
    AMPLIACAO("Amplicao"),
    OUTRO("Outro"); // para adiciona um tipo genérico

    private final String descricao;


    TipoObra(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao(){
        return descricao;
    }
}
