package il.swhm.data.dao.impl;

import il.swhm.data.MyBatisUtil;
import il.swhm.data.dao.interfaces.IImportDao;
import il.swhm.data.mappers.ImportMapper;
import il.swhm.shared.entities.Approver;
import il.swhm.shared.entities.Customer;
import il.swhm.shared.entities.product.CustomerProduct;
import il.swhm.shared.entities.product.ManProduct;
import il.swhm.shared.entities.product.MaraManProduct;
import il.swhm.shared.entities.product.SubProduct;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

public class ImportDao implements IImportDao {
	private static Logger logger=Logger.getLogger(ImportDao.class);
	
	@Override
	public Date getLastLoadTime() {
		SqlSession session=null;
		try{
			session=MyBatisUtil.getInstance().openSession();
			ImportMapper im=session.getMapper(ImportMapper.class);
			return im.selectLastLoadTime();
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
	}

	public void setLastLoadTime(){
		SqlSession session=null;
		try{
			session=MyBatisUtil.getInstance().openSession();
			ImportMapper im=session.getMapper(ImportMapper.class);
			im.updateLastLoadTime();
			session.commit();
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
	}


	@Override
	public void insertCustomers(List<Customer> customers) throws Exception {
		SqlSession session=null;
		try{
			session=MyBatisUtil.getInstance().openSession();
			ImportMapper im=session.getMapper(ImportMapper.class);
			im.deleteCustomers();
			for(Customer customer:customers){
				im.insertCustomer(customer);
			}
			session.commit();
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
	}

	@Override
	public void insertProducts(List<CustomerProduct> customerProducts)
			throws Exception {
		SqlSession session=null;
		try{
			session=MyBatisUtil.getInstance().openSession();
			ImportMapper im=session.getMapper(ImportMapper.class);
			im.deleteProducts();
			for(CustomerProduct product:customerProducts){
				im.insertProduct(product);
			}
			session.commit();
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
	}

	@Override
	public void insertApprovers(List<Approver> approvers) {
		SqlSession session=null;
		try{
			session=MyBatisUtil.getInstance().openSession();
			ImportMapper im=session.getMapper(ImportMapper.class);
			im.deleteApprovers();
			for(Approver approver:approvers){
				im.insertApprover(approver);
			}
			session.commit();
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
	}

	@Override
	public void insertManProducts(List<ManProduct> manProducts)
			throws Exception {
		SqlSession session=null;
		try{
			session=MyBatisUtil.getInstance().openSession();
			ImportMapper im=session.getMapper(ImportMapper.class);
			im.deleteManProducts();
			for(ManProduct manProduct:manProducts){
				try{
					im.insertManProduct(manProduct);
					logger.info("a manufactor product was found manProductId["+manProduct.getManProductId()+"] customer product id ["+manProduct.getCustProductId()+"] swhm product id ["+manProduct.getProductId()+"].OK.");
				}
				catch (Exception e) {
					//ignore
					logger.warn("a duplicate manufactor product was found manProductId["+manProduct.getManProductId()+"] customer product id ["+manProduct.getCustProductId()+"] swhm product id ["+manProduct.getProductId()+"]. it will be dumped");
				}
			}
			session.commit();
		}
		finally{
			if(session!=null){
				session.close();
			}
		}

	}

	@Override
	public void insertSubProducts(List<SubProduct> subProducts) {
		SqlSession session=null;
		try{
			session=MyBatisUtil.getInstance().openSession();
			ImportMapper im=session.getMapper(ImportMapper.class);
			im.deleteSubProducts();
			for(SubProduct subProduct:subProducts){
				im.insertSubProduct(subProduct);
			}
			session.commit();
		}
		finally{
			if(session!=null){
				session.close();
			}
		}

		
	}

	@Override
	public void insertMaraManProducts(List<MaraManProduct> manProducts)
			throws Exception {
		SqlSession session=null;
		try{
			session=MyBatisUtil.getInstance().openSession();
			ImportMapper im=session.getMapper(ImportMapper.class);
			im.deleteMaraManProducts();
			for(MaraManProduct manProduct:manProducts){
				try{
					im.insertMaraManProduct(manProduct);
					logger.info("a mara manufactor product was found manProductId["+manProduct.getManProductId()+"]  swhm product id ["+manProduct.getProductId()+"].OK.");
				}
				catch (Exception e) {
					//ignore
					logger.warn("a duplicate mara manufactor product was found manProductId["+manProduct.getManProductId()+"] swhm product id ["+manProduct.getProductId()+"]. it will be dumped");
				}
			}
			session.commit();
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
		
	}



}
