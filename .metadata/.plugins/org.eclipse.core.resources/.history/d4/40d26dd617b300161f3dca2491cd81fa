import java.util.HashSet;
import java.util.Set;


public class InstanceObject {
 private String className;
 private HashSet<String> instances;
 private int duplicateCounter;
 
 public InstanceObject(String className) {
	 this.className = className;
	 this.instances = new HashSet<String>();
	 this.duplicateCounter = 0;
 }
 /**
  * Add instance to instanceSet
  * @param label (string)
 */
 public void addInstance(String label) {
	 if (!this.instances.contains(label)) {
		 this.instances.add(label);
	 } else {
		 this.duplicateCounter += 1;
	 }
 }
 public HashSet<String> getInstances() {
	 return this.instances;
 }
 
 public int getNumberOfInstances() {
	 return this.instances.size();
 }
 public int getDuplicateCounter() {
	 return this.duplicateCounter;
 }
 
}
