package search;

import java.util.Queue;

import strategies.ASS;
import strategies.GRS;
import strategies.UCS;

import java.util.HashSet;
import java.util.LinkedList;

public abstract class Problem {
	private State initialState;
	private Node root;
	private HashSet<State> visitedStates;
	private Operator[] operators;
	private int expandedNodesCount;
	private Queue<Node> nodes;

	public Problem(State initialState, Operator[] operators) {
		this.initialState = initialState;
		this.root = new Node(initialState);
		this.visitedStates = new HashSet<State>();
		this.operators = operators;
		this.expandedNodesCount = 0;
	}

	public abstract int pathCost(Node currentNode, State nextState, Operator operator);

	public abstract boolean goalTest(State currentState);

	public abstract LinkedList<Node> expand(Node currentNode);

	public Node solveUsingSearch(SearchStrategy strategy) throws SolutionNotFoundException {
		nodes.add(root);

		boolean isCostBasedSearchStrategy = strategy instanceof UCS || strategy instanceof GRS
				|| strategy instanceof ASS;

		if (!isCostBasedSearchStrategy)
			visitedStates.add(root.getState());

		while (!nodes.isEmpty()) {
			Node currentNode = nodes.poll();

			if (isCostBasedSearchStrategy)
				if (!visitedStates.add(currentNode.getState()))
					continue;

			if (goalTest(currentNode.getState()))
				return currentNode;

			LinkedList<Node> expandedNodes = expand(currentNode);

			if (!isCostBasedSearchStrategy)
				expandedNodes.removeIf(expandedNode -> (!visitedStates.add(expandedNode.getState())));

			nodes = strategy.execute(nodes, expandedNodes);
			expandedNodesCount++;
		}

		throw new SolutionNotFoundException();
	}

	public void reset() {
		visitedStates.clear();
		nodes.clear();
	}

	public State getInitialState() {
		return initialState;
	}

	public HashSet<State> getVisitedStates() {
		return visitedStates;
	}

	public Operator[] getOperators() {
		return operators;
	}

	public Node getRoot() {
		return root;
	}

	public int getExpandedNodesCount() {
		return expandedNodesCount;
	}

	public Queue<Node> getNodes() {
		return nodes;
	}

	public void setNodes(Queue<Node> nodes) {
		this.nodes = nodes;
	}
}
