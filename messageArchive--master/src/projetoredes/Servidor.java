package projetoredes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

/**
 *
 * @author 27396 Joao Martins, 29035 Eduardo Simao Martins, 30178 Marlene
 * Barroso
 */
public class Servidor extends Thread {

    private static Vector<Cliente> CLIENTES; // vetor de clientes conectados
    public static ArrayList<Acesso> acessos;// ArrayList do tipo de dados acessos
    public static incrementaID incrementa = new incrementaID();//inicializa o construtor incrementaId por default
    private Socket cliente;// socket deste cliente
    private int porto;//porto que e usado para a conexao 
    public static BufferedReader entrada;//permite enviar dados para o cliente
    public static PrintStream saida;//permite receber dados do cliente

    /**
     * Construtor default sem parametros servidor
     */
    public Servidor() {
    }

    /**
     * Contrutor Servidor que recebe o porto como parametro permitindo
     * inicializa-lo
     *
     * @param porto
     */
    public Servidor(int porto) {
        this.porto = porto;
        this.acessos = new ArrayList<Acesso>();
        this.CLIENTES = new Vector<Cliente>();
    }

    /**
     * Contrutor que recebe um Socket (cliente) e inicializa-o
     */
    private Servidor(Socket s) {
        cliente = s;
    }

    /**
     * Retorna os cliente conectados ao servidor
     *
     * @return
     */
    public Vector getListaDeClientes() {
        return CLIENTES;
    }

    /**
     * Retorna a lista de acessos
     *
     * @return
     */
    public ArrayList<Acesso> getAcessos() {
        return acessos;
    }

    /**
     * Metodo principal por conectar o servidor O servidor conecta-se ao porto
     * 2500 e avanca de seguida para o metodo executa() Sem um servidor activo o
     * cliente nao consegue estabeler qualquer tipo de ligacao
     *
     * @param args
     */
    public static void main(String[] args) {
        new Servidor(2500).executa();
    }

    /**
     * Como o porto 2500 se encontra ocupado por este servidor mais nenhum
     * servidor consegue ser inicializado. Caso isso aconteca lanca uma exepcao
     * indicando que o endereco ja se encontra activo.
     *
     *
     */
    public void executa() {
        try {
            System.out.println("***********************************************************\n");
            System.out.println("                          Servidor                         \n");
            System.out.println("***********************************************************\n");
            //servidor(serverSocket) que permite futuras ligacoes ao mesmo atravez do porto
            ServerSocket servidor = new ServerSocket(this.porto);
            System.out.println("                    Porto " + this.porto + " aberta!                 \n");
            //inicializacao da classe incremente que atribui ids aos clientes a medida que estes se conectam
            incrementa = new incrementaID();
            //o servidor nunca desconecta e espera sempre pela ligacao/conexao de um novo cliente
            //o servidor permanecera activo mesmo que nenhum cliente se conecte
            while (true) {
                System.out.println("A esperar conexão...");
                //aceita o socket(cliente)-cliente fez uma conexao ao servidor
                Socket cliente = servidor.accept();
                //Os threads são necessários pelo facto do programa precisar de fazer,em simultâneo, 
                //várias operações de leitura
                Thread t = new Servidor(cliente);
                t.start();
                System.out.println("Total de clientes conectados: " + CLIENTES.size());
                //volta ao loop, a espera que mais alguém se conecte.
                //as threads terminam quando chegam ao fim do método run() onde arrancaram
            }
        } catch (IOException erro) {
            System.out.println("IOException: " + erro);
        }
    }

