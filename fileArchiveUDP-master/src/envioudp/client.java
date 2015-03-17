package envioudp;

import java.io.*;
import java.net.*;

/**
 *
 * @author Joao Martins 27396 & Eduardo Simao Ramos 29035 & Marlene Barroso
 * 30178
 */
public class client {
    //diretoria onde se encontra o projecto
    static String caminho = System.getProperty("user.dir");
    private static File envio;
    //Datagrama enviado pelo servidor
    public static DatagramPacket receivePacket;
    //cria um datagramSocket para o cliente
    public static DatagramSocket clientSocket;
    //array de bytes de dados a enviar pelo cliente
    private static byte[] enviardados = null;

    public static void main(String args[]) throws Exception {

        String serverHostname = new String("127.0.0.1");

        try {

            if (args.length > 0) {
                serverHostname = args[0];
            }
            //cria stream de entrada
            BufferedReader inFromUser
                    = new BufferedReader(new InputStreamReader(System.in));
            // cria socket cliente
            clientSocket = new DatagramSocket();
            //translada nome do host para endereço IP usando DNS
            InetAddress IPAddress = InetAddress.getByName(serverHostname);
            System.out.println("Attemping to connect to " + IPAddress
                    + ") via UDP port 9876");

            //imprime menssagem indincando o path onde se encontra o ficheiro
            System.out.println("Path " + caminho);
            //cliente indica o nome do ficheiro 
            System.out.print("Name of file: ");
            String caminho_file = inFromUser.readLine();
            envio = new File(caminho + "/" + caminho_file);
            //tamanho do ficheiro transferido pelo cliente
            int tamanhoficheiro = (int) envio.length();

            InputStream ler = new FileInputStream(envio);

            //tamanho maximo de cada datagrama
            int tamanho = 51090;//51089
            //cria um array de bytes com o tamanho maximo default
            byte[] bloco = new byte[tamanho];
            //numero de pacotes enviados
            int enviados = 0;
            int nr_blocos = -1;
            //calculo do numero de blocos
            if (tamanhoficheiro % tamanho != 0) {
                nr_blocos = (tamanhoficheiro / tamanho) + 1;
            } else {
                nr_blocos = (tamanhoficheiro / tamanho);
            }
            String resposta;
            DatagramPacket pacoteEnvio;
            int tentativas = 0;
            //inicio do envio do ficheiro
            long startTime = System.nanoTime();
            //repete enquanto o numero de pacotes enviados for diferente do numero de blocos
            do {
                //Thread.sleep(10000);
                //caso o numero de pacotes enviados seja igual ao numero de blocos -1
                //caso em que se trata do ultimo bloco
                if (enviados == nr_blocos - 1) {
                    //tamanho dos blocos enviados ao servidor
                    int tamanho_aux = nr_blocos * tamanho;
                    //tamanho final sera igual ao tamanho definido por default para cada datagrama 
                    // menos (o tamanho dos blocos enviados ao servidor-o tamanho original do ficheiro)
                    int tamanho_final = tamanho - (tamanho_aux - tamanhoficheiro);
                    //cria um array de dados que tratra o ultimo bloco
                    byte[] bloco_final = new byte[tamanho_final];
                    //le os dados do ficheiro
                    ler.read(bloco_final, 0, tamanho_final);
                    //cria um pacote com o bloco_final
                    pacoteEnvio = new DatagramPacket(bloco_final, tamanho_final, IPAddress, 9876);
                    //envia o pacote ao servidor
                    clientSocket.send(pacoteEnvio);
                    System.out.println("Pacote " + enviados + " enviado");
                    receivePacket = new DatagramPacket(new byte[4], 4);
                    try {
                        clientSocket.setSoTimeout(5000);
                        clientSocket.receive(receivePacket);
                        resposta = new String(receivePacket.getData());
                        System.out.println(resposta + " ao pacote " + enviados);
                    } catch (SocketTimeoutException timeout) {
                        System.out.println("*** Packet Timed Out ***");
                        System.out.println("*** Reenviar packet ***");
                        gereTimeOut(pacoteEnvio, tentativas);
                    }
                    //caso o tamanho_final seja igual ao tamanho predefinido
                    //e necessario cria um novo datagrama para invalidar erros
                    if (tamanho_final == tamanho) {
                        enviardados = ("FIM").getBytes();
                        pacoteEnvio = new DatagramPacket(enviardados, enviardados.length, IPAddress, 9876);
                        //envia o datagrama com a palavra FIM para o servidor
                        //para que este saiba que se trata do ultimo pacote
                        clientSocket.send(pacoteEnvio);
                        System.out.println("Enviado pacote FIM");
                        try {
                            //o cliente aguarda pela resposta do servidor durante o timeout estabelecido
                            clientSocket.setSoTimeout(5000);
                            clientSocket.receive(receivePacket);
                            //caso nao receba o pacote neste tempo e lacncada a excepcao 
                        } catch (SocketTimeoutException timeout) {
                            System.out.println("*** Packet Timed Out ***");
                            System.out.println("*** Reenviar packet ***");
                            //metodo que gere o reenvio do pacote quando ocorre uma falha
                            gereTimeOut(pacoteEnvio, tentativas);
                        }
                    }
                    //aumenta o numero de pacotes enviados
                    enviados++;
                } else {
                    //le dados contidos no ficheiro
                    ler.read(bloco, 0, tamanho);
                    //cria pacote para enviar ao servidor com o bloco lido
                    pacoteEnvio = new DatagramPacket(bloco, tamanho, IPAddress, 9876);
                    //envia o pacote ao servidor
                    clientSocket.send(pacoteEnvio);
                    System.out.println("Pacote " + enviados + " enviado");
                    //cria um novo datagrama para receber novos dados
                    receivePacket = new DatagramPacket(new byte[4], 4);
                    try {
                        //o cliente aguarda pela resposta do servidor durante o timeout estabelecido
                        clientSocket.setSoTimeout(5000);
                        clientSocket.receive(receivePacket);
                        //guarda informacao recebida na string resposta
                        resposta = new String(receivePacket.getData());
                        System.out.println(resposta + " ao pacote " + enviados);
                        //caso nao receba o pacote neste tempo e lacncada a excepcao 
                    } catch (SocketTimeoutException timeout) {
                        System.out.println("*** Packet Timed Out ***");
                        System.out.println("*** Reenviar packet ***");
                        //metodo que gere o reenvio do pacote quando ocorre uma falha
                        gereTimeOut(pacoteEnvio, tentativas);
                    }
                    //aumenta o numero de pacotes enviados
                    enviados++;

                }
            } while (enviados != nr_blocos);
            long stopTime = System.nanoTime();
            //fim do envio do ficheiro
            System.out.println("Tempo que demorou em ns: " + (stopTime - startTime));
            System.out.println("A enviar " + tamanhoficheiro + " bytes to server");

        } catch (SocketException erro1) {
            System.out.println("\n***** O Socket nao pode ser criado *****\n");
        } catch (UnknownHostException erro2) {
            System.out.println("\n****** O IP nao pode ser encontrado ******\n");
        } catch (FileNotFoundException erro3) {
            System.out.println("\n****** O arquivo nao pode ser carregado ******\n");
        } catch (IOException erro4) {
            System.out.println("\n ****** O arquivo nao pode ser lido ******\n");
        } finally {
            clientSocket.close();
        }

    }

