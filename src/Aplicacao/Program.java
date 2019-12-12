package aplicacao;

import java.util.InputMismatchException;
import java.util.Scanner;

import xadrez.PartidaDeXadrez;
import xadrez.PecaDeXadrez;
import xadrez.PosicaoXadrez;
import xadrez.XadrezException;

public class Program {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		
		PartidaDeXadrez partidaDeXadrez = new PartidaDeXadrez();
		
		//REPEDE INDEFINIDAMENTE
		while (true) {
			try {
			UI.clearScreen(); //O COMANDO VAI LIMPAR A TELA TODA VEZ QUE RODAR O WHILE
			UI.printPartida(partidaDeXadrez);
			System.out.println();
			System.out.print("\nPosicao de Origem: ");
			PosicaoXadrez origem = UI.leiaPosicaoDeXadrez(sc);
			
			boolean[][] movimentosPossiveis = partidaDeXadrez.possiveisMovimentos(origem);
			UI.clearScreen();
			UI.printTabuleiro(partidaDeXadrez.getPecas(), movimentosPossiveis);
			System.out.println();
			System.out.print("\nPosicao de Destino: ");
			PosicaoXadrez destino = UI.leiaPosicaoDeXadrez(sc);
			
			PecaDeXadrez capturaPeca = partidaDeXadrez.movimentoDeXadrez(origem, destino);
			
			}
			catch(XadrezException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}
			catch(InputMismatchException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}
		}
		
	}

}
