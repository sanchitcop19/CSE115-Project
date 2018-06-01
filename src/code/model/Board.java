package code.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class Board {
	private ArrayList<ArrayList<String>> _board;
	private ArrayList<String> _colorFileNames;
	private Random _rand;
	private boolean _recreateBoard = false;
	private int _rows;//convert to local
	private int _cols;//convert to local
	private HashSet<Point> _matchedPoints;
	private Model _model;
	private int _score;



	private static int MAX_COLORS = 6; // max possible is 6

	public Board(int rows, int cols, Model m) {
		
		_model = m;

		createBoard(rows, cols);
		this._rows = rows;
		this._cols = cols;

		

		while(match().size()>0){
			
			createBoard(rows, cols);

			while(!checkLegalMatch()){
				createBoard(rows, cols);
			}
		}
		
		
		
			

	}


	public int rows() { return _board.size(); }
	public int cols() { return _board.get(0).size(); }

	public String get(Point p) {
		return _board.get(p.x).get(p.y);
	}

	private String set(Point p, String s) {
		return _board.get(p.x).set(p.y, s);
	}

	public void exchange(Point p, Point q) {
		String temp = get(p);
		set(p, get(q));
		set(q, temp);


		handleExchange();
		
	

	}

	public HashSet<Point> match() {
		return match(3);
	}

	private HashSet<Point> match(int runLength) {
		HashSet<Point> matches = verticalMatch(runLength);
		matches.addAll(horizontalMatch(runLength));
		return matches;
	}

	private HashSet<Point> horizontalMatch(int runLength) {
		HashSet<Point> matches = new HashSet<Point>();
		int minCol = 0;
		int maxCol = cols() - runLength;
		for (int r = 0; r < rows(); r = r + 1) {
			for (int c = minCol; c <= maxCol; c = c + 1) {  // The cols we can START checking in
				HashSet<String> values = new HashSet<String>();
				HashSet<Point> points = new HashSet<Point>();
				for (int offset = 0; offset < runLength; offset = offset + 1) {
					Point p = new Point(r,c+offset);
					points.add(p);
					String s = get(p);
					values.add(s);
				}
				if (values.size() == 1) { 
					matches.addAll(points); 
					
				}
			}
		}
		return matches;
	}

	private HashSet<Point> verticalMatch(int runLength) {
		HashSet<Point> matches = new HashSet<Point>();
		int minRow = 0;
		int maxRow = rows() - runLength;
		for (int c = 0; c < cols(); c = c + 1) {
			for (int r = minRow; r <= maxRow; r = r + 1) {  // The rows we can START checking in
				HashSet<String> values = new HashSet<String>();
				HashSet<Point> points = new HashSet<Point>();
				for (int offset = 0; offset < runLength; offset = offset + 1) {
					Point p = new Point(r+offset,c);
					points.add(p);
					String s = get(p);
					values.add(s);
				}
				if (values.size() == 1) { 
					matches.addAll(points); 
				}
			}
		}
		return matches;
	}

	public void createBoard(int rows, int cols){
		_board = new ArrayList<ArrayList<String>>();
		_rand = new Random();
		_colorFileNames = new ArrayList<String>();
		for (int i=0; i<MAX_COLORS; i=i+1) {
			_colorFileNames.add("Images/Tile-"+i+".png");
		}
		for (int r=0; r<rows; r=r+1) {
			ArrayList<String> row = new ArrayList<String>();
			for (int c=0; c<cols; c=c+1) {
				//FIXME
				row.add(_colorFileNames.get(_rand.nextInt(_colorFileNames.size())));
			}
			_board.add(row);
		}
	}

	public boolean checkLegalMatch(Point a, Point b) {

		String temp = get(a);
		set(a, get(b));
		set(b, temp);
		temp = null;

		if (match().size() > 0) {

			temp = get(a);
			set(a, get(b));
			set(b, temp);
			temp = null;
			
		

			return true;
		}
		else {

			temp = get(a);
			set(a, get(b));
			set(b, temp);
			temp = null;
			return false;
		}
	}

	public boolean checkLegalMatch(){
		for (int i = 0; i < _rows; ++i){
			for (int j = 0; j < _cols-1; ++j){
				Point point1 = new Point(i, j);
				Point point2 = new Point(i, j+1);
				_recreateBoard = checkLegalMatch(point1, point2);
				
				if (_recreateBoard){
					break;
				}
			}
		}
		if (_recreateBoard){
			return true;
		}
		for (int i = 0; i < _rows-1; ++i){
			for (int j = 0; j < _cols; ++j){
				Point point1 = new Point(i, j);
				Point point2 = new Point(i+1, j);
				_recreateBoard = checkLegalMatch(point1, point2);
				if (_recreateBoard){
					break;
				}
			}
		}

		return _recreateBoard;	
	}




	public HashSet<Point> getMatchedRegion(){
		return _matchedPoints;
	}

	

	public void dropTiles(){
		for(int i = 0; i < _rows; ++i){
			for(int j = 0; j < _cols; ){
				Point currentPoint = new Point(i, j);
				while(get(currentPoint)==null){
					if(i==0){
						
						set(currentPoint, _colorFileNames.get(_rand.nextInt(_colorFileNames.size())));
						
						
					}
					else{
						Point pointAbove = new Point(i-1, j);
						set(currentPoint, get(pointAbove));
						set(pointAbove, null);
						
						
					}
					i = 0;
					j = 0;
					
				}
				if (currentPoint!= new Point(0, 0)){
				++j;
				}
				else{
					set(currentPoint, _colorFileNames.get(_rand.nextInt(_colorFileNames.size())));
				}
			}
		}
		
		_model.update();
	}
	public void setNull(){
		for(Point a: _matchedPoints){
			set(a, null);
		}
	}
	
	public void handleExchange(){
		while(match().size()>0){
			_matchedPoints = match();
			setNull();
			dropTiles();
			_score += _matchedPoints.size();
			System.out.println(_score);
		}


		if (!checkLegalMatch2()){
			
			System.out.println("Score: " + _score);
			System.exit(0);
		}
		
	
		
		
		
	}

	
	public boolean checkLegalMatch2(Point a, Point b) {

		String temp = get(a);
		set(a, get(b));
		set(b, temp);
		temp = null;

		if (match().size() > 0) {

			temp = get(a);
			set(a, get(b));
			set(b, temp);
			temp = null;
			

			return true;
		}
		else {

			temp = get(a);
			set(a, get(b));
			set(b, temp);
			temp = null;
			return false;
		}
	}

	public boolean checkLegalMatch2(){
		boolean legalMove = false;
		for (int i = 0; i < _rows; ++i){
			for (int j = 0; j < _cols-1; ++j){
				Point point1 = new Point(i, j);
				Point point2 = new Point(i, j+1);
				legalMove = checkLegalMatch(point1, point2);
				
				if (legalMove){
					
					break;
				}
				
			}
		}
		
		if (legalMove){
			return legalMove;
		}
		
		for (int i = 0; i < _rows-1; ++i){
			for (int j = 0; j < _cols; ++j){
				Point point1 = new Point(i, j);
				Point point2 = new Point(i+1, j);
				legalMove = checkLegalMatch(point1, point2);
				
				if (legalMove){
					break;
				}
			}
		}

		return legalMove;	
	}
		
}



