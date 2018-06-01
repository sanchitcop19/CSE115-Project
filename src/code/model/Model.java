package code.model;

import java.awt.Point;
import java.util.HashSet;

import code.ui.UI;

public class Model {
	
	private UI _observer;	
	private Board _board;
	private Selector _selector;
	
	public Model() {
		_board = new Board(10, 10, this);
		_selector = new Selector(_board);
		
	}
	
	public void addObserver(UI ui) {
		_observer = ui;
		_observer.update();
	}
	
	
	public int rows() { return _board.rows(); }
	public int cols() { return _board.cols(); }

	public String get(Point p) {
		return _board.get(p);
	}
	
	public Point selectedFirst() {
		return _selector.selectedFirst();
	}

	public void select(int r, int c) {
		_selector.select(new Point(r,c));
		_observer.update();
	}
	
	public void update(){
		
		_observer.update();
		
	}

    
    public HashSet<Point> getMatchedRegion(){
    	return _board.getMatchedRegion();
    }
   

}
