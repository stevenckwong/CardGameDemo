package com.stevenckwong.cardgamedemo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ThreeCardGame {
	
	Map<Integer, Integer> dealerCards;
	Map<Integer, Integer> playerCards;
	Integer cardsDrawn;
	int betPool;
	int commissionCharged;
	String winner; // 0=Dealer, 1=Player; 2=Draw
	static String DEALER="Dealer";
	static String PLAYER="Player";
	static String DRAW="Draw";
	
	
	public ThreeCardGame() {
		dealerCards = new HashMap<Integer, Integer>();
		playerCards = new HashMap<Integer, Integer>();
		cardsDrawn = 0;
		betPool = 0;
		commissionCharged = 0;
	}
	
	// boolean return indicates if the game has been completed.
	// true means game is finished as all 3 cards have been drawn.
	public boolean drawNext() {
		int min = 1;
		int max = 10;
		Random rn = new Random();
		Integer dealerThisCard = new Integer(rn.nextInt(max-min+1)+min);
		Integer playerThisCard = new Integer(rn.nextInt(max-min+1)+min);
		cardsDrawn++;
		dealerCards.put(cardsDrawn, dealerThisCard);
		playerCards.put(cardsDrawn, playerThisCard);
		
		if (cardsDrawn==3) {
			determineWinner();
			return true;
		} else {
			return false;
		}
	}
	
	public int getDealerCard(int cardNo) {
		int card = this.dealerCards.get(new Integer(cardNo)).intValue();
		return card;
	}
	
	public int getPlayerCard(int cardNo) {
		int card = this.playerCards.get(new Integer(cardNo)).intValue();
		return card;
	}
	
	public int getCardsDrawn() {
		return this.cardsDrawn.intValue();
	}
	
	private void determineWinner() {
		int dealerTotal = 0;
		int playerTotal = 0;
		
		for (int i=1;i<=3;i++) {
			dealerTotal+=getDealerCard(i);
			playerTotal+=getPlayerCard(i);
		}
		if (playerTotal > dealerTotal) {
			winner = ThreeCardGame.PLAYER;
		} else if (dealerTotal > playerTotal) {
			winner = ThreeCardGame.DEALER;
		} else {
			winner = ThreeCardGame.DRAW;
		}
		
	}
	
	public String getWinner() {
		return this.winner;
	}
	
	public int getBetPool() {
		return this.betPool;
	}
	
	public int getCommissionCharged() {
		return this.commissionCharged;
	}
	
	public void setCommissionCharged(int c) {
		this.commissionCharged = c;
	}
	
}
