package il.swhm.data.dao.interfaces;

import il.swhm.shared.entities.Approver;
import il.swhm.shared.entities.Customer;
import il.swhm.shared.entities.HistoryRecord;
import il.swhm.shared.entities.Transmitable;
import il.swhm.shared.entities.product.ManProduct;
import il.swhm.shared.entities.product.MaraManProduct;
import il.swhm.shared.entities.product.Product;
import il.swhm.shared.enums.EntityType;

import java.util.List;
import java.util.Map;

public interface IGeneralDao {
	public List<Customer> getCustomer(int stationId,String hospitalId,String customerId);
	public List<Product> getCustomerProducts(String hospitalId,String customerId);
	public List<Product> getHospitalProducts(String hospitalId,String customerId);
	public List<ManProduct> getCustomerManProducts(String hospitalId,String customerId);
	public List<MaraManProduct> getMaraManProducts(String hospitalId,String customerId);
	public boolean transmitCounts(int stationId,Map<Integer,EntityType> entities,String user);
	public List<HistoryRecord> getHistory();
	public List<Approver> getAllApprovers();
	public void cleanDb(int stationId,int period);
	public Product selectGeneralProductForCustProductId(String custProductId);
	public List<Transmitable> getEntitiesForTransmit(int stationId);
}
