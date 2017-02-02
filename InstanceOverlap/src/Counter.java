//http://stackoverflow.com/questions/34208705/multiple-lines-of-code-in-stream-foreach/34209023
public class Counter {
	private int count;
	public Counter() {this.count = 0;}
    public int getCount() { return this.count; }
    public void increase() { 
    	this.count++;
    	if (this.count % 5000 == 0) {
    		System.out.println(this.count + " instances compared.");
    	}
    }
}
