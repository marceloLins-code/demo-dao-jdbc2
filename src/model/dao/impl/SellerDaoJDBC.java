package model.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mysql.jdbc.Connection;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {

private Connection conn;

public SellerDaoJDBC() {}
	
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Seller obj) {
		
		
	}

	@Override
	public void update(Seller obj) {
		
		
	}

	@Override
	public void deletById(Integer id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE seller.Id = ?"	);
			
			st.setInt(1,id);		
			rs = st.executeQuery();
			
			// INSTANCIANDO A MEMORIA
			if (rs.next()) {
				Department dep = instantiateDepartment(rs);
				
				Seller obj = instantiate(rs,dep)
;				return obj;
			}	
			return null;
			
		} 
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		} 
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
		
	}

	private Seller instantiate(ResultSet rs, Department dep) throws SQLException {
		Seller obj = new Seller();
		obj.setId(rs.getInt("Id"));
		obj.setName(rs.getNString("Name"));
		obj.setEmail(rs.getString("Email"));
		obj.setBaseSalary(rs.getDouble("BaseSalary"));
		obj.setBirthdate(rs.getDate("BirthDate"));
		obj.setDep(dep);
		return obj;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep =new Department(); 
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));
		return dep;
	}

	@Override
	public List<Seller> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName " 
					+ "FROM seller INNER JOIN department " 
					+ "ON seller.DepartmentId = department.Id "
					+ " WHERE DepartmentId = ? "
					+ "ORDER BY Name");
					
					st.setInt(1,department.getId());		
			
					rs = st.executeQuery();
			//como s�o varios deve se criar uma lista de resulatados. a assinatura do m�todo retorna uma lista
			List<Seller> list = new ArrayList();
			
			// USANDO MAP PARA QUE OS DEPARTAMENTOS N�O SE REPITAM  AULA 248 7:40
			Map<Integer, Department> map = new HashMap<>(); 
			// INSTANCIANDO A MEMORIA DO RETORNO DO BANCO PARA JAVA OO
			while (rs.next()) {
				
				Department dep = map.get(rs.getInt("DepartmentId"));
				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}
				// APAGA SE ESTA INSTANCIA��O POIS JA ESTA SENDO FEITA DENTRO DO IF ACIMA
				//Department dep = instantiateDepartment(rs);
				
				Seller obj = instantiate(rs,dep)
;				list.add(obj);

			}	
			return list;
			
		} 
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		} 
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
		
	
	}

}
																							