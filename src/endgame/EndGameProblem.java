package endgame;

import java.util.HashSet;
import java.util.LinkedList;

import cells.*;
import search.Node;
import search.Operator;
import search.Problem;
import search.State;
import java.awt.Point;
import operators.*;

public class EndGameProblem extends Problem {
	private Point mapDimensions;

	private static final int MAX_COST = 100;

	public EndGameProblem(Point ironManLoc, Point thanosLoc, HashSet<Point> stonesLoc, HashSet<Point> warriorsLoc,
			Point mapDimensions) {

		super(new EndGameState(ironManLoc, stonesLoc, warriorsLoc, thanosLoc),
				new Operator[] { new CollectOperator(), new SnapOperator(), new UpOperator(mapDimensions),
						new DownOperator(mapDimensions), new LeftOperator(mapDimensions),
						new RightOperator(mapDimensions), new KillOperator(mapDimensions) });
		this.mapDimensions = mapDimensions;
	}

	@Override
	public int pathCost(Node currentNode, State nextState, Operator operator) {
		int nextStateCost = 0;
		if (operator instanceof CollectOperator) {
			nextStateCost += 3;
		}

		if (operator instanceof KillOperator) {
			int currentWarriorCount = EndGameUtils.CountEnemiesAround((EndGameState) currentNode.getState());
			nextStateCost += currentWarriorCount * 2;
		}

		int nextWarriorCount = EndGameUtils.CountEnemiesAround((EndGameState) nextState);
		nextStateCost += nextWarriorCount;

		if (EndGameUtils.IsThanosAround((EndGameState) nextState)) {
			nextStateCost += 5;
		}
		
		if(operator instanceof SnapOperator) {
			nextStateCost = 0;
		}

		return currentNode.getCost() + nextStateCost;
	}

	@Override
	public boolean goalTest(State currentState) {
		return ((EndGameState) currentState).getThanosLoc() == null ? true : false;
	}

	@Override
	public LinkedList<Node> expand(Node currentNode) {
		LinkedList<Node> expandedNodes = new LinkedList<Node>();

		for (Operator currentOperator : getOperators()) {
			EndGameState newState = (EndGameState) currentOperator.transition(currentNode.getState());

			if (newState == null)
				continue;

			int newStateCost = pathCost(currentNode, newState, currentOperator);
			if (newStateCost >= MAX_COST) {
				continue;
			}
			expandedNodes.add(new Node(newState, currentNode, currentOperator, newStateCost));
		}

		return expandedNodes;
	}

	public Point getMapDimensions() {
		return mapDimensions;
	}

	public Cell[][] constructMap(EndGameState state) {
		Cell[][] map = new Cell[mapDimensions.x][mapDimensions.y];

		for (int x = 0; x < map.length; x++)
			for (int y = 0; y < map[x].length; y++)
				map[x][y] = new EmptyCell(new Point(x, y));

		for (Point stoneLoc : state.getStonesLoc())
			map[stoneLoc.x][stoneLoc.y] = new StoneCell(stoneLoc);

		for (Point warriorLoc : state.getWarriorsLoc())
			map[warriorLoc.x][warriorLoc.y] = new WarriorCell(warriorLoc);

		if (state.getThanosLoc() != null)
			map[state.getThanosLoc().x][state.getThanosLoc().y] = new ThanosCell(state.getIronManLoc());

		if (map[state.getIronManLoc().x][state.getIronManLoc().y] instanceof StoneCell)
			map[state.getIronManLoc().x][state.getIronManLoc().y] = new IronManStoneCell(state.getIronManLoc());
		else if (map[state.getIronManLoc().x][state.getIronManLoc().y] instanceof ThanosCell)
			map[state.getIronManLoc().x][state.getIronManLoc().y] = new IronManThanosCell(state.getIronManLoc());
		else
			map[state.getIronManLoc().x][state.getIronManLoc().y] = new IronManCell(state.getIronManLoc());

		return map;
	}
}
