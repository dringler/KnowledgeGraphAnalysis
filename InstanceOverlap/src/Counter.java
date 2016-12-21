//http://stackoverflow.com/questions/34208705/multiple-lines-of-code-in-stream-foreach/34209023
public class Counter {
	private int count;
	public Counter() {count = 0;}
    public int getCount() { return count; }
    public void increase() { 
    	count++;
    	if (count % 5000 == 0)
    		System.out.println(count + " instances compared.");
    }
}
