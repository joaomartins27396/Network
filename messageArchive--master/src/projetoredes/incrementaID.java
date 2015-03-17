package projetoredes;

/**
 *
 * @author 27396 Joao Martins, 29035 Eduardo Simao Martins, 30178 Marlene
 * Barroso
 */
/*
 Classe responsavel por incrementar o ID de cada cliente
 */
public class incrementaID {

    public int ID;

    /**
     * Construtor sem parametros inicializado por default a zero
     */
    public incrementaID() {
        this.ID = 0;
    }

    /**
     * Retorna id do cliente
     *
     * @return
     */
    public int getID() {
        return this.ID;
    }

    /**
     * Metodo responsavel por incrementar os ID's dos clientes Cada vez que um
     * cliente se conecta o ID e incrementado 1 unidade
     */
    public void incrementa() {
        this.ID++;
    }

}
