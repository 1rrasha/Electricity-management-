package application;

//1210773-Rasha Mansour
//AVL tree node
class AVLNode<T extends Comparable<T>> {
	T data;
	AVLNode<T> left, right;
	int height;

	AVLNode(T data) {
		this.data = data;
		this.height = 1;
		this.left = this.right = null;
	}
}