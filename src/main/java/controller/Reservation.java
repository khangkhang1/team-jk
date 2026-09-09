package controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.CommonExecute;
import common.reservation.ReservationMap;
import common.stepPay.stepPay;


/**
 * Servlet implementation class reservation
 */
@WebServlet("/Reservation")
public class Reservation extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Reservation() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8");
		String gubun = request.getParameter("t_gubun");
		if(gubun == null) gubun ="ReservationMap";
		String viewPage ="";
		
		if(gubun.equals("ReservationMap")) {
			CommonExecute memMap = new ReservationMap();
			CommonExecute memPay = new stepPay();
			memMap.execute(request);
			memPay.execute(request);
			viewPage ="reservation/reservation.jsp";
			
		}else if(gubun.equals("stepPay")) {
			CommonExecute mem = new stepPay();
			mem.execute(request);
			
			
			viewPage ="reservation/reservation.jsp";
			
			
		}
		
		
		RequestDispatcher rd = request.getRequestDispatcher(viewPage);
		rd.forward(request, response);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
