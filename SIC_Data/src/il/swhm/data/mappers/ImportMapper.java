package il.swhm.data.mappers;

import java.util.Date;

import il.swhm.shared.entities.Approver;
import il.swhm.shared.entities.Customer;
import il.swhm.shared.entities.product.CustomerProduct;
import il.swhm.shared.entities.product.ManProduct;
import il.swhm.shared.entities.product.MaraManProduct;
import il.swhm.shared.entities.product.SubProduct;

import org.apache.ibatis.annotations.Param;

public interface ImportMapper {
	public Date selectLastLoadTime();
	public void updateLastLoadTime();
	public void insertCustomer(@Param("customer") Customer customer);
	public void insertProduct(@Param("custProduct") CustomerProduct custProduct);
	public void insertApprover(@Param("approver") Approver approver);
	public void deleteCustomers();
	public void deleteProducts();
	public void deleteApprovers();
	public void insertManProduct(@Param("manProduct") ManProduct manProduct);
	public void deleteManProducts();
	public void deleteSubProducts();
	public void insertSubProduct(@Param("subProduct") SubProduct subProduct);
	public void insertMaraManProduct(@Param("maraManProduct") MaraManProduct maraManProduct);
	public void deleteMaraManProducts();
}
