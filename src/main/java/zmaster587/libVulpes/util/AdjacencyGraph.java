package zmaster587.libVulpes.util;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class AdjacencyGraph<T> {
	private final HashMap<T, HashSet<T>> adjacencyMatrix;

	public AdjacencyGraph() {
		adjacencyMatrix = new HashMap<>();
	}

	/**
	 * Adds a node to the graph
	 * @param object object to add to the node
	 * @param adjNodes any adjacent nodes
	 */
	public void add(T object, @NotNull HashSet<T> adjNodes) {
		if(!contains(object)) {
			adjacencyMatrix.put(object, adjNodes);

            for (T adjNode : adjNodes) {
                adjacencyMatrix.get(adjNode).add(object);
            }
		}
	}

	/**
	 * Returns whether or not the object already exists in the graph
	 * @param node node to verify existance in the graph
	 * @return true if the object already exists in the graph, false if not
	 */
	public boolean contains(T node) {
		return adjacencyMatrix.get(node) != null;
	}

	/**
	 * Returns whether or not a is connected to b
	 * @param a a node
	 * @param b node to check if a's neighbor
	 * @return true if a is connected to be through an edge in the graph
	 */
	public boolean isNeighbor(T a, T b) {
		HashSet<T> set = adjacencyMatrix.get(a);
		if(set != null) {
			return set.contains(b);
		}

		return false;
	}
	
	/**
	 * @return set containing all keys
	 */
	public final Set<T> getKeys() {
		return adjacencyMatrix.keySet();
	}

	/**
	 * 
	 * @param node starting node
	 * @return a hashset containing all block connected to node including itself
	 */
	public HashSet<T> getAllNodesConnectedToNode(T node) {
		HashSet<T> removableNodes = new HashSet<>();
		getAllNodesConnectedToBlock(node, removableNodes);

		return removableNodes;
	}

	/**
	 * Helper function for getAllNodesConnectedToNode(T node)
	 * @param node node to check adjacent nodes of
	 * @param removableNodes HashSet containing visited nodes
	 */
	private void getAllNodesConnectedToBlock(T node,HashSet<T> removableNodes) {

		
		Stack<T> stack = new Stack<>();
		stack.push(node);
		removableNodes.add(node);
		
		while(!stack.isEmpty()) {
			T stackElement = stack.pop();

            for (T nextElement : adjacencyMatrix.get(stackElement)) {
                if (!removableNodes.contains(nextElement)) {
                    stack.push(nextElement);
                    removableNodes.add(nextElement);
                }
            }
		}
		
		/*Iterator<T> iterator = adjacencyMatrix.get(node).iterator();

		removableNodes.add(node);
		while(iterator.hasNext()) {
			T block = iterator.next();
			if(!removableNodes.contains(block) && adjacencyMatrix.containsKey(block)) {
				getAllNodesConnectedToBlock(block, removableNodes);
			}
		}*/
	}

	/**
	 * Helper method for checking if a path from from to to exists
	 * @param from node to start with
	 * @param to node to find
	 * @param removableNodes HashSet containing visitedNodes
	 * @return true if a path from from to to is found
	 */
	private boolean findPathToBlock(T from, @NotNull T to, @NotNull HashSet<T> removableNodes) {

		Stack<T> stack = new Stack<>();
		stack.push(from);

		while(!stack.isEmpty()) {
			T stackElement = stack.pop();
			removableNodes.add(stackElement);

            for (T nextElement : adjacencyMatrix.get(stackElement)) {
                if (to.equals(nextElement))
                    return true;

                if (!removableNodes.contains(nextElement))
                    stack.push(nextElement);
            }
		}
		
		return false;
	}

	/**
	 * Method for checking if a path from from to to exists
	 * @param from node to start with
	 * @param to node to find
	 * @return true if a path from from to to is found
	 */
	public boolean doesPathExist(T from, @NotNull T to) {
		HashSet<T> pos = new HashSet<>();

		return findPathToBlock(from, to, pos);
	}

	/**
	 * Removes all nodes connected to node
	 * @param node node to start from
	 */
	public Collection<T> removeAllNodesConnectedTo(T node) {

		HashSet<T> removableNode = getAllNodesConnectedToNode(node);

        for (T t : removableNode) adjacencyMatrix.remove(t);
		
		return removableNode;
	}

	/**
	 * Removes a single node from the graph
	 * @param node node to remove
	 */
	public void remove(T node) {
		HashSet<T> set = adjacencyMatrix.get(node);
		if(set != null) {
            for (T t : set) {
                adjacencyMatrix.get(t).remove(node);
            }
		}
		adjacencyMatrix.remove(node); //I will be not here
	}

	public void clear() {
		adjacencyMatrix.clear();
	}
	
	/**
	 * @return number of elements contained in the matrix
	 */
	public int size() {
		return adjacencyMatrix.size();
	}
}