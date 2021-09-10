package il.swhm.shared.entities.transmit;

public class OrderLine {
	private String customerId;
	private int productRunningId;
	private String productId;
	private double salesQty;
	private String salesUnit;
	private String docType;
	private String prodCategory;
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public int getProductRunningId() {
		return productRunningId;
	}
	public void setProductRunningId(int productRunningId) {
		this.productRunningId = productRunningId;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public double getSalesQty() {
		return salesQty;
	}
	public void setSalesQty(double salesQty) {
		this.salesQty = salesQty;
	}
	public String getSalesUnit() {
		return salesUnit;
	}
	public void setSalesUnit(String salesUnit) {
		this.salesUnit = salesUnit;
	}
	public String getDocType() {
		return docType;
	}
	public void setDocType(String docType) {
		this.docType = docType;
	}
	public String getProdCategory() {
		return prodCategory;
	}
	public void setProdCategory(String prodCategory) {
		this.prodCategory = prodCategory;
	}
	
	
}
