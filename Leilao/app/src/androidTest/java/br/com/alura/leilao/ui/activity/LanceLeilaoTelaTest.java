package br.com.alura.leilao.ui.activity;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import br.com.alura.leilao.BaseTesteIntegracao;
import br.com.alura.leilao.R;
import br.com.alura.leilao.database.dao.UsuarioDAO;
import br.com.alura.leilao.formatter.FormatadorDeMoeda;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Usuario;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

public class LanceLeilaoTelaTest extends BaseTesteIntegracao {

    @Rule
    public ActivityTestRule<ListaLeilaoActivity> mainActivity = new ActivityTestRule(ListaLeilaoActivity.class, true, false);

    @Before
    public void setup() throws IOException {
        limpaBancoDeDadosDaApi();
        limpaBancoDeDadosInterno();
    }

    @Test
    public void deve_AtualizarLanceDoLeilao_QuandoReceberUmLance() throws IOException {
        // Salvar leilão na API
        tentaSalvarLeilaoNaApi(new Leilao("Carro"));

        // Inicializar a main activity
        mainActivity.launchActivity(new Intent());

        // Clica no leilão
        onView(withId(R.id.lista_leilao_recyclerview))
                .perform(actionOnItemAtPosition(0, click()));

        // Clica no fab da tela de lances do leilão
        onView(allOf(withId(R.id.lances_leilao_fab_adiciona),
                isDisplayed()))
                .perform(click());

        // Verificar se aparece dialog de aviso por não ter usuário com título e mensagem esperada
        onView(allOf(withText("Usuários não encontrados"),
                withId(R.id.alertTitle)))
                .check(matches(isDisplayed()));

        onView(allOf(withText("Não existe usuários cadastrados! Cadastre um usuário para propor o lance."),
                withId(android.R.id.message)))
                .check(matches(isDisplayed()));

        // Clica no botão "Cadastrar usuário"
        onView(allOf(withText("Cadastrar usuário"), isDisplayed()))
                .perform(click());

        // Clica no fab tela de lista de usuários
        onView(allOf(withId(R.id.lista_usuario_fab_adiciona),
                isDisplayed()))
                .perform(click());

        // Clica no EditText e preenche com o nome do usuário
        onView(allOf(withId(R.id.form_usuario_nome_edittext),
                isDisplayed()))
                .perform(click(),
                        typeText("Robson"),
                        closeSoftKeyboard());

        // Clica em Adicionar
        onView(allOf(withId(android.R.id.button1),
                withText("Adicionar"),
                isDisplayed()))
                .perform(scrollTo(), click());

        // Clica no botão back do Android
        pressBack();

        // Verifica visibilidade do dialog com o título esperado
        propoeNovoLance("200", 1, "Robson");

        // Fazer assertion para as views de maior e menor lance, e também, para os tres lances
        FormatadorDeMoeda formatador = new FormatadorDeMoeda();

        onView(withId(R.id.lances_leilao_maior_lance))
                .check(matches(allOf(withText(formatador.formata(200)),
                        isDisplayed())));

        onView(withId(R.id.lances_leilao_menor_lance))
                .check(matches(allOf(withText(formatador.formata(200)),
                        isDisplayed())));

        onView(withId(R.id.lances_leilao_maiores_lances))
                .check(matches(allOf(withText(formatador.formata(200) + " - (1) Robson\n"),
                        isDisplayed())));
    }

