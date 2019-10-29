package operators;

import search.Operator;
import java.awt.Point;

import endgame.EndGameState;
import search.State;

public class RightOperator extends Operator {

	public RightOperator(Point mapDimensions) {
		super(mapDimensions);
	}

	@Override
	public EndGameState transition(State currentState) {
		EndGameState nextState = ((EndGameState) currentState).clone();
		Point currentIronManLoc = nextState.getIronManLoc();
		// Check that iron man is not in the last column
		if (currentIronManLoc.y >= this.getMapDimensions().y - 1) {
			return null;
		}
		Point targetIronManLoc = new Point(currentIronManLoc.x, currentIronManLoc.y + 1);
		if (OperatorUtils.PointCollidesWithOtherObjectsOnMap(targetIronManLoc, nextState)) {
			return null;
		}
		nextState.setIronManLoc(targetIronManLoc);
		return nextState;
	}
}