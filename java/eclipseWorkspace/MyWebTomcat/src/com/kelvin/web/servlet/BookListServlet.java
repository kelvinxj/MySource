package com.kelvin.web.servlet;

import java.io.IOException;
import java.util.List;



//import javax.inject.Inject;
//import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bookstore.Book;
import com.bookstore.BookRepository;
import com.bookstore.BookRepositoryJDBCImpl;

/**
 * Servlet implementation class BookListServlet
 */
@WebServlet("/book/")
public class BookListServlet extends HttpServlet {
	
	private BookRepository bookRepo;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BookListServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		bookRepo = new BookRepositoryJDBCImpl();
		String field = request.getParameter("field");
		String order = request.getParameter("order");
		List<Book> books = null;
		
		if ("asc".equals(order) && "title".equals(field)) {
			books = bookRepo.listBooksSortByTitleAsc();
		} else if ("desc".equals(order) && "title".equals(field)) {
			books = bookRepo.listBooksSortByTitleDesc();
		} else if ("asc".equals(order) && "price".equals(field)) {
			books = bookRepo.listBooksSortByPriceAsc();
		} else if ("desc".equals(order) && "price".equals(field)) {
			books = bookRepo.listBooksSortByPriceDesc();
		} else if ("asc".equals(order) && "pubDate".equals(field)) {
			books = bookRepo.listBooksSortByPubDateAsc();
		} else if ("desc".equals(order) && "pubDate".equals(field)) {
			books = bookRepo.listBooksSortByPubDateDesc();
		} else {
			books = bookRepo.listBooks();			
		}
		
		request.setAttribute("books", books);
		getServletContext().getRequestDispatcher("/WEB-INF/pages/book-list.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		bookRepo = new BookRepositoryJDBCImpl();
		String titleSearch = request.getParameter("titleSearch");
		List<Book> books = null;

		if(titleSearch != null && !titleSearch.isEmpty()){
			books = bookRepo.searchByTitle(titleSearch);
		}
		else{
			books = bookRepo.listBooks();
		}
		request.setAttribute("books", books);
		getServletContext().getRequestDispatcher("/WEB-INF/pages/book-list.jsp").forward(request, response);
	}
}
