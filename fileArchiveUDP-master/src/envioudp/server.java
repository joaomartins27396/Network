package envioudp;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author Joao Martins 27396 & Eduardo Simao Ramos 29035 & Marlene Barroso
 * 30178
 */
public class server {

    //retorna diretoria onde se encontra o projecto
    static String caminho = System.getProperty("user.dir");
    //cria um datagramSocket para o servidor
    public static DatagramSocket servidor;
    //prepara caminho para a escrita do ficheiro
    private static String saida = caminho + "/";
    //ficheiro default
    private static String geral = "recebi.pdf";
    private static byte[] buffer = null;
    //array de bytes de dados recebidos pelo servidor
    private static byte[] dadosRecebidos = null;
    //array de bytes de dados a enviar pelo servidor
    private static byte[] enviardados = null;
    //Datagrama enviado pelo cliente
    public static DatagramPacket packetRecebido;
    //acknoladge que confirma se pacote foi recebido
    public static String confirmar = "ACK";
    //Arraylist do tipo objecto datagramaclientes
    public static ArrayList<datagramasCliente> bufferdatagramas = new ArrayList<datagramasCliente>();
    //objecto do tipo datagramascliente
    public static datagramasCliente C;
//    public static boolean testeFalhaACK = true;//teste de ack falhado

    public static void main(String args[]) throws Exception {

        try {
            //cria socket datagrama na porta 9876
            servidor = new DatagramSocket(9876);
            while (true) {
                //tamanho maximo de cada datagrama
                int tamanho = 51090;
                //inicializa array de bytes dos dados recebidos com o tamanho pre-definido acima
                dadosRecebidos = new byte[tamanho];
                //cria espaco para datagramas recebidos
                packetRecebido = new DatagramPacket(dadosRecebidos, dadosRecebidos.length);//cria espaco para datagramas recebidos
                //servidor encontra-se a escuta
                System.out.println("Waiting for datagram packet\n");
                //recebe datagrama
                servidor.receive(packetRecebido);
                //obtém endereço IP do emissor
                InetAddress IPAddress = packetRecebido.getAddress();
                //obtém numero do porto do emissor 
                int port = packetRecebido.getPort();
                //guarda tamanho do pacote recebido
                int tamanhopacote = packetRecebido.getLength();
                //verifica se o pacote enviado pelo cliente nao e o pacote fim
                if (!packetRecebido.getData().equals("FIM")) {
                    //verifica se o arraylist bufferdatagramas contem a porta deste cliente
                    //caso o retorno da funcao seja false e porque ainda nao existe 
                    if (containsCliente(port) == false) {
                        //cria um objecto do tipo datagramasCliente com a porta e o pacote recebido
                        C = new datagramasCliente(port);
                        C.adicionarPacote(packetRecebido);
                        //adciona o objecto ao arraylist bufferdatagramas
                        bufferdatagramas.add(C);

                    } else {
                        //senao o cliente com aquele porto ja envio anteriormente um datagrama e basta
                        //adicionar o novo datagrama (pacote) ao tuplo
                        retornaDatagramas(port).adicionarPacote(packetRecebido);

                    }
                }

                //informa de quem e a menssagem bem como o seu tamanho
                System.out.println("De: " + IPAddress + ":" + port);
                System.out.println("Com tamanho: " + tamanhopacote);
                System.out.println("Message: " + confirmar + "\n");

//                if (testeFalhaACK == true) {//teste de ACK falhado
//                  Thread.sleep(5000);
//                }
                //envia dados com ACK
                enviardados = confirmar.getBytes();
                //cria datagrama para enviar ao cliente
                DatagramPacket pacoteEnvio = new DatagramPacket(enviardados, enviardados.length, IPAddress, port);//cria datagrama para enviar ao cliente
                //envia o datagrama para o cliente
                servidor.send(pacoteEnvio);
                //se o tamanho do pacote for menor do que o tamanho predefinido
                if (tamanhopacote < tamanho) {
                    //cria ficheiro de out stream no caminho indicado pelo variavel saida
                    //com o nome do port do cliente + o nome geral
                    OutputStream out = new FileOutputStream(saida + String.valueOf(port) + geral);
                    //cria uma vaiavel auxiliar que contem os datagramas do cliente com determinada porta
                    datagramasCliente velho = retornaDatagramas(port);
                    //pega em todos os pacotes (datagramas) do cliente "velho"
                    ArrayList<DatagramPacket> datagramas = velho.getPacotes();
                    System.out.println("Quantidade de pacotes a compilar: " + datagramas.size());
                    //percorre o arraylist que contem os datagramas do cliente
                    for (int i = 0; i < datagramas.size(); i++) {
                        //para cada pacote e criado um buffer com o tamanho do pacote
                        buffer = new byte[datagramas.get(i).getLength()];
                        System.out.println("Tamanho do pacote " + i + ": " + datagramas.get(i).getLength());
                        buffer = datagramas.get(i).getData();
                        //escreve a informacao contida no dagrama no ficheiro
                        out.write(buffer, 0, datagramas.get(i).getLength());

                    }
                    System.out.println("\nFicheiro " + String.valueOf(port) + geral + " compilado\n");
                    //caso tenha terminado a compilacao do cliente em questao muda a variavel para true
                    //para que o servidor saiba que terminou
                    velho.setTerminou(true);
                    //fecha o ficheiro
                    out.close();
                }
                
                
                
//                 if (testeFalhaACK == true) {//teste de ACK falhado
//                 testeFalhaACK = false;
//                 }
            }

        } catch (SocketException erro1) {
            System.out.println("O Socket nao pode ser criado");
            System.exit(1);
        } catch (UnknownHostException erro2) {
            System.out.println("O IP nao pode ser encotrado");
            System.exit(1);
        } catch (FileNotFoundException erro3) {
            System.out.println("O arquivo nao pode ser carregado");
            System.exit(1);
        } catch (IOException erro4) {
            System.out.println("O arquivo nao pode ser lido");
            System.exit(1);
        } finally {

            servidor.close();
        }
        System.out.println("Envio Concluido");

    }

    /**
     * Verifica se o arraylist bufferdatagramas contem a porta do cliente
     * recebida como parametro
     *
     * @param port
     * @return
     */
    public static boolean containsCliente(int port) {
        if (bufferdatagramas.isEmpty()) {
            return false;
        } else {
            //percorre o arraylist bufferdatagramas posicao a posicao
            for (Iterator<datagramasCliente> it = bufferdatagramas.iterator(); it.hasNext();) {
                datagramasCliente datagrama = it.next();
                //caso a porta que pretendemos saber seja igual a presente no arraylist
                if (port == datagrama.getPort()) {
                    //e retornado true, pois ela existe
                    return true;
                }
            }

        }
        return false;
    }

    /**
     * Percorrer o arraylist bufferdatagramas e consonate a porta seja igual a
     * encontrada no arraylist retorna o datagrama. Por outras palavras retorna
     * um datagrama correspondente a um determinado cliente, caso contrario retorna null
     *
     * @param port
     * @return
     */
    public static datagramasCliente retornaDatagramas(int port) {
        //percorre o arraylist bufferdatagramas posicao a posicao
        for (Iterator<datagramasCliente> it = bufferdatagramas.iterator(); it.hasNext();) {
            datagramasCliente datagrama = it.next();
            //caso a porta que pretendemos saber seja igual a presente no arraylist
            if (port == datagrama.getPort()) {
                //retorna o datagrama correspondente
                return datagrama;
            }

        }
        return null;
    }

}
