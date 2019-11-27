package aplicacao;

import java.util.Scanner;

import xadrez.PartidaDeXadrez;
import xadrez.PecaDeXadrez;
import xadrez.PosicaoXadrez;

public class Program {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		
		PartidaDeXadrez partidaDeXadrez = new PartidaDeXadrez();
		
		//REPEDE INDEFINIDAMENTE
		while (true) {
			UI.printTabuleiro(partidaDeXadrez.getPecas());
			System.out.println();
			System.out.print("\nPosicao de Origem: ");
			PosicaoXadrez origem = UI.leiaPosicaoDeXadrez(sc);
			
			System.out.println();
			System.out.print("Posicao de Destino: ");
			PosicaoXadrez destino = UI.leiaPosicaoDeXadrez(sc);
			
			PecaDeXadrez capturaPeca = partidaDeXadrez.movimentoDeXadrez(origem, destino);
			
		}
		
	}

}
