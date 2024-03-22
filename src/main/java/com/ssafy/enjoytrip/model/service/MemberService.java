package com.ssafy.enjoytrip.model.service;

import com.ssafy.enjoytrip.dto.Member;

public interface MemberService {
	public void register(Member m) throws Exception;
	public void myPage() throws Exception;
	public void login(String id, String pw) throws Exception;
	public void modifyMember(String id) throws Exception;
	public void deleteMember(String id) throws Exception;
}
