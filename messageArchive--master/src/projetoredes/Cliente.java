package projetoredes;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 27396 Joao Martins, 29035 Eduardo Simao Martins, 30178 Marlene
 * Barroso
 */
public class Cliente extends Thread {

    public static Scanner teclado = new Scanner(System.in);
    public static Socket cliente; //cliente
    public static int IDCliente; //id especifico do cliente conectado
    public static boolean isConnected; //indica se o cliente se encontra ou nao conectado
    public static BufferedReader entrada; // cria stream de entrada
    public static PrintStream saida; //cria stream de saida
    public static BufferedInputStream BufferedInputStream; //cria stream de entrada para ficheiros
    public static BufferedOutputStream BufferedOutputStream; //cria strea, de saida para ficheiros
    public static FileInputStream FileInputStream;

    /**
     *
     * @param cliente
     */
    public Cliente(Socket cliente) {
        this.cliente = cliente;
    }

    /**
     *
     * @param socket
     */
    public static void setSocket(Socket socket) {
        cliente = socket;
    }

    /**
     *
     * @return
     */
    public int getIDCliente() {
        return this.IDCliente;
    }

    /**
     *
     * @param IDCliente
     */
    public void setIDCLiente(int IDCliente) {
        this.IDCliente = IDCliente;
    }

