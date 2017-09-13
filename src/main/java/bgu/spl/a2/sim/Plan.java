package bgu.spl.a2.sim;


import java.util.List;
/**
 * 
 *this class represents a plan for a specific product: the needed tools and parts in order to assemble it
 *methods: getters and setters
 */

public class Plan {

	private String product;
	private List<String> tools = null;
	private List<String> parts = null;

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public List<String> getTools() {
		return tools;
}

	public void setTools(List<String> tools) {
		this.tools = tools;
	}

	public List<String> getParts() {
		return parts;
	}

	public void setParts(List<String> parts) {
		this.parts = parts;
	}
}
