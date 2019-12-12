package xadrez;

import tabuleiro.Peca;
import tabuleiro.Posicao;
import tabuleiro.Tabuleiro;
import xadrez.pecas.Rei;
import xadrez.pecas.Torre;

public class PartidaDeXadrez {

	private Tabuleiro tabuleiro;
	
	public PartidaDeXadrez() {
		tabuleiro = new Tabuleiro(8, 8);
		configuracaoInicial();
	}
	
	public PecaDeXadrez[][] getPecas(){
		PecaDeXadrez[][] mat = new PecaDeXadrez[tabuleiro.getLinhas()][tabuleiro.getColunas()];
		for (int i=0; i<tabuleiro.getLinhas(); i++) {
			for (int j=0; j<tabuleiro.getColunas(); j++) {
				mat[i][j] = (PecaDeXadrez) tabuleiro.peca(i, j);
			}
		}
		return mat;
	}
	
	public boolean [][] possiveisMovimentos(PosicaoXadrez possicaoOrigem){
		Posicao posicao = possicaoOrigem.toPosicao();
		validandoPosicaoDeOrigem(posicao);
		return tabuleiro.peca(posicao).movimentosPossiveis();
	}
	
	public PecaDeXadrez movimentoDeXadrez(PosicaoXadrez posicaoDeOrigem, PosicaoXadrez posicaoDeDestino) {
		Posicao origem = posicaoDeOrigem.toPosicao();
		Posicao destino = posicaoDeDestino.toPosicao();
		validandoPosicaoDeOrigem(origem);
		validandoPosicaoDeDestino(origem, destino);
		Peca capturaPeca = realizaMovimento(origem, destino);
		return (PecaDeXadrez)capturaPeca;
	}
	
	private Peca realizaMovimento(Posicao origem, Posicao destino) {
		Peca p = tabuleiro.removePeca(origem);
		Peca capturaPeca = tabuleiro.removePeca(destino);
		tabuleiro.colocarPeca(p, destino);
		return capturaPeca;
	}
	
	//! NA FRENTE NEGA O TESTE
	private void validandoPosicaoDeOrigem(Posicao posicao) {
		if (!tabuleiro.existeUmaPeca(posicao)) {
			throw new XadrezException("Nao ha uma peca na posicao de origem.");
		}
		if(!tabuleiro.peca(posicao).existeAlgumMovimentoPossivel()) {
			throw new XadrezException("Nao existe movimentos possiveis para a peca escolhida.");
		}
	}
	
	private void validandoPosicaoDeDestino(Posicao origem, Posicao destino) {
		if(!tabuleiro.peca(origem).movimentoPossivel(destino)) {
			throw new XadrezException("A peca escolhida nao pode se mover para a posicao de destino.");
		}
	}
	
	private void coloqueNovaPeca(char coluna, int linha, PecaDeXadrez peca) {
		tabuleiro.colocarPeca(peca, new PosicaoXadrez(coluna, linha).toPosicao());
	}
	
	//COLOCANDO PEÇAS NO TABULEIRO
	private void configuracaoInicial() {
		
		coloqueNovaPeca('c', 1, new Torre(tabuleiro, Color.WHITE));
		coloqueNovaPeca('c', 2, new Torre(tabuleiro, Color.WHITE));
		coloqueNovaPeca('d', 2, new Torre(tabuleiro, Color.WHITE));
		coloqueNovaPeca('e', 2, new Torre(tabuleiro, Color.WHITE));
		coloqueNovaPeca('e', 1, new Torre(tabuleiro, Color.WHITE));
		coloqueNovaPeca('d', 1, new Rei(tabuleiro, Color.WHITE));

		coloqueNovaPeca('c', 7, new Torre(tabuleiro, Color.BLACK));
		coloqueNovaPeca('c', 8, new Torre(tabuleiro, Color.BLACK));
		coloqueNovaPeca('d', 7, new Torre(tabuleiro, Color.BLACK));
		coloqueNovaPeca('e', 7, new Torre(tabuleiro, Color.BLACK));
		coloqueNovaPeca('e', 8, new Torre(tabuleiro, Color.BLACK));
		coloqueNovaPeca('d', 8, new Rei(tabuleiro, Color.BLACK));
	}
	
}
