package com.bookstore;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
//import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;

//@ApplicationScoped
@JDBC
public class BookRepositoryJDBCImpl implements BookRepository {

	@Resource(name="jdbc/bookstore")
	private DataSource datasource;
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	
	@Override
	public Book lookupBookById(final String id){
		return withDB(new JDBCRunner<Book>(){

			@Override
			public Book run(Connection connection) throws Exception {
				PreparedStatement ps = connection.prepareStatement("select * from book where id=?");
				ps.setLong(1, Long.parseLong(id));
				ResultSet rs = ps.executeQuery();
				if(rs.next()){
					Book book = new Book();
					book.setId("" + rs.getLong("id"));
					book.setPrice(rs.getBigDecimal("price"));
					book.setPubDate(rs.getDate("pubDate"));
					book.setTitle(rs.getString("title"));
					book.setDescription(rs.getString("description"));
					return book;
				}
				else
					return null;
			}});
	}

	@Override
	public void addBook(final String title, final String description, final String price,
			final String pubDate){
		withDB(new JDBCRunner<Book>(){

			@Override
			public Book run(Connection connection) throws Exception {
				PreparedStatement ps = connection.prepareStatement("insert into book (title, description, price, pubdate) values (?,?,?,?)");
				ps.setString(1, title);
				ps.setString(2, description);
				ps.setBigDecimal(3, new BigDecimal(price));
				
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(dateFormat.parse(pubDate));
				ps.setDate(4, new Date(calendar.getTimeInMillis()));
				
				int rowCount = ps.executeUpdate();
				if(rowCount != 1){
					//throw new exception;
					throw new Exception("Unable to insert book into bookstore");
				}
				return null;
			}});
	}

	@Override
	public void updateBook(final String id, final String title, final String description,
			final String price, final String pubDate){
		withDB(new JDBCRunner<Book>(){
			@Override
			public Book run(Connection connection) throws Exception {
				
				PreparedStatement prepareStatement = connection.prepareStatement("update book set  title=?, description=?, price=?, pubdate=? where id = ?");
				prepareStatement.setString(1, title);
				prepareStatement.setString(2, description);
				prepareStatement.setBigDecimal(3, new BigDecimal(price));
				
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(dateFormat.parse(pubDate));
				prepareStatement.setDate(4, new Date(calendar.getTimeInMillis()));
				
				prepareStatement.setLong(5, Long.parseLong(id));
				
				int rowCount = prepareStatement.executeUpdate();
				if (rowCount!=1) {
					throw new Exception("Unable to update book into bookstore");
				}
				return null;
			}});
	}

	@Override
	public void removeBook(final String id){withDB(new JDBCRunner<Book>(){
		@Override
		public Book run(Connection connection) throws Exception{
			
			PreparedStatement prepareStatement = connection.prepareStatement("delete from book where id = ?");
			prepareStatement.setLong(1, Long.parseLong(id));
			
			int rowCount = prepareStatement.executeUpdate();
			if (rowCount!=1) {
				throw new Exception("Unable to remove book from bookstore");
			}
			return null;
		}});
	}

	@Override
	public List<Book> listBooks(){
		return doList(null);
	}
	
	@Override
	public List<Book> listBooksSortByPriceAsc(){
		return doList("price ASC");
	}

	@Override
	public List<Book> listBooksSortByTitleAsc(){
		return doList("title ASC");
	}

	@Override
	public List<Book> listBooksSortByTitleDesc(){
		return doList("title DESC");
	}

	@Override
	public List<Book> listBooksSortByPriceDesc(){
		return doList("price DESC");
	}

	@Override
	public List<Book> listBooksSortByPubDateDesc(){
		return doList("pubDate DESC");
	}

	@Override
	public List<Book> listBooksSortByPubDateAsc(){
		return doList("pubDate ASC");
	}
	
	static interface JDBCRunner<T>{
		T run(Connection connection) throws Exception;
	}
	
	private List<Book> doList(final String orderBy){
		return withDB(new JDBCRunner<List<Book>>(){
			@Override
			public List<Book> run(Connection connection) throws SQLException{
				List<Book> listing = new ArrayList<Book>();				
				Statement statement = connection.createStatement();
				final String query = "select * from book" + 
						(orderBy != null ? " ORDER BY " + orderBy + ";" : ";");
				ResultSet rs = statement.executeQuery(query);
				while (rs.next()) {
					Book book = new Book();
					book.setId("" + rs.getLong("id"));
					book.setPrice(rs.getBigDecimal("price"));
					book.setPubDate(rs.getDate("pubdate"));
					book.setTitle(rs.getString("title"));
					book.setDescription(rs.getString("description"));
					listing.add(book);
				} 
				return listing;
			}});		
	}
	
	private <T> T withDB(JDBCRunner<T> runner){
		Connection connection = null;
		try{
			datasource = GetDateSource();
			connection = datasource.getConnection();
			boolean auto = connection.getAutoCommit();
			connection.setAutoCommit(false);
			
			T result = runner.run(connection);
			connection.commit();
			connection.setAutoCommit(auto);
			return result;
		}
		catch(Exception ex){
			try{
				connection.rollback();
			}
			catch(SQLException e){
				//should log this as warn or info
			}
			//throw new BookStoreException(ex);
		}
		finally{
			if(connection != null){
				try{
					connection.close();
				}
				catch(Exception ex){
					//should log this as warn or info
				}
			}
		}
		return null;
	}

	private DataSource GetDateSource() throws NamingException {
		// TODO Auto-generated method stub
		Context ctx = new InitialContext();
		DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/bookstore");
		return ds;
	}

	@Override
	public List<Book> searchByTitle(final String title) {
		return withDB(new JDBCRunner<List<Book>>(){

			@Override
			public List<Book> run(Connection connection) throws Exception {
				List<Book> listing = new ArrayList<Book>();				
				PreparedStatement statement = connection.prepareStatement("select * from book where title like ?");
				statement.setString(1, "%" + title + "%");
				ResultSet rs = statement.executeQuery();
				while(rs.next()){
					Book book = new Book();
					book.setId("" + rs.getLong("id"));
					book.setPrice(rs.getBigDecimal("price"));
					book.setPubDate(rs.getDate("pubDate"));
					book.setTitle(rs.getString("title"));
					book.setDescription(rs.getString("description"));
					listing.add(book);
				}
				return listing;
			}
			
		});
	}
}
