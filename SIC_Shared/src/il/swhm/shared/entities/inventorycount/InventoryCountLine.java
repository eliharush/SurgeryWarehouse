package il.swhm.shared.entities.inventorycount;

import il.swhm.shared.entities.product.Product;

import java.util.Date;

public class InventoryCountLine {
	private Product product;
	private double countQty;
	private double orderQty;
	private Date countTime;
	
	
	public double getCountQty() {
		return countQty;
	}
	public void setCountQty(double countQty) {
		this.countQty = countQty;
	}

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
	public Date getCountTime() {
		return countTime;
	}
	public void setCountTime(Date countTime) {
		this.countTime = countTime;
	}
	
	
	
	
}
