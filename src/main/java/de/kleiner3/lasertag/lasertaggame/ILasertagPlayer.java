package de.kleiner3.lasertag.lasertaggame;

public interface ILasertagPlayer {
	default public int getLasertagScore() { return -1; }
	
	default public void resetLasertagScore() {}
	
	default public void increaseScore(int score) {}
}