    /**
     * Metodo responsavel por gerir o envio de pacotes caso o pacote nao seja
     * recebido pelo cliente durante o clientSocket.setSoTimeout(5000)
     * estabelecido
     *
     * @param pacote_a_enviar
     * @param tentativas
     * @throws IOException
     */
    public static void gereTimeOut(DatagramPacket pacote_a_enviar, int tentativas) throws IOException {
        //caso o numero de tenativas que o cliente fez e igual a 3,entao nao volta a tentar mais
        //o envio do ficheiro falha e o cliente desconecta forcadamente
        if (tentativas == 3) {
            System.out.println("O cliente não conseguiu enviar o pacote");
            System.out.println("Desconexão forçada");
            clientSocket.close();
        } else {
            //volta a enviar o pacote do qual nao obteve qualquer resposta por parte do servidor
            clientSocket.send(pacote_a_enviar);
            System.out.println("Pacote enviado");
            //cria um novo datagrama para receber novos dados
            receivePacket = new DatagramPacket(new byte[4], 4);
            try {
                //inicializa um novo Timeout para o pacote enviado
                clientSocket.setSoTimeout(5000);
                //se nao recebido dentro do tempo estabelecido parte para o catch da excepcao
                clientSocket.receive(receivePacket);

            } catch (SocketTimeoutException timeout) {
                System.out.println("*** Packet Timed Out ***");
                System.out.println("*** Reenviar packet ***");
                //chama o proprio metodo recursivamente, com o mesmo pacote, mas com o numero de tentativas efectuadas + 1
                gereTimeOut(pacote_a_enviar, tentativas + 1);
            }
        }

    }
}
