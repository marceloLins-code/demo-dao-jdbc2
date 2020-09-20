package model.dao.impl;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

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
		
		PreparedStatement st = null;
		try {
			st = conn.clientPrepareStatement(
					"INSERT INTO seller "
					+ "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthdate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDep().getId());
			
			int roesAffected = st.executeUpdate();
			
			if (roesAffected >0) {
				ResultSet rs = st.getGeneratedKeys();
						if (rs.next()) {
							int id = rs.getInt(1);
							obj.setId(id );
							
						}
						DB.closeResultSet(rs);
			}
			else {
				throw new DbException("unexpected error! no rows affected" );

			}
			
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
				}
		
		
	}

	@Override
	public void update(Seller obj) {
		PreparedStatement st = null;
		try {
			st = conn.clientPrepareStatement(
					"UPDATE seller " 
					+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? " 
					+ "WHERE Id = ?");
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthdate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDep().getId());
			st.setInt(6, obj.getId());
			
			st.executeUpdate();
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
				}
		
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
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName "  //TODOS OS DADOS DO VEDEDOR MAIS O DEP
					+ "FROM seller INNER JOIN department " 
					+ "ON seller.DepartmentId = department.Id "
					+ "ORDER BY Name");
					
			
					rs = st.executeQuery();
			//como são varios deve se criar uma lista de resulatados. a assinatura do método retorna uma lista
			List<Seller> list = new ArrayList();
			
			// USANDO MAP PARA QUE OS DEPARTAMENTOS NÃO SE REPITAM  AULA 248 7:40
			Map<Integer, Department> map = new HashMap<>(); 
			// INSTANCIANDO A MEMORIA DO RETORNO DO BANCO PARA JAVA OO
			while (rs.next()) {
				
				Department dep = map.get(rs.getInt("DepartmentId"));
				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}
				// APAGA SE ESTA INSTANCIAÇÃO POIS JA ESTA SENDO FEITA DENTRO DO IF ACIMA
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

	@Override
	// BUSCAR VENDEDORES DADO UM DEPARTAMENTO
	public List<Seller> findByDepartment(Department department) {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName "  //TODOS OS DADOS DO NEDEDOR MAIS O SEP
					+ "FROM seller INNER JOIN department " 
					+ "ON seller.DepartmentId = department.Id "
					+ " WHERE DepartmentId = ? " // ONDE O DepartmentId FOR IGUAL UM DADO VALOR 
					+ "ORDER BY Name");
					
					st.setInt(1,department.getId());		
			
					rs = st.executeQuery();
			//como são varios deve se criar uma lista de resulatados. a assinatura do método retorna uma lista
			List<Seller> list = new ArrayList();
			
			// USANDO MAP PARA QUE OS DEPARTAMENTOS NÃO SE REPITAM  AULA 248 7:40
			Map<Integer, Department> map = new HashMap<>(); 
			// INSTANCIANDO A MEMORIA DO RETORNO DO BANCO PARA JAVA OO
			while (rs.next()) {
				
				Department dep = map.get(rs.getInt("DepartmentId"));
				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}
				// APAGA SE ESTA INSTANCIAÇÃO POIS JA ESTA SENDO FEITA DENTRO DO IF ACIMA
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
																							