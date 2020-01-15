package xadrez;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import tabuleiro.Peca;
import tabuleiro.Posicao;
import tabuleiro.Tabuleiro;
import xadrez.pecas.Bispo;
import xadrez.pecas.Cavalo;
import xadrez.pecas.Peao;
import xadrez.pecas.Rainha;
import xadrez.pecas.Rei;
import xadrez.pecas.Torre;

public class PartidaDeXadrez {

	private int turno;
	private Color jogadorAtual;
	private Tabuleiro tabuleiro;
	private boolean check;
	private boolean checkMate;
	private PecaDeXadrez vulneravelPassante;
	private PecaDeXadrez promovido;
	
	private List<Peca> pecasNoTabuleiro = new ArrayList<>();
	private List<Peca> pecasCapturadas = new ArrayList<>();
	
	public PartidaDeXadrez() {
		tabuleiro = new Tabuleiro(8, 8);
		turno = 1;
		jogadorAtual = Color.WHITE;
		check = false;
		configuracaoInicial();
	}
	
	public int getTurno() {
		return turno;
	}
	
	public Color getJogadorAtual() {
		return jogadorAtual;
	}
	
	public boolean getCheck() {
		return check;
	}
	
	public boolean getCheckMate() {
		return checkMate;
	}
	
	public PecaDeXadrez getVulneravelPassante() {
		return vulneravelPassante;
	}
	
	public PecaDeXadrez getPromovido() {
		return promovido;
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
		
		if (testeCheck(jogadorAtual)) {
			desfazerMovimento(origem, destino, capturaPeca);
			throw new XadrezException("Voce nao pode se colocar em Check!!");
		}
		
		PecaDeXadrez pecaQueMoveu = (PecaDeXadrez)tabuleiro.peca(destino);
		
		//MOVIMENTO ESPECIAL - PROMOCAO
		promovido = null;
		if (pecaQueMoveu instanceof Peao) {
			if ((pecaQueMoveu.getColor() == Color.WHITE && destino.getLinha() == 0) || (pecaQueMoveu.getColor() == Color.BLACK && destino.getLinha() == 7)) {
				promovido = (PecaDeXadrez)tabuleiro.peca(destino);
				promovido = substituirPecaPromovida("R");
			}
		}
		
		check = (testeCheck(oponente(jogadorAtual))) ? true : false;
		
		if (testeCheckMate(oponente(jogadorAtual))) {
			checkMate = true;
		}
		else {
			proximoJogador();
		}
		
		//MOVIMENTO ESPECIAL PASSANT
		if (pecaQueMoveu instanceof Peao && (destino.getLinha() == origem.getLinha() - 2 || destino.getLinha() == origem.getLinha() + 2)) { 
			vulneravelPassante = pecaQueMoveu;
		}
		else {
			vulneravelPassante = null;
		}
		
		return (PecaDeXadrez)capturaPeca;
	}
	
	public PecaDeXadrez substituirPecaPromovida(String tipo) {
		if(promovido == null) {
			throw new IllegalStateException("Nao a peca para ser promovida");
		}
		if (!tipo.equals("B") && !tipo.equals("C") && !tipo.equals("T") && !tipo.equals("R")) {
			throw new InvalidParameterException("Tipo Invalido para Promocao");
		}
		
		Posicao pos = promovido.getPosicaoXadrez().toPosicao();
		Peca p = tabuleiro.removePeca(pos);
		pecasNoTabuleiro.remove(p);
		
		PecaDeXadrez novaPeca = novaPeca(tipo, promovido.getColor());
		tabuleiro.colocarPeca(novaPeca, pos);
		pecasNoTabuleiro.add(novaPeca);
		
		return novaPeca;
		
	}
	
	private PecaDeXadrez novaPeca(String tipo, Color color) {
		if (tipo.equals("B")) return new Bispo(tabuleiro, color);
		if (tipo.equals("C")) return new Cavalo(tabuleiro, color);
		if (tipo.equals("T")) return new Torre(tabuleiro, color);
		return new Rainha(tabuleiro, color);
	}
	
	private Peca realizaMovimento(Posicao origem, Posicao destino) {
		PecaDeXadrez p = (PecaDeXadrez)tabuleiro.removePeca(origem);
		p.aumentarMoveContagem();
		Peca capturaPeca = tabuleiro.removePeca(destino);
		tabuleiro.colocarPeca(p, destino);
		if (capturaPeca != null) {
			pecasNoTabuleiro.remove(capturaPeca);
			pecasCapturadas.add(capturaPeca);
		}
		
		//ROOK PEQUENO
		if (p instanceof Rei && destino.getColuna() == origem.getColuna() + 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() + 1);
			PecaDeXadrez torre = (PecaDeXadrez)tabuleiro.removePeca(origemT);
			tabuleiro.colocarPeca(torre, destinoT);
			torre.aumentarMoveContagem();
		}
		
