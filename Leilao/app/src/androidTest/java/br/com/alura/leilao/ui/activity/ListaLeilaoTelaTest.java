package br.com.alura.leilao.ui.activity;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import br.com.alura.leilao.api.retrofit.client.TesteWebClient;
import br.com.alura.leilao.model.Leilao;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class ListaLeilaoTelaTest {

    @Rule
    public ActivityTestRule<ListaLeilaoActivity> activity = new ActivityTestRule(ListaLeilaoActivity.class, true, false);

    private final TesteWebClient webClient = new TesteWebClient();

    @Test
    public void deve_AparecerUmLeilao_QuandoCarregarUmLeilaoNaApi() throws IOException, InterruptedException {
        limpaBaseDeDadosDaApi();

        tentaSalvarLeilaoNaApi(new Leilao("Carro"));

        activity.launchActivity(new Intent());

        Thread.sleep(1000);

        onView(withText("Carro"))
                .check(matches(isDisplayed()));
    }

    @Test
    public void deve_AparecerDoisLeiloes_QuandoCarregarDoisLeiloesDaApi() throws IOException, InterruptedException {
        limpaBaseDeDadosDaApi();

        tentaSalvarLeilaoNaApi(new Leilao("Carro"), new Leilao("Computador"));

        activity.launchActivity(new Intent());

        Thread.sleep(1000);

        onView(withText("Carro"))
                .check(matches(isDisplayed()));

        onView(withText("Computador"))
                .check(matches(isDisplayed()));
    }

    private void limpaBaseDeDadosDaApi() throws IOException {
        boolean bancoDeDadosNaoFoiLimpo = !webClient.limpaBancoDeDados();
        if (bancoDeDadosNaoFoiLimpo) {
            Assert.fail("Banco de dados não foi limpo");
        }
    }

    private void tentaSalvarLeilaoNaApi(Leilao... leiloes) throws IOException {
        for (Leilao leilao: leiloes) {
            Leilao leilaoSalvo = webClient.salva(leilao);

            if (leilaoSalvo == null) {
                Assert.fail("Leilão não foi salvo: " + leilao.getDescricao());
            }
        }
    }
}