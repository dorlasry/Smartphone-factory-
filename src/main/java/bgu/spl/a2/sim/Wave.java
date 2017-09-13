package bgu.spl.a2.sim;

/**
* 
*this class represents a wave: order of an amount of a specific product to produce
*methods: getters and setters
*/
public class Wave {


	private String product;
	private Integer qty;
	private Long startId;

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}

	public Long getStartId() {
		return startId;
	}

	public void setStartId(Long startId) {
		this.startId = startId;
	}
}
