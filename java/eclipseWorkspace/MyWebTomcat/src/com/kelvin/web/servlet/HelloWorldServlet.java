package com.kelvin.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/hello/")
public class HelloWorldServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HelloWorldServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//This is the entry point of a servlet.
		//in this function, the request type will be detected(get or post), then the doGet()
		//or doPost() method of subclass will be called
		System.out.println("service() method called, method:" + request.getMethod());
		
		//if you don't call super.service, doGet/doPost will not be executed.
		PrintWriter writer = response.getWriter();
		writer.println("service() called.");
		super.service(request, response);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter writer = response.getWriter();
		writer.print("This page was requested by GET method<br/>");
		writer.print("<input type='button' value='click me'/>");
		writer.close();
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		System.out.println("destroy() called at: " + new Date());
		super.destroy();
	}

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		System.out.println("init() called at: " + new Date());
		super.init();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter writer = response.getWriter();
		writer.print("This page was requested by Post method");
		writer.close();
	}
}
