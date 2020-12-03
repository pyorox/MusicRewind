package main;

public class Artist {
	
	private String name = "";
	private int count = 1;
	
	public Artist(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public int getCount() {
		return count;
	}
	
	public void increment() {
		count++;
	}
	
	@Override
	public boolean equals(Object object) {
		try {
			return this.name.equals(((Artist)object).getName());
		} catch(Exception e) {
			
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.name.hashCode();
	}
}
