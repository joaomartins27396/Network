package envioudp;

import java.net.DatagramPacket;
import java.util.ArrayList;

/**
 *
 * @author Joao Martins 27396 & Eduardo Simao Ramos 29035 & Marlene Barroso
 * 30178
 */
public class datagramasCliente {

    private int port;
    private ArrayList<DatagramPacket> pacotes;
    private boolean terminou;

    public datagramasCliente() {

    }

    /**
     * Contrutor com parametros que recebe o numero da porta do cliente
     *
     * @param port
     */
    public datagramasCliente(int port) {
        this.port = port;
        pacotes = new ArrayList<>();
        terminou = false;
    }

    /**
     * Retorna o numero da porta do cliente
     *
     * @return
     */
    public int getPort() {
        return port;
    }

    /**
     * Retorna a lista de pacotes
     *
     * @return
     */
    public ArrayList<DatagramPacket> getPacotes() {
        return pacotes;
    }

    /**
     * Retorna a variavel boolean que indica se o envio terminou
     *
     * @return
     */
    public boolean getTerminou() {
        return terminou;
    }

    /**
     * Altera a porta de um cliente
     *
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Altera um pacote contido na lista de pacotes
     *
     * @param pacotes
     */
    public void setPacotes(ArrayList<DatagramPacket> pacotes) {
        this.pacotes = pacotes;
    }

    /**
     * Altera a variavel boolean terminou
     *
     * @param terminou
     */
    public void setTerminou(boolean terminou) {
        this.terminou = terminou;
    }

    /**
     * Adiciona pacote(datagrama) ao arraylist pacotes
     *
     * @param pacote
     */
    public void adicionarPacote(DatagramPacket pacote) {
        //caso o arraylist esteja vazio adicona o pacote
        if (pacotes.isEmpty()) {
            pacotes.add(pacote);
        } //caso o pacote a adicionar seja diferente dos presentes, adicona o pacote
        else if (compare(pacotes.get(pacotes.size() - 1).getData(), pacote.getData()) == false) {
            pacotes.add(pacote);
        }
    }

    /**
     * compara bytes informacao de dois pacotes bit a bit retornando true caso
     * sejam iguais
     *
     * @param pA
     * @param pB
     * @return
     */
    public boolean compare(byte[] pA, byte[] pB) {
        boolean a = true;
        for (int i = 0; i < pA.length; i++) {
            if (pA[i] != pB[i]) {
                return false;
            }
        }

        return a;
    }
}
