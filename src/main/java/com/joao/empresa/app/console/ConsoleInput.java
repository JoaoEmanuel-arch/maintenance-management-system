package com.joao.empresa.app.console;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public final class ConsoleInput {

    private static final DateTimeFormatter DATA_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Scanner scanner;

    public ConsoleInput(Scanner scanner) {
        this.scanner = scanner;
    }

    public int lerOpcao(String mensagem, int minimo, int maximo) {
        while (true) {
            System.out.print(mensagem);
            String entrada = scanner.nextLine().trim();

            try {
                int valor = Integer.parseInt(entrada);

                if (valor < minimo || valor > maximo) {
                    System.out.printf("Digite uma opção entre %d e %d.%n", minimo, maximo);
                    continue;
                }

                return valor;
            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido.");
            }
        }
    }

    public int lerId(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String entrada = scanner.nextLine().trim();

            try {
                int id = Integer.parseInt(entrada);

                if (id <= 0) {
                    System.out.println("O ID deve ser maior que zero.");
                    continue;
                }

                return id;
            } catch (NumberFormatException e) {
                System.out.println("Digite um ID numérico válido.");
            }
        }
    }

    public String lerTextoObrigatorio(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String valor = scanner.nextLine().trim();

            if (!valor.isBlank()) {
                return valor;
            }

            System.out.println("Este campo é obrigatório.");
        }
    }

    public String lerTextoOpcional(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine().trim();
    }

    public LocalDate lerData(String mensagem) {
        while (true) {
            String valor = lerTextoObrigatorio(mensagem);

            try {
                return LocalDate.parse(valor, DATA_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida. Use o formato dd/MM/yyyy.");
            }
        }
    }

    public LocalDate lerDataOpcional(String mensagem, LocalDate valorAtual) {
        while (true) {
            String valor = lerTextoOpcional(mensagem);

            if (valor.isBlank()) {
                return valorAtual;
            }

            try {
                return LocalDate.parse(valor, DATA_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida. Use o formato dd/MM/yyyy.");
            }
        }
    }

    public BigDecimal lerBigDecimal(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String valor = scanner.nextLine().trim().replace(',', '.');

            try {
                BigDecimal numero = new BigDecimal(valor);

                if (numero.compareTo(BigDecimal.ZERO) < 0) {
                    System.out.println("O valor não pode ser negativo.");
                    continue;
                }

                return numero;
            } catch (NumberFormatException e) {
                System.out.println("Digite um valor monetário válido.");
            }
        }
    }

    public boolean confirmar(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String resposta = scanner.nextLine().trim().toLowerCase();

            if (resposta.equals("s") || resposta.equals("sim")) {
                return true;
            }

            if (resposta.equals("n") || resposta.equals("nao") || resposta.equals("não")) {
                return false;
            }

            System.out.println("Responda com 's' ou 'n'.");
        }
    }

    public void executarOperacao(Runnable operacao) {
        try {
            operacao.run();
        } catch (RuntimeException e) {
            String mensagem = e.getMessage();
            System.out.println();
            System.out.println(
                    "[ERRO] " + (mensagem == null || mensagem.isBlank()
                            ? "Não foi possível concluir a operação."
                            : mensagem)
            );
        } finally {
            pausar();
        }
    }

    public void pausar() {
        System.out.println();
        System.out.print("Pressione ENTER para continuar...");
        scanner.nextLine();
    }

    public static String manterSeVazio(String novoValor, String atual) {
        return novoValor.isBlank() ? atual : novoValor;
    }

    public static String formatarData(LocalDate data) {
        return data == null ? "-" : data.format(DATA_FORMATTER);
    }

    public static void titulo(String texto) {
        System.out.println();
        System.out.println("=".repeat(72));
        System.out.println(texto);
        System.out.println("=".repeat(72));
    }
}

