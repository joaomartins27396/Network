package projetoredes;

/**
 *
 * @author 27396 Joao Martins, 29035 Eduardo Simao Martins, 30178 Marlene
 * Barroso
 */
class ExceptionDadoNaoPermitido extends Exception {

    /**
     *Excepcao criada para o meu apresentado ao cliente
     */
    public ExceptionDadoNaoPermitido() {
        super();
    }

    /**
     *
     * @param s
     */
    public ExceptionDadoNaoPermitido(String s) {
        super(s);
    }

}
