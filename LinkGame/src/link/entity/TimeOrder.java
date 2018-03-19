package link.entity;

public class TimeOrder {
	private int id;
	private String name;
	private int time;
	private int score;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}

	public TimeOrder(int id, String name, int time, int score) {
		super();
		this.id = id;
		this.name = name;
		this.time = time;
		this.score = score;
	}
	
	
}
