package Versoes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Stremio2_2 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String primeiraLinha = br.readLine();
        if (primeiraLinha == null)
            return;

        StringTokenizer st = new StringTokenizer(primeiraLinha);
        int f = Integer.parseInt(st.nextToken());
        int a = Integer.parseInt(st.nextToken());

        // Armazenamos os filmes por ID
        Map<Integer, Filme> mapaFilmes = new HashMap<>();
        for (int i = 0; i < f; i++) {
            String[] dados = br.readLine().split(",");
            int id = Integer.parseInt(dados[0].trim());
            String titulo = dados[1].trim();
            String diretor = dados[3].trim();
            mapaFilmes.put(id, new Filme(id, titulo, diretor));
        }

        // Armazenamos as avaliações por ID de Usuário: Map<User, List<Avaliacao>>
        Map<Integer, List<Avaliacao>> avaliacoesPorUsuario = new HashMap<>();
        for (int i = 0; i < a; i++) {
            String[] dados = br.readLine().split(",");
            int idUser = Integer.parseInt(dados[0].trim());
            int idFilme = Integer.parseInt(dados[1].trim());
            int nota = Integer.parseInt(dados[2].trim());

            avaliacoesPorUsuario.computeIfAbsent(idUser, k -> new ArrayList<>())
                    .add(new Avaliacao(idFilme, nota));
        }

        // Processamento de Comandos
        String linhaComando;
        while ((linhaComando = br.readLine()) != null && !linhaComando.isEmpty()) {
            System.out.println("--- " + linhaComando + " ---");
            StringTokenizer stCmd = new StringTokenizer(linhaComando);
            String cmd = stCmd.nextToken();
            int id1 = Integer.parseInt(stCmd.nextToken());

            switch (cmd) {
                case "MEDIA_AVALIACOES":
                    processarMedia(avaliacoesPorUsuario.get(id1));
                    break;
                case "HATER":
                    processarHater(avaliacoesPorUsuario.get(id1));
                    break;
                case "NAO_AVALIADOS":
                    processarNaoAvaliados(avaliacoesPorUsuario.get(id1), mapaFilmes);
                    break;
                case "DIRETORES_COMUNS":
                    int id2 = Integer.parseInt(stCmd.nextToken());
                    processarDiretoresComuns(avaliacoesPorUsuario.get(id1), avaliacoesPorUsuario.get(id2), mapaFilmes);
                    break;
            }
        }
    }

    private static void processarMedia(List<Avaliacao> avs) {
        if (avs == null || avs.isEmpty()) {
            System.out.println("0.00");
            return;
        }
        double soma = 0;
        for (Avaliacao a : avs)
            soma += a.nota;
        System.out.printf(Locale.US, "%.2f\n", soma / avs.size());
    }

    private static void processarHater(List<Avaliacao> avs) {
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

    private static void processarNaoAvaliados(List<Avaliacao> avs, Map<Integer, Filme> filmes) {
        Set<Integer> avaliados = new HashSet<>();
        if (avs != null) {
            for (Avaliacao a : avs)
                avaliados.add(a.idFilme);
        }
        List<String> naoAvaliados = new ArrayList<>();
        for (Filme f : filmes.values()) {
            if (!avaliados.contains(f.id)) {
                naoAvaliados.add(f.titulo);
            }
        }
        Collections.sort(naoAvaliados);
        for (String t : naoAvaliados)
            System.out.println("- " + t);
    }

    private static void processarDiretoresComuns(List<Avaliacao> av1, List<Avaliacao> av2, Map<Integer, Filme> filmes) {
        if (av1 == null || av2 == null)
            return;

        Set<String> dir1 = new HashSet<>();
        for (Avaliacao a : av1)
            dir1.add(filmes.get(a.idFilme).diretor);

        Set<String> comuns = new TreeSet<>(); // TreeSet já mantém ordem alfabética
        for (Avaliacao a : av2) {
            String dir = filmes.get(a.idFilme).diretor;
            if (dir1.contains(dir))
                comuns.add(dir);
        }

        for (String d : comuns)
            System.out.println("- " + d);
    }

    static class Filme {
        int id;
        String titulo, diretor;

        Filme(int id, String t, String d) {
            this.id = id;
            this.titulo = t;
            this.diretor = d;
        }
    }

    static class Avaliacao {
        int idFilme, nota;

        Avaliacao(int idF, int n) {
            this.idFilme = idF;
            this.nota = n;
        }
    }
}
