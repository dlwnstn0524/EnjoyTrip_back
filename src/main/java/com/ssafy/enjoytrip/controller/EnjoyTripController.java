package com.ssafy.enjoytrip.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import com.ssafy.enjoytrip.dto.Attraction;
import com.ssafy.enjoytrip.dto.Member;
import com.ssafy.enjoytrip.exception.AuthenticationException;
import com.ssafy.enjoytrip.model.dao.MemberDao;
import com.ssafy.enjoytrip.model.dao.MemberDaoImpl;
import com.ssafy.enjoytrip.model.service.MemberService;
import com.ssafy.enjoytrip.model.service.MemberServiceImpl;
import com.ssafy.enjoytrip.model.service.TripService;
import com.ssafy.enjoytrip.model.service.TripServiceImpl;

@WebServlet("/enjoytrip")
public class EnjoyTripController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private MemberService mSer;
	private MemberDao mDao;
	private TripService tSer;
	
    public EnjoyTripController() {
        super();
    }

	@Override
	public void init() throws ServletException {
		super.init();
		mSer = MemberServiceImpl.getInstance();
		mDao = MemberDaoImpl.getInstance();
		tSer = TripServiceImpl.getInstance();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		service(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		service(request, response);
	}


	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		String url = "";
		try {
			if (action.equals("mvregister")) {
				url = "/join.jsp";
			} else if (action.equals("register")) {
				url = register(request, response);
			} else if (action.equals("login")) {
				url = login(request, response);
			} else if (action.equals("logout")) {
				url = "";
			} else if (action.equals("modify")) {
				url = "";
			} else if (action.equals("delete")) {
				url = "";
			} else if (action.equals("mypage")) {
				url = "";
			} else if (action.equals("init")) {
				url = "/index.jsp";
			} else if (action.equals("local")) {
				url = local(request, response);
			} else if (action.equals("tripSearch")) {
				url = tripSearch(request, response);
			}
		} catch (SQLIntegrityConstraintViolationException e) {
			e.printStackTrace();
			request.setAttribute("msg", "중복된 아이디 입니다. 다시 시도하세요.");
			// 
			url = "error/error.jsp";
		} catch(Exception e) {
			e.printStackTrace();
			//error page 처리
			request.setAttribute("msg", "에러에요 ;ㅁ;");
			url = "error/error.jsp";
		}
		if (url.startsWith("redirect")) {
			url = url.substring(url.indexOf(":")+1);
			response.sendRedirect(request.getContextPath()+url);		
		} else {
			request.getRequestDispatcher(url).forward(request, response);	
		}
	}
	
	private String tripSearch(HttpServletRequest request, HttpServletResponse response) {
		
		int sidoCode = Integer.parseInt(request.getParameter("sidoCode"));
		int contentTypeId = Integer.parseInt(request.getParameter("contentTypeId"));
		String keyword = request.getParameter("keyword");
		Attraction dto = new Attraction(contentTypeId, keyword, sidoCode);
		List<Attraction> list = tSer.tripSearch(dto);
		System.out.println(list);
		return "/enjoytrip?action=index.jsp";
	}

	private String local(HttpServletRequest request, HttpServletResponse response) {
		List<Attraction> allSido = tSer.getAllSido();
		request.setAttribute("dto", allSido);
		return "/publicData.jsp";
	}

	private String register(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		String pw = request.getParameter("pw");
		String email = request.getParameter("email");
		System.out.println(id + " " + pw + " " + email);
		mSer.register(new Member(id, pw, email));
		// register 주소 매핑
		// 이후 마이페이지로 수정?
		return "/enjoytrip?action=index.jsp";
	}
	
	private String login(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		String pw = request.getParameter("pw");	
		try {
			mSer.login(id, pw);
			HttpSession session = request.getSession();
			session.setAttribute("login", id);
		} catch (AuthenticationException e) {
			return "redirect:/enjoytrip?action=loginFailed";
		}
		return "redirect:/enjoytrip?action=init";
	}
}
