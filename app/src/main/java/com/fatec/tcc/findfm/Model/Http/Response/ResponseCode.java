package com.fatec.tcc.findfm.Model.Http.Response;

/**
 * Representa códigos de resposta do servidor para serem usados no ResponseBody
 * **/
public enum ResponseCode {

    /**
     * Reposta não especificada, default
     * **/
    Unspecified(0),

    /**
     * Recurso não foi encontrado
     * **/
    NotFound(1),

    /**
     * Senha incorreta
     * **/
    IncorrectPassword(2),

    /**
     * Código de sucesso genérico, para operações sem muito sentido para ter seu próprio código aqui.
     * **/
    GenericSuccess(3),

    /**
     * Código de erro genérico, para operações sem muito sentido para ter seu próprio código aqui.
     * **/
    GenericFailure(4),

    /**
     * O recurso que tentou criar já existe.
     * **/
    AlreadyExists(5),

    /**
     * O e-mail não foi confirmado pelo usuário
     * **/
    UnconfirmedEmail(6),

    /**
     * O usuário não está autenticado, a operação exige que esteja
     * **/
    RequiresAuthentication(7),

    /**
     * A validação dos dados que o usuário enviou falhou
     * **/
    ValidationFailure(8),

    /**
     * O usuário está autenticado, a operação exige que não esteja
     * **/
    RequiresUnauthentication(9),

    /**
     * Uma conta premium é necessária para essa operação
     * **/
    RequiresPremium(10);

    private int codigo;
    
    ResponseCode(int codigo) {
        this.codigo = codigo;
    }

    public static final ResponseCode from( final int codigo) {
        for ( final ResponseCode item : ResponseCode.values() ) {
            if ( item.getCodigo() == codigo ) {
                return item;
            }
        }
        return ResponseCode.Unspecified;
    }

    public int getCodigo() {
        return codigo;
    }
}
