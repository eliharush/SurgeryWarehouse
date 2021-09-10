package il.swhm.data.dao.interfaces;

import il.swhm.shared.entities.Approver;
import il.swhm.shared.entities.Customer;
import il.swhm.shared.entities.product.CustomerProduct;
import il.swhm.shared.entities.product.ManProduct;
import il.swhm.shared.entities.product.MaraManProduct;
import il.swhm.shared.entities.product.SubProduct;

import java.util.Date;
import java.util.List;

public interface IImportDao {
	public void setLastLoadTime();
	public Date getLastLoadTime();
	public void insertCustomers(List<Customer> customers) throws Exception;
	public void insertProducts(List<CustomerProduct> customerProducts) throws Exception;
	public void insertApprovers(List<Approver> approvers);
	public void insertManProducts(List<ManProduct> manProducts) throws Exception;
	public void insertSubProducts(List<SubProduct> subProducts);
	public void insertMaraManProducts(List<MaraManProduct> manProducts) throws Exception;
}
