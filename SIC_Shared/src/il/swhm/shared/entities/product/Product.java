package il.swhm.shared.entities.product;

import java.util.List;


public class Product {
	private int id;
	private String productId;
	private String custProductId;
	private String custProductDesc;
	private String salesUnit;
	private String salesUnitDesc;
	private String customerUnit;
	private String customerUnitDesc;
	private double factor;
	private String category;
	private double stdQuantity;
	private int lot1;
	private int lot2;
	private int lot3;
	private String site;
	private String productType;
	private String mitadef;
	private String docType;
	private String commentCode;
	private String comment;
	private int countFactor;
	private double minimalOrderCount;
	private List<String> subIds;
	private String alternative;
	private String custProductDesc2;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getCustProductId() {
		return custProductId;
	}
	public void setCustProductId(String custProductId) {
		this.custProductId = custProductId;
	}
	public String getCustProductDesc() {
		return custProductDesc;
	}
	public void setCustProductDesc(String customerProdDesc) {
		this.custProductDesc = customerProdDesc;
	}
	public String getSalesUnit() {
		return salesUnit;
	}
	public void setSalesUnit(String salesUnit) {
		this.salesUnit = salesUnit;
	}
	public String getCustomerUnit() {
		return customerUnit;
	}
	public void setCustomerUnit(String customerUnit) {
		this.customerUnit = customerUnit;
	}
	public double getFactor() {
		return factor;
	}
	public void setFactor(double factor) {
		this.factor = factor;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public double getStdQuantity() {
		return stdQuantity;
	}
	public void setStdQuantity(double stdQuantity) {
		this.stdQuantity = stdQuantity;
	}
	public int getLot1() {
		return lot1;
	}
	public void setLot1(int lot1) {
		this.lot1 = lot1;
	}
	public int getLot2() {
		return lot2;
	}
	public void setLot2(int lot2) {
		this.lot2 = lot2;
	}
	public int getLot3() {
		return lot3;
	}
	public void setLot3(int lot3) {
		this.lot3 = lot3;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public String getMitadef() {
		return mitadef;
	}
	public void setMitadef(String mitadef) {
		this.mitadef = mitadef;
	}
	public String getDocType() {
		return docType;
	}
	public void setDocType(String docType) {
		this.docType = docType;
	}
	public String getSalesUnitDesc() {
		return salesUnitDesc;
	}
	public void setSalesUnitDesc(String salesUnitDesc) {
		this.salesUnitDesc = salesUnitDesc;
	}
	public String getCustomerUnitDesc() {
		return customerUnitDesc;
	}
	public void setCustomerUnitDesc(String customerUnitDesc) {
		this.customerUnitDesc = customerUnitDesc;
	}
	public String getCommentCode() {
		return commentCode;
	}
	public void setCommentCode(String commentCode) {
		this.commentCode = commentCode;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public int getCountFactor() {
		return countFactor;
	}
	public void setCountFactor(int countFactor) {
		this.countFactor = countFactor;
	}
	public double getMinimalOrderCount() {
		return minimalOrderCount;
	}
	public void setMinimalOrderCount(Double minimalOrderCount) {
		this.minimalOrderCount = minimalOrderCount;
	}
	public List<String> getSubIds() {
		return subIds;
	}
	public void setSubIds(List<String> subIds) {
		this.subIds = subIds;
	}
	public String getAlternative() {
		return alternative;
	}
	public void setAlternative(String alternative) {
		this.alternative = alternative;
	}
	public String getCustProductDesc2() {
		return custProductDesc2;
	}
	public void setCustProductDesc2(String custProductDesc2) {
		this.custProductDesc2 = custProductDesc2;
	}
	
	

}
