package application;

import java.util.Date;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class Program {

	public static void main(String[] args) {
		
		Department obj = new Department(1,"Books");
		
		Seller seller =  new Seller(21, "bob", "bob@gmail.com", new Date(0), 300.00, obj );
		
		SellerDao sellerdao = DaoFactory.createSellerdao();  // injeção de dependencia
		
		
		System.out.println(seller);
	}

}
