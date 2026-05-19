import java.io.BufferedReader;
import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Paths;
import java.io.InputStreamReader;
import java.util.*;

public class Stremio2 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        // BufferedReader br = Files.newBufferedReader(Paths.get("src/teste.csv"));
        String primeiraLinha = br.readLine();
        if (primeiraLinha == null) {
            br.close();
            return;
        }

        StringTokenizer st = new StringTokenizer(primeiraLinha);
        int f = Integer.parseInt(st.nextToken());
        int a = Integer.parseInt(st.nextToken());

        // Armazenamos os filmes por id
        HashMap<Integer, Filme> catalogo = new HashMap<>();
        for (int i = 0; i < f; i++) {
            String[] dados = br.readLine().split(",");
            insercaoFilme(dados, catalogo);
        }

        // Armazenamos as avaliações por ID de Usuário: Map<User, List<Avaliacao>>
        HashMap<Integer, Set<Avaliacao>> avaliacoes = new HashMap<>();
        for (int i = 0; i < a; i++) {
            String[] dados = br.readLine().split(",");
            insercaoAvaliacoes(dados, avaliacoes);
        }

        String linha;
        while ((linha = br.readLine()) != null && !linha.isEmpty()) {
            StringTokenizer stCmd = new StringTokenizer(linha);
            String cmd = stCmd.nextToken();
            int id1 = Integer.parseInt(stCmd.nextToken());

            switch (cmd) {
                case "MEDIA_AVALIACOES":
                    System.out.printf("--- MEDIA_AVALIACOES %d ---\n", id1);
                    processarMedia(avaliacoes.get(id1));
                    break;
                case "HATER":
                    System.out.printf("--- HATER %d ---\n", id1);
                    processarHater(avaliacoes.get(id1));
                    break;
                case "NAO_AVALIADOS":
                    System.out.printf("--- NAO_AVALIADOS %d ---\n", id1);
                    processarNaoAvaliados(avaliacoes.get(id1), catalogo);
                    break;
                case "DIRETORES_COMUNS":
                    int id2 = Integer.parseInt(stCmd.nextToken());
                    System.out.printf("--- DIRETORES_COMUNS %d %d ---\n", id1, id2);
                    processarDiretoresComuns(avaliacoes.get(id1), avaliacoes.get(id2), catalogo);
                    break;
            }

        }
        br.close();

    }

    private static void processarMedia(Set<Avaliacao> avs) {
        if (avs == null || avs.isEmpty()) {
            System.out.println("0.00");
            return;
        }
        double soma = 0;
        for (Avaliacao a : avs)
            soma += a.nota;
        System.out.printf(Locale.US, "%.2f\n", soma / avs.size());
    }

    private static void processarHater(Set<Avaliacao> avs) {
        int nota1 = 0, nota5 = 0;
        if (avs != null) {
            for (Avaliacao a : avs) {
                if (a.nota == 1)
                    nota1++;
                else if (a.nota == 5)
                    nota5++;
            }
        }
        System.out.println(nota1 > nota5 ? "Sim" : "Nao");
    }

    private static void processarNaoAvaliados(Set<Avaliacao> avs, HashMap<Integer, Filme> catalogo) {
        Set<Integer> avaliados = new HashSet<>();
        List<String> naoAvaliados = new ArrayList<>();

        if (avs != null) {
            for (Avaliacao a : avs)
                avaliados.add(a.id_filme);
            for (Filme f : catalogo.values()) {
                if (!avaliados.contains(f.id)) {
                    naoAvaliados.add(f.titulo);
                }
            }
        } else { // se avs==null pego todos os filmes e jogo dentro de naoAvaliados
            for (Filme f : catalogo.values()) {
                naoAvaliados.add(f.titulo);
            }
        }

        // ordena alfabeticamente os filmes
        naoAvaliados.sort(Comparator.naturalOrder());
        for (String t : naoAvaliados)
            System.out.println("- " + t);
        // Collections.sort(naoAvaliados);

        // > usando array, pois é mais eficiente:
        // String[] array = naoAvaliados.toArray(new String[0]);
        // Arrays.sort(array);
        // for (String string : array) {
        // System.out.println("- " + string);
        // }

    }

    private static void processarDiretoresComuns(Set<Avaliacao> avID1, Set<Avaliacao> avID2,
            HashMap<Integer, Filme> catalogo) {
        if (avID1 == null || avID2 == null) // se pelo menos um dos ids não existir não faz nada
            return;

        Set<String> d1 = new HashSet<>();
        for (Avaliacao av : avID1) {
            d1.add(catalogo.get(av.id_filme).diretor);
        }

        Set<String> comuns = new TreeSet<>();
        for (Avaliacao av : avID2) {
            String d2 = catalogo.get(av.id_filme).diretor;
            if (d1.contains(d2))
                comuns.add(d2);
        }

        for (String d : comuns) {
            System.out.println("- " + d);
        }
    }

    private static void insercaoFilme(String[] dados, HashMap<Integer, Stremio2.Filme> catalogo) {
        int id = Integer.parseInt(dados[0].trim());
        String titulo = dados[1].trim();
        String[] generos = dados[2].split(";");
        String diretor = dados[3].trim();
        int ano = Integer.parseInt(dados[4]);
        catalogo.put(id, new Filme(id, titulo, generos, diretor, ano));
    }

    private static void insercaoAvaliacoes(String[] dados, HashMap<Integer, Set<Avaliacao>> avaliacoes) {
        int id_usuario = Integer.parseInt(dados[0].trim());
        int id_filme = Integer.parseInt(dados[1].trim());
        int nota = Integer.parseInt(dados[2].trim());
        Set<Avaliacao> avaliacoesUsuario = avaliacoes.computeIfAbsent(id_usuario, k -> new HashSet<>());
        Avaliacao novaAvaliacao = new Avaliacao(id_filme, nota);
        avaliacoesUsuario.remove(novaAvaliacao);
        avaliacoesUsuario.add(novaAvaliacao);
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
            return Objects.hash(id_filme);
        }
    }
}