    /**
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        //caso nao exista nenhum servidor conectado o cliente nao consegue estabelecer conecxao
        //sendo lancada a excepcao
        try {
            //cria um novo socket que contem o ip e a porta que da inicio a 
            //comunicacao entre as duas partes cliente-servidor
            cliente = new Socket("127.0.0.1", 2500);
            //permite a insercao de dados por parte do cliente para o servidor
            entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            //cria um "canal" entre o cliente e o servidor que permite ao cliente 
            //obter mensagens por parte do servidor
            saida = new PrintStream(cliente.getOutputStream());
            teclado = new Scanner(System.in);
            //permite mais tarde fazer o upload de um arquivo que o cliente envia para o servidor
            BufferedOutputStream = new BufferedOutputStream(cliente.getOutputStream());
            //Os threads são necessários pelo facto do programa precisar de fazer,em simultâneo, 
            //várias operações de leitura
            Thread t = new Cliente(cliente);
            t.start();
            //as threads terminam quando chegam ao fim do método run() onde arrancaram
        } catch (IOException e) {
            System.out.println("Nao existe nenhum servidor connectado");
        }
    }

    public static void executa() throws IOException, InterruptedException {
        String linha;
        String opcao = menu();
        switch (opcao) {
            //escrita de mensagens
            case "1":
                System.out.println("***********************************************************\n");
                System.out.println("Para voltar ao menu anterior, pressione ENTER\n");
                do {
                    System.out.println("Escreva a mensagem que pretende guardar");
                    System.out.print(">");
                    //cliente espera pelo input do utilizador
                    linha = teclado.nextLine();
                    if (linha.trim().equals("")) {
                        break;
                    }
                    //envia o id do cliente ao servidor
                    saida.println(IDCliente);
                    //envia um string ao servidor em que o primeiro char e a opcao escolhida e o resto e a mensagem
                    saida.println("1" + linha);
                    //cliente recebe mensagem do servidor com o confirmacao de que 
                    //a mensagem foi recebida bem como o id da mensagem anteriormente enviada
                    linha = entrada.readLine();
                    System.out.println(linha);
                    System.out.println("***********************************************************\n");
                } while (!linha.trim().equals(""));
                break;
            case "2":
                System.out.println("***********************************************************\n");
                System.out.println("Para voltar ao menu anterior, pressione ENTER\n");
                System.out.print("File:");
                //cliente espera que o utilizador insira o nome do ficheiro que quer enviar ao servidor
                linha = teclado.nextLine();
                //caso a linha seja null significa que nao pretende enviar nenhum ficheiro
                if (linha.trim().equals("")) {
                    break;
                }
                //path onde os ficheiros dos clientes se encontram "clientfiles"
                File f = new File("./clientfiles/" + linha);
                FileInputStream in = null;
                //verifica se o ficheiro existe na directoria, caso nao exista
                // lanca a excepcao e termina a requisicao feita anteriormente
                try {
                    in = new FileInputStream(f);
                } catch (FileNotFoundException e) {
                    System.out.println("O ficheiro nao existe na directoria");
                    break;
                }
                //cliente envia ao servidor o seu id, para que este reconheca qual o cliente que se trata
                saida.println(IDCliente);
                //cliente envia ao servidor o char correspondente a opcao correspondente para que este
                //possa entra no case correto
                saida.println("2");
                long fileLength = f.length();
                //e imprimido o tamanho do ficheiro que o cliente se prepara para enviar 
                //so por conveniencia de calculo, para verificar se tudo esta correcto
                System.out.println("Tamanho do ficheiro enviado: " + fileLength + "kb");
                //cliente cria uma stream de output para que o cliente possa enviar o file ao servidor
                OutputStream out = cliente.getOutputStream();
                //cliente cria uma stream writer do outputstream
                OutputStreamWriter osw = new OutputStreamWriter(out);
                //cria um stream writer
                BufferedWriter writer = new BufferedWriter(osw);
                //o cliente escreve no escritor o nome do file seguido de um /n
                //para isolar o nome do ficheiro e nao estendar a primeira string do conteudo do mesmo
                writer.write(f.getName() + "\n");
                writer.flush();

                System.out.println(f.getName());
                int tamanho = 64;
                //como solicitado no enunciado o servidor recebe faciadamente o ficheiro em blocos de 64 bytes
                //pelo que tambem escreve de movo faseado pelo que cria um buffer de bytes com tamanho de 64.
                byte[] buffer = new byte[tamanho];

                int lidos = -1;
                //o cliente escreve no buffer ate nao restarem mais blocos para enviar
                while ((lidos = in.read(buffer, 0, tamanho)) != -1) {
                    out.write(buffer, 0, lidos);
                  
                }
                //significa que a stream de dados enviada para o servidor esta completa
                //pelo que o ficheiro foi enviado com sucesso
                if (lidos == -1) {
                    System.out.println("Ficheiro enviado com sucesso");
                }
                out.flush();

                break;

            case "3":
                //escrita de acessos com mensagens e ficheiros
                System.out.println("Escreva a mensagem que pretende guardar");
                System.out.print(">");
                linha = teclado.nextLine();
                if (linha.trim().equals("")) {
                    break;
                }
                saida.println(IDCliente);
                saida.println("3" + linha);
                linha = entrada.readLine();
                System.out.println(linha);
                System.out.print("File:");
                linha = teclado.nextLine();
                if (linha.trim().equals("")) {
                    break;
                }
                File ficheiro = new File("./clientfiles/" + linha);

                in = null;
                try {
                    in = new FileInputStream(ficheiro);
                } catch (FileNotFoundException e) {
                    System.out.println("O ficheiro nao existe na directoria");
                    break;
                }
                fileLength = ficheiro.length();
                System.out.println("Tamanho do ficheiro enviado: " + fileLength + "kb");

                out = cliente.getOutputStream();
                osw = new OutputStreamWriter(out);
                writer = new BufferedWriter(osw);

                writer.write(ficheiro.getName() + "\n");
                writer.flush();
                System.out.println(ficheiro.getName());
                tamanho = 64; //buffer de 64 kb
                buffer = new byte[tamanho];
                lidos = -1;
                while ((lidos = in.read(buffer, 0, tamanho)) != -1) {
                    out.write(buffer, 0, lidos);
                    
                }
                if (lidos == -1) {
                    System.out.println("Ficheiro enviado com sucesso");
                }
                out.flush();
                break;
            case "4":
                System.out.println("***********************************************************\n");
                System.out.println("Para voltar ao menu anterior, pressione ENTER\n");
                do {
                    System.out.println("Escreva o Identificador da mensagem que quer aceder");
                    System.out.print(">");
                    //o cliente espera pelo input do utilizador
                    linha = teclado.nextLine();
                    if (linha.trim().equals("")) {
                        break;
                    }
                    //envia o id do cliente ao servidor
                    saida.println(IDCliente);
                    //envia a char que corresponde a opcao concatenada com a linha que corresponde ao indentificador da mensagem
                    saida.println("4" + linha);
                    linha = entrada.readLine();
                    if (linha.equals("A sua mensagem é:null") || linha.equals("nack")) {
                        //caso a mensagem que chega ao cliente seja null ou nack significa que o
                        //servidor nao encontrou nenhuma mensagem com o identificador enviado anteriormenete
                        System.out.println("Nao existe mensagem com este identificador");
                    } else {
                        //cliente imprime mensagem que requesitou
                        System.out.println(linha);
                    }
                    System.out.println("***********************************************************\n");
                } while (!linha.trim().equals(""));
                break;
            case "5":
                //leitura de acessos com mensagens e ficheiros
                System.out.println("***********************************************************\n");
                System.out.println("Para voltar ao menu anterior, pressione ENTER\n");
                do {
                    System.out.println("Escreva o Identificador da mensagem guardada com o ficheiro");
                    System.out.print(">");
                    linha = teclado.nextLine();
                    if (linha.trim().equals("")) {
                        break;
                    }
                    saida.println(IDCliente);
                    saida.println("5" + linha);
                    linha = entrada.readLine();
                    if (linha.equals("A sua mensagem é:null") || linha.equals("nack")) {
                        System.out.println("Nao existe mensagem com este identificador");
                    } else {
                        System.out.println(linha);
                    }
                    System.out.println("***********************************************************\n");

                } while (!linha.trim().equals(""));
                break;
            case "6":
                //cliente desconecta
                System.exit(0);
            default:
                break;
        }

    }

    @Override
    public void run() {
        try {
            String linha;

            System.out.println("***********************************************************\n");
            System.out.println("            Bem vindo ao arquivo de mensagens!             \n");
            System.out.println("***********************************************************\n");
            //le a mensagem enviada pelo servidor
            linha = entrada.readLine();
            System.out.println("                       Cliente " + linha + "                \n");
            //altera o id do cliente consoante o valor da linha
            setIDCLiente(Integer.parseInt(linha));
            while (true) {
                executa();
            }
        } catch (IOException e) {

            System.out.println("IOException: " + e);
        } catch (InterruptedException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
     *Metodo responsavel por tratar as decisoes que o cliente toma retornando a respectiva String(1-6)
     */
    public static String menu() {
        String opcao;
        boolean dados_correctos;
        System.out.println("***********************************************************\n");
        System.out.println("Escolha uma das seguintes opções:");
        System.out.println("1 - Guardar Mensagem");
        System.out.println("2 - Guardar Ficheiro");
        System.out.println("3 - Guardar Mensagem com Ficheiro");
        System.out.println("4 - Ler Mensagem");
        System.out.println("5 - Ler mensagem guardada junto com um Ficheiro");
        System.out.println("6 - Disconectar\n");
        System.out.println("***********************************************************\n");

        do {
            System.out.print("Opcao:");
            teclado = new Scanner(System.in);
            opcao = teclado.nextLine();
            dados_correctos = verifica_dados(opcao, 6);
            System.out.println("\n");
        } while (dados_correctos == false);

        return opcao;
    }

    /**
     * Retorna true caso a opcao introduzida pelo utilizador se encontre dentro
     * dos parametros especificados no menu() caso contrario retorna false
     *
     * @param opcao
     * @param maxima
     * @return
     */
    public static boolean verifica_dados(String opcao, int maxima) {
        if (isInt(opcao) == false) {
            return false;
        } else {
            try {
                int aux_opcao = Integer.parseInt(opcao);

                if (aux_opcao < 1 || aux_opcao > maxima) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verifica se a String passada como parametro e um inteiro retornando true
     * caso se trate de um inteiro e false, caso não se trate de um inteiro
     *
     * @param str
     * @return
     */
    public static boolean isInt(String str) {
        boolean isInteger = true;
        int size = str.length();
        for (int i = 0; (i < size) && isInteger; i++) {
            isInteger = Character.isDigit(str.charAt(i));
        }
        return isInteger;
    }

    /*
     **************Fim metodos verfica entrada de dados no menu****************************
     */
}
