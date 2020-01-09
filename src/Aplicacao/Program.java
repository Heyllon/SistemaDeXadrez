package aplicacao;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import xadrez.PartidaDeXadrez;
import xadrez.PecaDeXadrez;
import xadrez.PosicaoXadrez;
import xadrez.XadrezException;

public class Program {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		PartidaDeXadrez partidaDeXadrez = new PartidaDeXadrez();
		List<PecaDeXadrez> capturadas = new ArrayList<>();
		
		//REPEDE INDEFINIDAMENTE
		while (!partidaDeXadrez.getCheckMate()) {
			try {
				UI.clearScreen(); //O COMANDO VAI LIMPAR A TELA TODA VEZ QUE RODAR O WHILE
				UI.printPartida(partidaDeXadrez, capturadas);
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
				
				if(capturaPeca != null) {
					capturadas.add(capturaPeca);
				}
			
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
		UI.clearScreen();
		UI.printPartida(partidaDeXadrez, capturadas);
		
	}

}
