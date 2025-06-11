package leandro.dev.gestao_obras.enums;

public enum StatusCronograma {

    NO_PRAZO("No prazo"),
    ADIANTADA("Adiantada"),
    ATRASADA("Atrasada");

    private final String descricao;

    StatusCronograma(String descricao){
        this.descricao = descricao;
    }

    public String getDescricao(){
        return descricao;
    }
}
