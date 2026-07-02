import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

public class TestePreAtividade {

    private static final int N = 10_000;
    private static final int M = 1_000;
    private static final int LIMITE_VALORES = N * 10;

    private static class Resultado {
        long comparacoes;
        double tempo;
    }

    public static void main(String[] args) {
        List<Integer> valores = gerarValoresUnicos(N);
        List<Integer> buscas = gerarBuscas(M);
        List<Integer> valoresOrdenados = new ArrayList<>(valores);
        Collections.sort(valoresOrdenados);

        Resultado abbAleatoria = executarTeste(new ABB<>(), valores, buscas);
        Resultado avlAleatoria = executarTeste(new AVL<>(), valores, buscas);
        Resultado abbOrdenada = executarTeste(new ABB<>(), valoresOrdenados, buscas);
        Resultado avlOrdenada = executarTeste(new AVL<>(), valoresOrdenados, buscas);

        System.out.printf("%-25s %20s %20s%n", "Cenario", "Comparacoes", "Tempo total(ns)");
        imprimirResultado("ABB/insercao aleatoria", abbAleatoria);
        imprimirResultado("AVL/insercao aleatoria", avlAleatoria);
        imprimirResultado("ABB/insercao ordenada", abbOrdenada);
        imprimirResultado("AVL/insercao ordenada", avlOrdenada);

        System.out.println();
        System.out.println("1. ABB e AVL tendem a ficar parecidas na insercao aleatoria, pois a ABB costuma ficar menos degenerada.");
        System.out.println("2. A diferenca fica mais evidente na insercao ordenada: a ABB vira quase uma lista encadeada.");
        System.out.println("3. Como produtos.txt nao garante ordem, usar AVL por ID evita pior caso e mantem buscas mais estaveis.");
    }

    private static List<Integer> gerarValoresUnicos(int quantidade) {
        Random random = new Random(42);
        List<Integer> valores = new ArrayList<>();

        while (valores.size() < quantidade) {
            int valor = random.nextInt(LIMITE_VALORES);
            if (!valores.contains(valor))
                valores.add(valor);
        }

        return valores;
    }

    private static List<Integer> gerarBuscas(int quantidade) {
        Random random = new Random(42);
        List<Integer> buscas = new ArrayList<>();

        for (int i = 0; i < quantidade; i++)
            buscas.add(random.nextInt(LIMITE_VALORES));

        return buscas;
    }

    private static Resultado executarTeste(ABB<Integer, Integer> arvore, List<Integer> valores, List<Integer> buscas) {
        Resultado resultado = new Resultado();

        for (Integer valor : valores)
            arvore.inserir(valor, valor);

        for (Integer busca : buscas) {
            try {
                arvore.pesquisar(busca);
            } catch (NoSuchElementException excecao) {
                // Buscas por valores ausentes tambem fazem parte do experimento.
            }
            resultado.comparacoes += arvore.getComparacoes();
            resultado.tempo += arvore.getTempo();
        }

        return resultado;
    }

    private static void imprimirResultado(String cenario, Resultado resultado) {
        System.out.printf("%-25s %,20d %,20.0f%n", cenario, resultado.comparacoes, resultado.tempo);
    }
}
