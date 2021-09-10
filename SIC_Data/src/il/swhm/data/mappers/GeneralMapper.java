package il.swhm.data.mappers;

import il.swhm.shared.entities.Approver;
import il.swhm.shared.entities.Customer;
import il.swhm.shared.entities.product.ManProduct;
import il.swhm.shared.entities.product.MaraManProduct;
import il.swhm.shared.entities.product.Product;
import il.swhm.shared.entities.transmit.Transmit;
import il.swhm.shared.enums.EntityType;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface GeneralMapper {
	public List<Customer> selectCustomer(@Param("stationId") int stationId,@Param("hospitalId") String hospitalId,@Param("customerId") String customerId);
	public List<Product> selectProductsForCustomer(@Param("hospitalId") String hospitalId,@Param("customerId") String customerId);
	public List<Product> selectProductsForHospital(@Param("hospitalId") String hospitalId,@Param("productId") String productId);
	public List<ManProduct> selectManProductsForCustomer(@Param("hospitalId") String hospitalId,@Param("customerId") String customerId);
	public int insertTransmit(@Param("stationId") int stationId,@Param("transmit") Transmit transmit);
	public void updateTransmit(@Param("stationId") int stationId,@Param("transmit") Transmit transmit);
	public void insertTransmitLine(@Param("stationId") int stationId,@Param("transmitId") int transmitId,@Param("entityId") int entityId,@Param("entityType") EntityType entityType);
	
	public List<Approver> selectApprovers();
	public void updateTransmitToMaster(@Param("stationId") int stationId,@Param("transmitId") int transmitId);
	public String selectProductIdbyManProduct(@Param("productId") String productId);
	public List<Product> selectGeneralProductByCustProductId(@Param("custProductId") String custProductId);
	public List<MaraManProduct> selectMaraProducts(@Param("hospitalId") String hospitalId,@Param("customerId") String customerId);
}