    @Test
    public void deve_AtualizarLancesDoLeilao_QuandoReceberTresLances() throws IOException {
        // Salvar leilão na API
        tentaSalvarLeilaoNaApi(new Leilao("Carro"));

        // Cria usuários manualmente
        tentaSalvarUsuariosNoBancoDeDados(new Usuario("Robson"),
                new Usuario("Fran"));

        // Inicializar a main activity
        mainActivity.launchActivity(new Intent());

        // Clica no leilão
        onView(withId(R.id.lista_leilao_recyclerview))
                .perform(actionOnItemAtPosition(0, click()));

        propoeNovoLance("200", 1, "Robson");
        propoeNovoLance("300", 2, "Fran");
        propoeNovoLance("400", 1, "Robson");

        // Fazer assertion para as views de maior e menor lance, e também, para os tres lances
        FormatadorDeMoeda formatador = new FormatadorDeMoeda();

        onView(withId(R.id.lances_leilao_maior_lance))
                .check(matches(allOf(withText(formatador.formata(400)),
                        isDisplayed())));

        onView(withId(R.id.lances_leilao_menor_lance))
                .check(matches(allOf(withText(formatador.formata(200)),
                        isDisplayed())));

        onView(withId(R.id.lances_leilao_maiores_lances))
                .check(matches(allOf(withText(formatador.formata(400) + " - (1) Robson\n" +
                                formatador.formata(300) + " - (2) Fran\n" +
                                formatador.formata(200) + " - (1) Robson\n"),
                        isDisplayed())));
    }

    @Test
    public void deve_atualizarLancesDoLeilao_QuandoReceberUmLanceMuitoAlto() throws IOException {
        // Salvar leilão na API
        tentaSalvarLeilaoNaApi(new Leilao("Carro"));

        // Cria usuários manualmente
        tentaSalvarUsuariosNoBancoDeDados(new Usuario("Robson"));

        // Inicializar a main activity
        mainActivity.launchActivity(new Intent());

        // Clica no leilão
        onView(withId(R.id.lista_leilao_recyclerview))
                .perform(actionOnItemAtPosition(0, click()));

        propoeNovoLance("2000000000", 1, "Robson");

        // Fazer assertion para as views de maior e menor lance, e também, para os tres lances
        FormatadorDeMoeda formatador = new FormatadorDeMoeda();

        onView(withId(R.id.lances_leilao_maior_lance))
                .check(matches(allOf(withText(formatador.formata(2000000000)),
                        isDisplayed())));

        onView(withId(R.id.lances_leilao_menor_lance))
                .check(matches(allOf(withText(formatador.formata(2000000000)),
                        isDisplayed())));

        onView(withId(R.id.lances_leilao_maiores_lances))
                .check(matches(allOf(withText(formatador.formata(2000000000) + " - (1) Robson\n"),
                        isDisplayed())));
    }

    private void propoeNovoLance(String valorLance, int idUsuario, String nomeUsuario) {
        // Clica no fab lances do leilão
        onView(allOf(withId(R.id.lances_leilao_fab_adiciona),
                isDisplayed()))
                .perform(click());

        // Verifica visibilidade do dialog com o título esperado
        onView(allOf(withText("Novo lance"),
                withId(R.id.alertTitle)))
                .check(matches(isDisplayed()));

        // Clica no EditText de valor e preenche
        onView(allOf(withId(R.id.form_lance_valor_edittext),
                isDisplayed()))
                .perform(click(),
                        typeText(valorLance),
                        closeSoftKeyboard());

        // Seleciona o usuário
        onView(allOf(withId(R.id.form_lance_usuario),
                isDisplayed()))
                .perform(click());

        onData(is(new Usuario(idUsuario, nomeUsuario)))
                .inRoot(isPlatformPopup())
                .perform(click());

        // Clica no botão "Propor"
        onView(allOf(withText("Propor"),
                isDisplayed()))
                .perform(click());
    }

    private void tentaSalvarUsuariosNoBancoDeDados(Usuario... usuarios) {
        UsuarioDAO dao = new UsuarioDAO(InstrumentationRegistry.getTargetContext());

        for (Usuario usuario: usuarios) {
            Usuario usuariosSalvo = dao.salva(usuario);

            if (usuariosSalvo == null){
                Assert.fail("Usuário " + usuario + " não foi salvo");
            }
        }
    }

    @After
    public void tearDown() throws IOException {
        limpaBancoDeDadosDaApi();
        limpaBancoDeDadosInterno();
    }
}
