package br.com.alura.ScreenMatch.principal;

import br.com.alura.ScreenMatch.model.DadosEpisodio;
import br.com.alura.ScreenMatch.model.DadosSerie;
import br.com.alura.ScreenMatch.model.DadosTemporada;
import br.com.alura.ScreenMatch.model.Episodio;
import br.com.alura.ScreenMatch.service.ConsumoApi;
import br.com.alura.ScreenMatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner leitura = new Scanner(System.in);

    private ConsumoApi consumo = new ConsumoApi();

    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "https://omdbapi.com/?t=";

    private final String API_KEY = "&apikey=52a85f35";

    public void exibeMenu() {

        System.out.println("Digite o nome da série para buscar");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);

        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);
        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= dados.totalTemporadas(); i++) {
            json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
/*        for (int i = 0; i < dados.totalTemporadas(); i++) {
            List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
            for (int j = 0; j < episodiosTemporada.size() ; j++) {
                System.out.println(episodiosTemporada.get(j).titulo());
            }
        }*/
        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
        //https://omdbapi.com/?t=gilmore+girls&season=1&episode=2&apikey=52a85f35"

        // LAMBDAS
        // Episodio((parametro) -> expressao);
        //STREAMS
//        List<String> nomes = Arrays.asList("jacke","Rodrigo", "Navarro","amanda", "baleia");
//        nomes.stream()
//                .sorted()
//                .limit(2)
//                .filter(n -> n.startsWith("N"))
//                .map(n -> n.toUpperCase())
//                .forEach(System.out::println);
        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())//Lista dentro de lista puxa as listas junto
                .collect(Collectors.toList());//Lista mutavel
                //.toList(); LISTA IMUTAVEL NAO DA PRA FAZER NADA
        System.out.println("/n Top 5 episodios");
        dadosEpisodios.stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                .limit(5)
                .forEach(System.out::println);

        List<Episodio> episodios = temporadas.stream()
                .flatMap(t-> t.episodios().stream()
                        .map(d-> new Episodio(t.numeroEpisodio(),d))
                ).collect(Collectors.toList());

        episodios.forEach(System.out::println);



    }
}
