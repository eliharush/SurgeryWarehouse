package il.swhm.shared.entities.order;

import il.swhm.shared.entities.product.Product;

public class PushOrderLine {
	private Product product;
	private double orderQty;
	private boolean swhmProduct;
	
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public double getOrderQty() {
		return orderQty;
	}
	public void setOrderQty(double orderQty) {
		this.orderQty = orderQty;
	}
	
	public void setProductRunningId(int id){
		//for the mybatis id
	}
	public boolean isswhmProduct() {
		return swhmProduct;
	}
	public void setswhmProduct(boolean swhmProduct) {
		this.swhmProduct = swhmProduct;
	}

	
	
	
}
