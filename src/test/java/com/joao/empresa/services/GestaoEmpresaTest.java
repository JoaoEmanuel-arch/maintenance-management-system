package com.joao.empresa.services;

import com.joao.empresa.builders.EmpresaBuilder;
import com.joao.empresa.exceptions.EmpresaJaCadastradaException;
import com.joao.empresa.exceptions.EmpresaNaoEncontradaException;
import com.joao.empresa.model.Empresa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GestaoEmpresaTest {

    private GestaoEmpresa gestaoEmpresa;
    private Empresa empresaNova;

    @BeforeEach
    public void antesDeCadaMetodoInstanciaOObjeto(){
        gestaoEmpresa = new GestaoEmpresa();
        empresaNova = EmpresaBuilder.builder().comId(1).build();
    }

    @Test
    public void quandoMetodoBuscarPorIdForChamadoEExistirEmpresaComIdBuscadoDeveRetornarEssaEmpresa(){

        gestaoEmpresa.cadastrarEmpresa(empresaNova);

        Empresa empresa = gestaoEmpresa.buscarPorId(1); // basta testar com um para ver se o método funcionar "TESTE UNITÁRIO"

        assertEquals(1, empresa.getId());
    }

    @Test
    public void quandoMetodoBuscarPorIdForChamadoENaoExistirEmpresaComOIdBuscadoDeveLancarExcecao(){
        assertThrows(EmpresaNaoEncontradaException.class, () -> gestaoEmpresa.buscarPorId(2));
    }

    @Test
    public void quandoOMetodoCadastrarEmpresaForChamadoSeJaExistirUmaEmpresaComOMesmoIdDeveLancarExcecao(){

        Empresa empresaNova2 = EmpresaBuilder.builder().
                comId(1).comNome("Vale do Rio Doce").
                comEndereco("Congonhas").
                comSeguimento("Produtora de Chapas").
                build();

        gestaoEmpresa.cadastrarEmpresa(empresaNova);

        assertThrows(EmpresaJaCadastradaException.class, () -> gestaoEmpresa.cadastrarEmpresa(empresaNova2));
    }

    @Test
    public void quandoOMetodoCadastrarEmpresaForChamadoSeJaExistirUmaEmpresaComOMesmoCnpjDeveLancarExcecao(){

        Empresa empresaNova2 = EmpresaBuilder.builder().
                comId(1).comNome("Vale do Rio Doce").
                comCpnj("999929714").
                comEndereco("Congonhas").
                comSeguimento("Produtora de Chapas").
                build();

        gestaoEmpresa.cadastrarEmpresa(empresaNova);

        assertThrows(EmpresaJaCadastradaException.class, () -> gestaoEmpresa.cadastrarEmpresa(empresaNova2));
    }

    @Test
    public void quandoCadastrarEmpresaForChamadoSemConflitosDeIdECnpjDeveAdicionarEmpresaAoSistema(){

        gestaoEmpresa.cadastrarEmpresa(empresaNova);

        assertTrue(gestaoEmpresa.listarEmpresas().contains(empresaNova)); // a empresa cadastrada está no sistema?
    }

    @Test
    public void quandoAtualizarEmpresaForChamadoDeveAtualizarOsDadosDaEmpresaCadastrada(){

        gestaoEmpresa.cadastrarEmpresa(empresaNova);
        Empresa empresaAlterada = EmpresaBuilder.builder().
                comNome("Vale do Rio Doce").
                comSeguimento("Produtora de aço").
                build(); // o resto dos atributos já estão por padrão no builders

        gestaoEmpresa.atualizarEmpresa(empresaAlterada);

        // não compara os dois objetos de uma vez pq é arriscado: depende de equals e se algum falhar não sabe qual
        assertAll( // aqui os erros aparecem tudo de uma vez (sem isso, no primeiro erro o teste parava)
                () -> assertEquals(empresaAlterada.getNome(), empresaNova.getNome()),
                () -> assertEquals(empresaAlterada.getCnpj(), empresaNova.getCnpj()),
                () -> assertEquals(empresaAlterada.getEndereco(), empresaNova.getEndereco()),
                () -> assertEquals(empresaAlterada.getSegmento(), empresaNova.getSegmento())
        );
    }

    @Test
    public void quandoOMetodoExcluirEmpresaForChamadoAEmpresaDeveSerExcluidaDoSistema(){

        gestaoEmpresa.cadastrarEmpresa(empresaNova);
        gestaoEmpresa.excluirEmpresa(1);

        assertFalse(gestaoEmpresa.listarEmpresas().contains(empresaNova));
    }

}