		//ROOK GRANDE
		if (p instanceof Rei && destino.getColuna() == origem.getColuna() - 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
			PecaDeXadrez torre = (PecaDeXadrez)tabuleiro.removePeca(origemT);
			tabuleiro.colocarPeca(torre, destinoT);
			torre.aumentarMoveContagem();
		}
		
		//MOVIMENTO ESPECIAL PASSANT
		if (p instanceof Peao) {
			if (origem.getColuna() != destino.getColuna() && capturaPeca == null) {
				Posicao posicaoPeao;
				if (p.getColor() == Color.WHITE) {
					posicaoPeao = new Posicao(destino.getLinha() + 1, destino.getColuna());
				}
				else {
					posicaoPeao = new Posicao(destino.getLinha() - 1, destino.getColuna());
				}
				capturaPeca = tabuleiro.removePeca(posicaoPeao);
				pecasCapturadas.add(capturaPeca);
				pecasNoTabuleiro.remove(capturaPeca);
			}
		}
		
		return capturaPeca;
	}
	
	private void desfazerMovimento(Posicao origem, Posicao destino, Peca capturaPeca) {
		PecaDeXadrez p = (PecaDeXadrez)tabuleiro.removePeca(destino);
		p.diminuirMoveContagem();
		tabuleiro.colocarPeca(p, origem);
		if (capturaPeca != null) {
			tabuleiro.colocarPeca(capturaPeca, destino);
			pecasCapturadas.remove(capturaPeca);
			pecasNoTabuleiro.add(capturaPeca);
		}
		
		//ROOK PEQUENO
		if (p instanceof Rei && destino.getColuna() == origem.getColuna() + 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() + 1);
			PecaDeXadrez torre = (PecaDeXadrez)tabuleiro.removePeca(destinoT);
			tabuleiro.colocarPeca(torre, origemT);
			torre.diminuirMoveContagem();
		}
		
		//ROOK GRANDE
		if (p instanceof Rei && destino.getColuna() == origem.getColuna() - 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
			PecaDeXadrez torre = (PecaDeXadrez)tabuleiro.removePeca(destinoT);
			tabuleiro.colocarPeca(torre, origemT);
			torre.diminuirMoveContagem();
		}
		
		//MOVIMENTO ESPECIAL PASSANT
		if (p instanceof Peao) {
			if (origem.getColuna() != destino.getColuna() && capturaPeca == vulneravelPassante) {
				PecaDeXadrez peao = (PecaDeXadrez)tabuleiro.removePeca(destino);
				Posicao posicaoPeao;
				if (p.getColor() == Color.WHITE) {
					posicaoPeao = new Posicao(3, destino.getColuna());
				}
				else {
					posicaoPeao = new Posicao(4, destino.getColuna());
				}
				tabuleiro.colocarPeca(peao, posicaoPeao);
				
			}
		}
		
	}
	
	//! NA FRENTE NEGA O TESTE
	private void validandoPosicaoDeOrigem(Posicao posicao) {
		if (!tabuleiro.existeUmaPeca(posicao)) {
			throw new XadrezException("Nao ha uma peca na posicao de origem.");
		}
		if(jogadorAtual != ((PecaDeXadrez)tabuleiro.peca(posicao)).getColor()) {
			throw new XadrezException("A peca escolhida nao e sua.");
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
	
	private void proximoJogador() {
		turno++;
		jogadorAtual = (jogadorAtual == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
	
	private Color oponente(Color color) {
		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
	
	private PecaDeXadrez rei(Color color) {
		List<Peca> list = pecasNoTabuleiro.stream().filter(x -> ((PecaDeXadrez)x).getColor() == color).collect(Collectors.toList());
		for (Peca p : list) {
			if (p instanceof Rei) {
				return (PecaDeXadrez)p;
			}
		}
		throw new IllegalStateException("Nao existe o Rei da cor " + color + "no tabuleiro");
	}
	
	private boolean testeCheck(Color color) {
		Posicao posicaoDoRei = rei(color).getPosicaoXadrez().toPosicao();
		List<Peca> pecasOponente = pecasNoTabuleiro.stream().filter(x -> ((PecaDeXadrez)x).getColor() == oponente(color)).collect(Collectors.toList());
		for (Peca p : pecasOponente) {
			boolean[][] mat = p.movimentosPossiveis();
			if (mat[posicaoDoRei.getLinha()][posicaoDoRei.getColuna()]) {
				return true;
			}
		}
		return false;
	}
	
	private boolean testeCheckMate(Color color) {
		if (!testeCheck(color)) {
			return false;
		}
		List<Peca> list = pecasNoTabuleiro.stream().filter(x -> ((PecaDeXadrez)x).getColor() == color).collect(Collectors.toList());
		for (Peca p : list) {
			boolean[][] mat = p.movimentosPossiveis();
			for (int i=0; i<tabuleiro.getLinhas(); i++) {
				for (int j=0; j<tabuleiro.getColunas(); j++) {
					if (mat[i][j]) {
						Posicao origem = ((PecaDeXadrez)p).getPosicaoXadrez().toPosicao();
						Posicao destino = new Posicao(i, j);
						Peca capturaPeca = realizaMovimento(origem, destino);
						boolean testeCheck = testeCheck(color);
						desfazerMovimento(origem, destino, capturaPeca);
						if (!testeCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	private void coloqueNovaPeca(char coluna, int linha, PecaDeXadrez peca) {
		tabuleiro.colocarPeca(peca, new PosicaoXadrez(coluna, linha).toPosicao());
		pecasNoTabuleiro.add(peca);
	}
	
	//COLOCANDO PEÇAS NO TABULEIRO
	private void configuracaoInicial() {
		
		coloqueNovaPeca('a', 1, new Torre(tabuleiro, Color.WHITE));
		coloqueNovaPeca('b', 1, new Cavalo(tabuleiro, Color.WHITE));
		coloqueNovaPeca('c', 1, new Bispo(tabuleiro, Color.WHITE));
		coloqueNovaPeca('d', 1, new Rainha(tabuleiro, Color.WHITE));
		coloqueNovaPeca('e', 1, new Rei(tabuleiro, Color.WHITE, this)); //THIS REALIZANDO A AUTO REFERENCIA 
		coloqueNovaPeca('f', 1, new Bispo(tabuleiro, Color.WHITE));
		coloqueNovaPeca('g', 1, new Cavalo(tabuleiro, Color.WHITE));
		coloqueNovaPeca('h', 1, new Torre(tabuleiro, Color.WHITE));
		coloqueNovaPeca('a', 2, new Peao(tabuleiro, Color.WHITE, this));
		coloqueNovaPeca('b', 2, new Peao(tabuleiro, Color.WHITE, this));
		coloqueNovaPeca('c', 2, new Peao(tabuleiro, Color.WHITE, this));
		coloqueNovaPeca('d', 2, new Peao(tabuleiro, Color.WHITE, this));
		coloqueNovaPeca('e', 2, new Peao(tabuleiro, Color.WHITE, this));
		coloqueNovaPeca('f', 2, new Peao(tabuleiro, Color.WHITE, this));
		coloqueNovaPeca('g', 2, new Peao(tabuleiro, Color.WHITE, this));
		coloqueNovaPeca('h', 2, new Peao(tabuleiro, Color.WHITE, this));

		coloqueNovaPeca('a', 8, new Torre(tabuleiro, Color.BLACK));
		coloqueNovaPeca('b', 8, new Cavalo(tabuleiro, Color.BLACK));
		coloqueNovaPeca('c', 8, new Bispo(tabuleiro, Color.BLACK));
		coloqueNovaPeca('d', 8, new Rainha(tabuleiro, Color.BLACK));
		coloqueNovaPeca('e', 8, new Rei(tabuleiro, Color.BLACK, this));
		coloqueNovaPeca('f', 8, new Bispo(tabuleiro, Color.BLACK));
		coloqueNovaPeca('g', 8, new Cavalo(tabuleiro, Color.BLACK));
		coloqueNovaPeca('h', 8, new Torre(tabuleiro, Color.BLACK));
		coloqueNovaPeca('a', 7, new Peao(tabuleiro, Color.BLACK, this));
		coloqueNovaPeca('b', 7, new Peao(tabuleiro, Color.BLACK, this));
		coloqueNovaPeca('c', 7, new Peao(tabuleiro, Color.BLACK, this));
		coloqueNovaPeca('d', 7, new Peao(tabuleiro, Color.BLACK, this));
		coloqueNovaPeca('f', 7, new Peao(tabuleiro, Color.BLACK, this));
		coloqueNovaPeca('e', 7, new Peao(tabuleiro, Color.BLACK, this));
		coloqueNovaPeca('g', 7, new Peao(tabuleiro, Color.BLACK, this));
		coloqueNovaPeca('h', 7, new Peao(tabuleiro, Color.BLACK, this));
		
	}
	
}
