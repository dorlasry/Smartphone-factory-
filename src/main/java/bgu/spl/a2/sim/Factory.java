package bgu.spl.a2.sim;


import java.util.List;
/**
 * This class represents the factory: tools, plans of products, and products to produce
 * 
 * methods:getters and setters
 */

public class Factory {

	private Integer threads;
	private List<ToolGson> tools = null;
	private List<Plan> plans = null;
	List<List<Wave>> waves = null;

	public Integer getThreads() {
		return threads;
	}

	public void setThreads(Integer threads) {
		this.threads = threads;
	}
	
	public List<ToolGson> getTools() {
		return tools;
	}

	public void setTools(List<ToolGson> tools) {
		this.tools = tools;
	}

	public List<Plan> getPlans() {
		return plans;
	}

	public void setPlans(List<Plan> plans) {
		this.plans = plans;
	}

	public List<List<Wave>> getWaves() {
		return waves;
	}

	public void setWaves(List<List<Wave>> waves) {
		this.waves = waves;
	}
}