package com.yl.webService;

import javax.jws.WebService;
import javax.ws.rs.Produces;
import com.yl.beans.Userinfo;
import javax.ws.rs.core.MediaType;

@WebService
@Produces(MediaType.APPLICATION_JSON)
public interface ScenicWebService {
	// 城市所包含景区列表
	public String getscenicList(String cityName);

	// 主页所有信息
	public String getallinfo(String scenicId);

	public String getscenicDetails();

	public String getattractsList();

	public String getattractsDetail();

	public String gettravelNotesList();

	public String gettravelNotesDetails();

}