    /**
     * execucao da thread trata das requisicoes por parte do cliente ao servidor
     * Este metodo dispoe de 5 casos possiveis, consoante a escolha feita pelo
     * cliente.Quando o cliente realiza uma operacao o servidor reconhece o que
     * este esta a realizar e entra num dos cases possiveis.
     */
    @Override
    public void run() {
        try {
            //inicializacao/atrivuicao de variaveis
            Cliente c = new Cliente(cliente);//criado objeto do tipo cliente
            //permite a insercao de dados por parte do servidor para o cliente
            BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            //cria um "canal" entre o cliente e o servidor que permite ao servidor 
            //obter mensagens por parte do cliente
            PrintStream saida = new PrintStream(cliente.getOutputStream());
            incrementa.incrementa();
            c.setIDCLiente(incrementa.getID());
            saida.println(incrementa.getID());
            //adiciona novo cliente ao vector clientes
            CLIENTES.add(c);
            System.out.println("***********************************************************\n");
            System.out.println("            Nova conexão com o cliente " + c.getIDCliente() + "          \n");
            //ciclo que permite que o servidor se mantenha alerta de qualquer fluxo de dados por parte do cliente
            while (true) {
                //servidor recebe id do cliente conectado(permite ao servidor saber com quem esta a comunicar)
                String ID = entrada.readLine();
                //altera o id do cliente
                c.setIDCLiente(Integer.parseInt(ID));
                //servidor recebe a informacao do tipo de accao de operacao que o cliente quer executar
                String linha = entrada.readLine();
                switch (linha.substring(0, 1)) {

                    case "1":
                        //escrita de mensagens
                        System.out.println("***********************************************************\n");
                        //Servidor cria um acesso com a mensagem presente no envio que gera o identificador da mensagem
                        //consoante o idcliente
                        Acesso mensagem = new Acesso(linha.substring(1, linha.length()), c.getIDCliente(), Acesso.geraIdentificador(c, 1));
                        //armazena a menssagem no arraylist acessos
                        acessos.add(mensagem);
                        System.out.println("O Servidor recebeu a mensagem: <" + mensagem.getMensagem() + "> do cliente " + c.getIDCliente() + "\n");
                        //servidor envia identificador da mensagem ao cliente
                        saida.println("O Identificador da mensagem é:" + "<" + mensagem.getIdentificador() + ">");
                        System.out.println("***********************************************************\n");
                        break;
                    case "2":
                        //escrita de acessos de ficheiro
                        System.out.println("***********************************************************\n");
                        System.out.println("Cliente " + c.getIDCliente() + " a enviar ficheiro \n");
                        //servidor cria uma stream de input para o cliente
                        InputStream in = cliente.getInputStream();
                        //servidor cria uma stream input de reader
                        InputStreamReader isr = new InputStreamReader(in);
                        //servidor cria uma stream de reader para o cliente
                        BufferedReader reader = new BufferedReader(isr);
                        //servidor le o nome do ficheiro enviado pelo cliente
                        String fName = reader.readLine();
                        System.out.println(fName);
                        //servidor cria um ficheiro no path serverfiles com o mesmo nome do ficheiro que o cliente enviou
                        File f1 = new File("./serverfiles/" + fName);
                        //servidor prepara-se para copiar o conteudo do ficheiro origem para um novo ficheiro f1
                        FileOutputStream out = new FileOutputStream(f1);

                        int tamanho = 64;
                        //como solicitado no enunciado o servidor recebe faciadamente o ficheiro em blocos de 64 bytes
                        //pelo que tambem escreve de movo faseado pelo que cria um buffer de bytes com tamanho de 64.
                        byte[] buffer = new byte[tamanho];
                        int lidos;
                        //conta o numero de blocos que foram necessarios para guardar o ficheiro enviado do cliente para o servidor
                        int numeroblocos = 0;
                        //o servidor escreve de modo faseado no novo ficheiro os dados recebidos pelos cliente em blocos de 64bytes
                        while ((lidos = in.read(buffer)) > 0) {
                            numeroblocos++;
                            System.out.println("Bloco " + numeroblocos + " copiados " + lidos + "bytes");
                            System.out.println("Cliente " + cliente.getPort());
                            out.write(buffer, 0, lidos);
                            //caso ultimo bloco apresente um tamanho inferior a 64bytes sai do ciclo
                            if (lidos < tamanho) {
                                break;
                            }
                        }
                        //o servidor cria um acesso com o ficheiro
                        Acesso ficheiro = new Acesso(f1, c.getIDCliente(), Acesso.geraIdentificador(c, 2));
                        //armazena o ficheiro no Arraylist Acessos
                        acessos.add(ficheiro);
                        System.out.println("Ficheiro arquivado com sucesso do cliente " + c.getIDCliente() + "\n");
                        System.out.println("***********************************************************\n");
                        break;
                    case "3":
                        //escrita de acessos com mensagem e ficheiro
                        System.out.println("***********************************************************\n");
                        String identificador = Acesso.geraIdentificador(c, 3);
                        String mensage = linha.substring(1, linha.length());
                        saida.println("O Identificador da mensagem é:" + "<" + identificador + ">");

                        in = cliente.getInputStream();
                        isr = new InputStreamReader(in);
                        reader = new BufferedReader(isr);
                        fName = reader.readLine();

                        System.out.println(fName);

                        File ficheiro1 = new File("./serverfiles/" + fName);
                        out = new FileOutputStream(ficheiro1);

                        tamanho = 64;
                        buffer = new byte[tamanho];
                        numeroblocos = 0;
                        while ((lidos = in.read(buffer)) > 0) {
                            numeroblocos++;
                            System.out.println("Bloco " + numeroblocos + " copiados " + lidos + "bytes");
                            out.write(buffer, 0, lidos);
                            if (lidos < tamanho) {
                                break;
                            }
                        }
                        Acesso ambos = new Acesso(mensage, ficheiro1, c.getIDCliente(), identificador);
                        acessos.add(ambos);
                        System.out.println("O Servidor recebeu a mensagem:" + ambos.getMensagem() + "do cliente " + c.getIDCliente() + "\n");
                        System.out.println("Ficheiro arquivado com sucesso do cliente " + c.getIDCliente() + "\n");
                        System.out.println("***********************************************************\n");
                        break;
                    case "4":
                        System.out.println("***********************************************************\n");
                        System.out.println("A ler mensagem do cliente " + c.getIDCliente() + "\n");
                        //verifica se o arraylist de acessos contem mensagens 
                        if (!Acesso.containMensagens()) {
                            System.out.println("Acessos nao contem mensagens guardadas");
                            //servidor envia uma mensagem ao cliente NACK dando a conhecer ao cliente
                            //que nao existem mensagens com o identificador enviado pelo cliente
                            saida.println("nack");
                        } else {
                            //caso o arraylist contenha acessos com mensagens
                            //o servidor procura o acesso correspondente ao idcliente que requisitou e o identificador da mensagem
                            Acesso leitura = Acesso.find(c.getIDCliente(), linha.substring(1, linha.length()));
                            //envia a corresponde mensagem ao cliente
                            saida.println("A sua mensagem é:" + leitura.getMensagem());
                        }
                        System.out.println("***********************************************************\n");
                        break;
                    case "5":
                        //leitura de acessos com mensagens e ficheiros
                        System.out.println("***********************************************************\n");
                        System.out.println("A ler mensagem do cliente " + c.getIDCliente() + "\n");
                        if (!Acesso.containAmbos()) {
                            System.out.println("Acessos nao contem mensagens guardadas");
                            saida.println("nack");
                        } else {
                            Acesso leitura = Acesso.find(c.getIDCliente(), linha.substring(1, linha.length()));
                            //saida.println("Nao existem mensagens guardadas");                       
                            saida.println("A sua mensagem é:" + leitura.getMensagem());
                        }
                        System.out.println("***********************************************************\n");
                        break;
                    default:
                        break;
                }
            }

        } catch (IOException e) {
            //caso um cliente desconecte lanca a expecao e imprime a mensagem
            System.out.println("O Cliente desconectou");
        }
    }

}
