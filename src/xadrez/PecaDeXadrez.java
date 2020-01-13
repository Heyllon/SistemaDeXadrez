package xadrez;

import tabuleiro.Peca;
import tabuleiro.Posicao;
import tabuleiro.Tabuleiro;

public abstract class PecaDeXadrez extends Peca{

	private Color color;
	private int moveContagem;

	public PecaDeXadrez(Tabuleiro tabuleiro, Color color) {
		super(tabuleiro);
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
	
	public int getMoveContagem() {
		return moveContagem;
	}
	
	public void aumentarMoveContagem() {
		moveContagem++;
	}
	
	public void diminuirMoveContagem() {
		moveContagem--;
	}
	
	public PosicaoXadrez getPosicaoXadrez() {
		return PosicaoXadrez.daPosicao(posicao);
	}
	
	protected boolean existePecaOponente(Posicao posicao) {
		PecaDeXadrez p = (PecaDeXadrez)getTabuleiro().peca(posicao);
		return p != null && p.getColor() != color;
	}
	
}
