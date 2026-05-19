import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
// import java.io.InputStream;
// import java.nio.file.Files;
// import java.nio.file.Paths;

public class Stremio1 {
    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        // BufferedReader br = Files.newBufferedReader(Paths.get("ArquivosTeste/teste.csv"));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());
        int f = Integer.parseInt(st.nextToken());
        int a = Integer.parseInt(st.nextToken());

        List<Filme> filmes = new ArrayList<>();
        for (int i = 0; i < f; i++) {

            st = new StringTokenizer(br.readLine(), ",");
            int id = Integer.parseInt(st.nextToken());
            String titulo = st.nextToken();
            String[] generos = st.nextToken().split(";");
            String diretor = st.nextToken();
            int ano = Integer.parseInt(st.nextToken());

            filmes.add(new Filme(id, titulo, generos, diretor, ano));
        }

        List<Avaliacao> avaliacoes = new ArrayList<>();
        for (int i = 0; i < a; i++) {

            st = new StringTokenizer(br.readLine(), ",");
            int id_usuario = Integer.parseInt(st.nextToken());
            int id_filme = Integer.parseInt(st.nextToken());
            int nota = Integer.parseInt(st.nextToken());

            avaliacoes.add(new Avaliacao(id_usuario, id_filme, nota));
        }

        String linha = br.readLine();// lê a linha como uma string

        while (linha != null) {
            st = new StringTokenizer(linha);// quebra a string em tokens separando por espaços
            String comando = st.nextToken();

            if (comando.equals("GENERO")) {
                String tipo = st.nextToken();
                List<Filme> selecionados = buscaGenero(filmes, tipo);
                // Ordena por Gênero:
                Collections.sort(selecionados, Comparator.comparing(filme -> filme.titulo));
                // IMPRESSAO
                System.out.println(" --- GENERO " + tipo + " --- ");
                for (int i = 0; i < selecionados.size(); i++)
                    System.out.println(" - " + selecionados.get(i));

            } else {// ANO_SUPERIOR
                int ano = Integer.parseInt(st.nextToken());
                List<Filme> selecionados = buscaAno(filmes, ano);
                // Ordena por Ano:
                Collections.sort(selecionados);
                // IMPRESSAO
                System.out.println(" --- ANO_SUPERIOR " + ano + " --- ");
                for (int i = 0; i < selecionados.size(); i++)
                    System.out.println(" - " + selecionados.get(i));

            }
            linha = br.readLine();
        }
        br.close();
    }

    private static List<Filme> buscaGenero(List<Filme> filmes, String tipo) {

        List<Filme> selecionados = new ArrayList<Filme>();
        for (Filme filme : filmes) {
            for (String genero : filme.generos) {
                if (genero.equals(tipo)) {
                    selecionados.add(filme);
                }
            }
        }
        return selecionados;
    }

    private static List<Filme> buscaAno(List<Stremio1.Filme> filmes, int ano_base) {

        List<Filme> selecionados = new ArrayList<>();
        for (Filme filme : filmes) {
            if (filme.ano > ano_base) {
                selecionados.add(filme);
            }
        }
        return selecionados;
    }

    static class Filme implements Comparable<Filme> {
        int id;
        String titulo;
        String[] generos;
        String diretor;
        int ano;

        public Filme(int id, String titulo, String[] generos, String diretor, int ano) {
            this.id = id;
            this.titulo = titulo;
            this.generos = generos;
            this.diretor = diretor;
            this.ano = ano;
        }

        @Override
        public String toString() {
            return titulo;
        }

        @Override
        public int compareTo(Filme filme) {
            // Primeiro, compara por ano (decrescente: mais novo primeiro)
            int resultadoAno = Integer.compare(filme.ano, this.ano);
            if (resultadoAno != 0) {
                return resultadoAno;
            }
            // Em caso de empate no ano, compara por título (A-Z)
            return this.titulo.compareTo(filme.titulo);
        }

    }

    static class Avaliacao {
        int id_usuario;
        int id_filme;
        int nota;

        public Avaliacao(int id_usuario, int id_filme, int nota) {
            this.id_usuario = id_usuario;
            this.id_filme = id_filme;
            this.nota = nota;
        }
    }
}
