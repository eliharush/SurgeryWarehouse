package il.swhm.shared.entities.product;

import il.swhm.shared.entities.Customer;

public class CustomerProduct {
	Customer customer;
	Product product;
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	
	
}
