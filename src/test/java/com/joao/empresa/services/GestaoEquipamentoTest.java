package com.joao.empresa.services;

import com.joao.empresa.builders.EquipamentoBuilder;
import com.joao.empresa.exceptions.*;
import com.joao.empresa.model.Equipamento;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class GestaoEquipamentoTest {

    private GestaoEquipamento gestaoEquipamento;
    private Equipamento equipamentoNovo;

    @BeforeEach
    public void antesDeCadaMetodoInstanciaOObjeto(){
        gestaoEquipamento = new GestaoEquipamento();
        equipamentoNovo = EquipamentoBuilder.builder().comId(1).comCodigoPatrimonio("12345678").build();
    }

    @Test
    public void quandoMetodoBuscarPorIdForChamadoEExistirEquipamentoComIdBuscadoDeveRetornarEsseEquipamento(){

        gestaoEquipamento.cadastrarEquipamento(equipamentoNovo);

        Equipamento equipamento = gestaoEquipamento.buscarPorId(1);

        assertEquals(1, equipamento.getId());
    }

    @Test
    public void quandoMetodoBuscarPorIdForChamadoENaoExistirEquipamentoComOIdBuscadoDeveLancarExcecao(){
        assertThrows(EquipamentoNaoEncontradoException.class, () -> gestaoEquipamento.buscarPorId(2));
    }

    @Test
    public void quandoOMetodoCadastrarEquipamentoForChamadoSeJaExistirUmEquipamentoComOMesmoIdDeveLancarExcecao(){

        Equipamento equipamentoNovo2 = EquipamentoBuilder.builder().
                comId(1).comNome("Rolamento da empilhadeira").
                comCodigoPatrimonio("7272721278").
                comDataAquisicao(LocalDate.of(2013, 05, 02)).
                build();

        gestaoEquipamento.cadastrarEquipamento(equipamentoNovo2);

        assertThrows(EquipamentoJaCadastradoException.class, () -> gestaoEquipamento.cadastrarEquipamento(equipamentoNovo2));
    }

    @Test
    public void quandochamarCadastrarEquipamentoSeJaExistirUmEquipamentoComOMesmoCodigoPatrimonioDeveLancarExcecao(){

        Equipamento equipamentoNovo2 = EquipamentoBuilder.builder().
                comId(2).comNome("Rolamento da empilhadeira").
                comCodigoPatrimonio("12345678").
                comDataAquisicao(LocalDate.of(2013, 05, 02)).
                build();

        gestaoEquipamento.cadastrarEquipamento(equipamentoNovo);
        gestaoEquipamento.cadastrarEquipamento(equipamentoNovo2);

        assertThrows(EquipamentoJaCadastradoException.class, () -> gestaoEquipamento.cadastrarEquipamento(equipamentoNovo2));
    }

    @Test
    public void quandoCadastrarEquipamentoForChamadoSemConflitosDeIdECodigoDeveAdicionarEquipamentoAoSistema(){

        gestaoEquipamento.cadastrarEquipamento(equipamentoNovo);

        assertTrue(gestaoEquipamento.listarEquipamentos().contains(equipamentoNovo));
    }

    @Test
    public void quandoAtualizarEquipamentoForChamadoDeveAtualizarOsDadosDaEquipamentoCadastrada(){

        gestaoEquipamento.cadastrarEquipamento(equipamentoNovo);
        Equipamento equipamentoAlterado = EquipamentoBuilder.builder().
                comId(1).comNome("Rolamento da empilhadeira").
                comCodigoPatrimonio("12345678").
                comDataAquisicao(LocalDate.of(2013, 05, 02)).
                build();

        gestaoEquipamento.atualizarEquipamento(equipamentoAlterado);

        assertAll(
                () -> assertEquals(equipamentoAlterado.getNome(), equipamentoNovo.getNome()),
                () -> assertEquals(equipamentoAlterado.getCodigoPatrimonio(), equipamentoAlterado.getCodigoPatrimonio()),
                () -> assertEquals(equipamentoAlterado.getDataAquisicao(), equipamentoNovo.getDataAquisicao())
        );
    }

    @Nested // agrupar os testes em uma hierarquia de classes alinhadas
    @ExtendWith(MockitoExtension.class)
    class GestaoEquipamentoComMockTest{

        @Mock
        GestaoManutencao gestaoManutencao; // aqui eu quero mockar, ou seja fingir que existe

        @InjectMocks // onde eu quero colocar o mock
        GestaoEquipamento gestaoEquipamento; // injeção de dependência (Manutencao dentro do construtor de Equipamento)

        @Test
        public void quandoExcluirEquipamentoENaoTiverManutencaoAssociadaDeveRemoverDoSistema() {

            gestaoEquipamento.cadastrarEquipamento(equipamentoNovo);

            when(gestaoManutencao.existeManutencaoDoEquipamento(1))
                    .thenReturn(false); // finge que deu certo e retorna falso (eu decido a realidade do método da outra classa)
            // quando eu chamar a função ela deve retornar falso

            gestaoEquipamento.excluirEquipamento(1);

            assertFalse(gestaoEquipamento.listarEquipamentos().contains(equipamentoNovo));
        }

        @Test
        public void quandoExcluirEquipamentoETiverManutencaoAssociadaDeveLancarExcecao(){

            gestaoEquipamento.cadastrarEquipamento(equipamentoNovo);

            when(gestaoManutencao.existeManutencaoDoEquipamento(1)).
                    thenReturn(true);

            assertThrows(EquipamentoNaManutencaoException.class, () -> gestaoEquipamento.excluirEquipamento(1));

        }

    }

}
