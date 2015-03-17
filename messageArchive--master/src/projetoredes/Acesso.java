package projetoredes;

import java.io.File;

/**
 *
 * @author 27396 Joao Martins, 29035 Eduardo Simao Martins, 30178 Marlene
 * Barroso
 */

/*
 Classe responsável pelo tratameto das mensagens, ficheiros ou ambos que o cliente
 envia ou quer aceder
 */
public class Acesso {

    public String mensagem;
    public File ficheiro;
    public int IDCLiente;
    public String Identificador;

    /*
     Construtor sem parametros
     */
    public Acesso() {
    }

    /**
     * Representa o construtor ambos(mensagem e ficheiro)
     *
     * @param mensagem
     * @param ficheiro
     * @param IDCliente
     * @param Identificador
     */
    public Acesso(String mensagem, File ficheiro, int IDCliente, String Identificador) {
        this.mensagem = mensagem;
        this.ficheiro = ficheiro;
        this.IDCLiente = IDCliente;
        this.Identificador = Identificador;
    }

    /**
     * Representa o construtor da mensagem
     *
     * @param mensagem
     * @param IDCliente
     * @param Identificador
     */
    public Acesso(String mensagem, int IDCliente, String Identificador) {
        this.mensagem = mensagem;
        this.ficheiro = null;
        this.IDCLiente = IDCliente;
        this.Identificador = Identificador;
    }

    /**
     * Representa o construtor do ficheiro
     *
     * @param ficheiro
     * @param IDCliente
     * @param Identificador
     */
    public Acesso(File ficheiro, int IDCliente, String Identificador) {
        this.mensagem = null;
        this.ficheiro = ficheiro;
        this.IDCLiente = IDCliente;
        this.Identificador = Identificador;
    }

    /**
     * Representa um construtor default
     *
     * @param IDCliente
     * @param Identificador
     */
    public Acesso(int IDCliente, String Identificador) {
        this.mensagem = null;
        this.ficheiro = null;
        this.IDCLiente = IDCliente;
        this.Identificador = Identificador;
    }

    /**
     * Retorna a menssagem
     *
     * @return
     */
    public String getMensagem() {
        return mensagem;
    }

    /**
     * Retorna o ficheiro
     *
     * @return
     */
    public File getFicheiro() {
        return ficheiro;
    }

    /**
     * Retorna o IDcliente que e a chave primaria que identifica o acesso que o
     * cliente requisitou, o idcliente garante que mais nenhum outro cliente
     * cosegue aceder a acessos de outrem
     *
     * @return
     */
    public int getIDCLiente() {
        return IDCLiente;
    }

    /**
     * Retorna o indentificador da mensagem
     *
     * @return
     */
    public String getIdentificador() {
        return Identificador;
    }

    /**
     * Permite alterar o conteudo da mensagem
     *
     * @param mensagem
     */
    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    /**
     * Permite alterar o conteudo do ficheiro
     *
     * @param ficheiro
     */
    public void setFicheiro(File ficheiro) {
        this.ficheiro = ficheiro;
    }

    /**
     * Permite alterar o idCliente
     *
     * @param IDCLiente
     */
    public void setIDCLiente(int IDCLiente) {
        this.IDCLiente = IDCLiente;
    }

    /**
     * Permite alterar o identificador da mensagem
     *
     * @param Identificador
     */
    public void setIdentificador(String Identificador) {
        this.Identificador = Identificador;
    }

    /**
     * Retorna true caso se trate de uma mensagem e o ficheiro seja igual a null
     * Garante que se trata apenas de uma mensagem
     *
     * @return
     */
    public boolean isMensagem() {
        return this.mensagem != null && this.ficheiro == null;
    }

    /**
     * Garante que se trata exactamente de um ficheiro retornando true nesse
     * caso
     *
     * @return
     */
    public boolean isFicheiro() {
        return this.mensagem == null && this.ficheiro != null;
    }

    /**
     * Garante que o objecto acesso contem um ficheiro e uma mensagem retornando
     * true nesse caso
     *
     * @return
     */
    public boolean isAmbos() {
        return this.mensagem != null && this.ficheiro != null;
    }

    /**
     * Retorna true caso exista menssagens no arraylist acessos
     *
     * @return
     */
    public static boolean containMensagens() {
        int contador = 0;
        for (int i = 0; i < Servidor.acessos.size(); i++) {
            if (Servidor.acessos.isEmpty()) {
                return false;
            } else if (Servidor.acessos.get(i).isMensagem()) {
                contador++;
            }
        }
        return contador != 0;
    }

    /**
     * Retorna true caso exista ficheiros no arraylist acessos
     *
     * @return
     */
    public static boolean containFicheiros() {
        int contador = 0;
        for (int i = 0; i < Servidor.acessos.size(); i++) {
            if (Servidor.acessos.isEmpty()) {
                return false;
            } else if (Servidor.acessos.get(i).isFicheiro()) {
                contador++;
            }
        }
        return contador != 0;
    }

    /**
     * Retorna true caso exista menssagens e ficheiros (ambos) no arraylist
     * acessos
     *
     * @return
     */
    public static boolean containAmbos() {
        int contador = 0;
        for (int i = 0; i < Servidor.acessos.size(); i++) {
            if (Servidor.acessos.isEmpty()) {
                return false;
            } else if (Servidor.acessos.get(i).isAmbos()) {
                contador++;
            }
        }
        return contador != 0;
    }

    /**
     * Retorna o acesso pedido consoante as variaveis passadas como parametro
     * retornando null caso não encontre esse tuplo
     *
     * @param IDcliente
     * @param Identificador
     * @return
     */
    public static Acesso find(int IDcliente, String Identificador) {
        Acesso encontrado = new Acesso();
        for (int i = 0; i < Servidor.acessos.size(); i++) {
            if (Servidor.acessos.get(i).getIDCLiente() == IDcliente && Servidor.acessos.get(i).getIdentificador().equals(Identificador)) {
                encontrado = Servidor.acessos.get(i);

            }
        }

        return encontrado;
    }

    /**
     * Gera um identificado para um cliente especifico consoante a variavel
     * inteira passada como parametro "acesso", caso coisida com um dos switch
     * case retorna a string correspondente+contador
     *
     * @param cliente
     * @param acesso
     * @return
     */
    public static String geraIdentificador(Cliente cliente, int acesso) {
        String Identificador = null;
        int contador = 0;
        if (acesso == 1) {
            Identificador = "Mensagem";
        } else if (acesso == 2) {
            Identificador = "Ficheiro";
        } else if (acesso == 3) {
            Identificador = "Ambos";
        } else {

        }
        for (int i = 0; i < Servidor.acessos.size(); i++) {
            switch (acesso) {
                case 1:
                    if (Servidor.acessos.get(i).getIDCLiente() == cliente.getIDCliente() && Servidor.acessos.get(i).isMensagem()) {
                        Identificador = "Mensagem";
                        contador++;
                    }
                    break;
                case 2:
                    if (Servidor.acessos.get(i).getIDCLiente() == cliente.getIDCliente() && Servidor.acessos.get(i).isFicheiro()) {
                        Identificador = "Ficheiro";
                        contador++;
                    }
                case 3:
                    if (Servidor.acessos.get(i).getIDCLiente() == cliente.getIDCliente() && Servidor.acessos.get(i).isAmbos()) {
                        Identificador = "Ambos";
                        contador++;
                    }
                default:
                    break;
            }
        }
        Identificador = Identificador + Integer.toString(contador);
        return Identificador;
    }

}
