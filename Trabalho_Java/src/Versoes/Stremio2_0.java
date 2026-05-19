package Versoes;
import java.io.BufferedReader;
import java.io.IOException;
// import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Stremio2_0 {
    public static void main(String[] args) throws IOException {
        // BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader br = Files.newBufferedReader(Paths.get("src/teste.csv"));
        StringTokenizer st;
        st = new StringTokenizer(br.readLine());
        int f = Integer.parseInt(st.nextToken());
        int a = Integer.parseInt(st.nextToken());
        HashMap<Integer, Filme> filmes = new HashMap<>(); // a key é o id !!!
        HashMap<Integer, Set<Avaliacao>> avaliacoes = new HashMap<>(); // a key é o id_usuario !!!
        
        for (int i = 0; i < f; i++) {
            st = new StringTokenizer(br.readLine(), ",");
            int id = Integer.parseInt(st.nextToken());
            String titulo = st.nextToken();
            String[] generos = st.nextToken().split(";");
            String diretor = st.nextToken();
            int ano = Integer.parseInt(st.nextToken());
            filmes.put(id, new Filme(id, titulo, generos, diretor, ano));
        }
        
        for (int i = 0; i < a; i++) {
            st = new StringTokenizer(br.readLine(), ",");
            int id_usuario = Integer.parseInt(st.nextToken());
            int id_filme = Integer.parseInt(st.nextToken());
            int nota = Integer.parseInt(st.nextToken());
            // avaliacoes.computeIfAbsent(id_usuario, k -> new HashSet<>()).
            avaliacoes.computeIfAbsent(id_usuario, k -> new HashSet<>()).add(new Avaliacao(id_filme, nota));
        }

        String linha;
        while ((linha = br.readLine()) != null && !linha.isEmpty()) {
            st = new StringTokenizer(linha);// quebra a string em tokens separando por espaços
            String comando = st.nextToken();
            int id_usuario1 = Integer.parseInt(st.nextToken());
            
            switch (comando) {
                case "MEDIA_AVALIACOES":
                    Set<Avaliacao> selecionadas = avaliacoes.get(id_usuario1);
                    if (selecionadas == null || selecionadas.isEmpty()) {
                        System.out.printf("--- MEDIA_AVALIACOES %d ---\n0.00\n", id_usuario1);
                    } else {
                        int quant = selecionadas.size();
                        int soma = 0;
                        for (Avaliacao avaliacao : selecionadas) {
                            soma += avaliacao.nota;
                        }
                        float media = (float) soma / quant;
                        System.out.printf("--- MEDIA_AVALIACOES %d ---\n%.2f\n", id_usuario1, media);
                    }
                    break;

                case "HATER":
                    Set<Avaliacao> avaliacoesHater = avaliacoes.get(id_usuario1);
                    if (avaliacoesHater != null) {
                        int nota1 = 0, nota5 = 0;
                        for (Avaliacao avaliacao : avaliacoesHater) {
                            if (avaliacao.nota == 1) {
                                nota1++;
                            }
                            if (avaliacao.nota == 5) {
                                nota5++;
                            }
                        }
                        if (nota1 > nota5) {
                            System.out.printf("--- HATER %d ---\nSim\n", id_usuario1);
                        } else
                            System.out.printf("--- HATER %d ---\nNao\n", id_usuario1);
                    } else
                        System.out.printf("--- HATER %d ---\nNao\n", id_usuario1);
                    break;

                case "NAO_AVALIADOS":
                    Set<Avaliacao> avaliacoesID = new HashSet<>();
                    avaliacoesID = avaliacoes.get(id_usuario1);
                    System.out.printf("--- NAO_AVALIADOS %d ---\n", id_usuario1);
                    if (avaliacoesID != null) {
                        Set<Integer> filmesID = new HashSet<>();
                        for (Avaliacao avaliacao : avaliacoesID) {
                            filmesID.add(avaliacao.id_filme);
                        }
                        List<String> filmesNaoAvaliados = new ArrayList<>();
                        for (int i = 1; i < filmes.size() + 1; i++) {
                            if (!filmesID.contains(i)) {
                                filmesNaoAvaliados.add(filmes.get(i).titulo);
                            }
                        }
                        Collections.sort(filmesNaoAvaliados);
                        for (String string : filmesNaoAvaliados) {
                            System.out.println("- " + string);
                        }
                    } else {
                        String[] array = new String[filmes.size()];
                        for (int i = 0; i < filmes.size(); i++) {
                            array[i] = filmes.get(i + 1).titulo;
                        }
                        Arrays.sort(array);
                        for (String string : array) {
                            System.out.println("- " + string);
                        }
                    }
                    break;

                case "DIRETORES_COMUNS":

                    int id_usuario2 = Integer.parseInt(st.nextToken());
                    Set<Avaliacao> avaliacoesID1 = avaliacoes.get(id_usuario1);
                    Set<Avaliacao> avaliacoesID2 = avaliacoes.get(id_usuario2);
                    
                    if (avaliacoesID1 != null && avaliacoesID2 != null) {
                        // pego só o id dos filmes que ID1 e ID2 avaliaram:
                        HashSet<Integer> filmesID1 = new HashSet<>(), filmesID2 = new HashSet<>();
                        for (Avaliacao avaliacao : avaliacoesID1) {
                            filmesID1.add(avaliacao.id_filme);
                        }
                        for (Avaliacao avaliacao : avaliacoesID2) {
                            filmesID2.add(avaliacao.id_filme);
                        }
                        // filtro os filmes em comum e pego o diretor deles
                        HashSet<String> diretores = new HashSet<>();
                        for (Integer filme : filmesID1) {
                            if (filmesID2.contains(filme)) {
                                diretores.add(filmes.get(filme).diretor);
                            }
                        }
                        String[] array = diretores.toArray(new String[0]);
                        Arrays.sort(array);
                        System.out.printf("--- DIRETORES_COMUNS %d %d ---\n", id_usuario1, id_usuario2);
                        for (String string : array) {
                            System.out.println("- " + string);
                        }
                    } else
                        System.out.printf("--- DIRETORES_COMUNS %d %d ---\n", id_usuario1, id_usuario2);
                    break;
                default:
                    System.out.println("Insira um comando válido");
                    break;
            }
        }
        br.close();
    }

    static class Filme {
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
    }

    static class Avaliacao {
        int id_filme;
        int nota;

        public Avaliacao(int id_filme, int nota) {
            this.id_filme = id_filme;
            this.nota = nota;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Avaliacao avaliacao = (Avaliacao) o;
            return id_filme == avaliacao.id_filme;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id_filme, nota);
        }
    }
}
