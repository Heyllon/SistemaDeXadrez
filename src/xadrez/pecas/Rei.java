package xadrez.pecas;

import tabuleiro.Tabuleiro;
import xadrez.Color;
import xadrez.PecaDeXadrez;

public class Rei extends PecaDeXadrez{

	public Rei(Tabuleiro tabuleiro, Color color) {
		super(tabuleiro, color);
	}
	
	@Override
	public String toString() {
		return "R";
	}

	//DEFINI��O DOS MOVIMENTOS DA PE�A REI
	@Override
	public boolean[][] movimentosPossiveis() {
		boolean[][] mat = new boolean[getTabuleiro().getLinhas()][getTabuleiro().getColunas()];
		return mat;
	}

}
