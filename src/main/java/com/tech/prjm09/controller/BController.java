package com.tech.prjm09.controller;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.tech.prjm09.dao.IDao;
import com.tech.prjm09.dto.BDto;
import com.tech.prjm09.util.SearchVO;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class BController {
	
	private final IDao iDao;
	
	@Autowired
	public BController(IDao iDao) {
		this.iDao = iDao;
	}
	
	@GetMapping("/")
	public String index() {
		return "index";
	}

	
	@RequestMapping("list")
	public String list(HttpServletRequest request, SearchVO searchVO, Model model) {
		System.out.println("list() ctr");
		// searching
		String btitle = "";
		String bcontent = "";
		
		String[] brdTitle = request.getParameterValues("searchType");
		if (brdTitle != null) {
			for (int i = 0; i < brdTitle.length; i++) {
				System.out.println("brdTitle: " + brdTitle[i]);
			}			
		}
		
		
		if (brdTitle != null) {
			for (String value : brdTitle) {
				if (value.equals("btitle")) {
					model.addAttribute("btitle", "true");
					btitle = "btitle";
				}
				
				if (value.equals("bcontent")) {
					model.addAttribute("bcontent", "true");
					bcontent = "bcontent";
				}
			}			
		}
		
		String searchKeyword = request.getParameter("sk");
		if (searchKeyword == null) {
			searchKeyword = "";
		}
		model.addAttribute("searchKeyword", searchKeyword);
		// ---------------------------------------
		// 전체글 개수 변형
		int total = 0;
		
		if (btitle.equals("btitle") && bcontent.equals("")) {
			total = iDao.selectBoardCount(searchKeyword, "1");
			System.out.println("total11111111111");
		} else if (btitle.equals("") && bcontent.equals("bcontent")) {
			total = iDao.selectBoardCount(searchKeyword, "2");
			System.out.println("total22222222222");
		} else if (btitle.equals("btitle") && bcontent.equals("bcontent")) {
			total = iDao.selectBoardCount(searchKeyword, "3");
			System.out.println("total33333333333");
		} else if (btitle.equals("") && bcontent.equals("")) {
			total = iDao.selectBoardCount(searchKeyword, "4");
			System.out.println("total44444444444");
		}
		
		
		
		// 글의 총 개수
//		int total = iDao.selectBoardCount();
		
		System.out.println("total: " + total);
		searchVO.pageCalculate(total);
		
		// paging
		String strPage = request.getParameter("page");
		// null 검사
		if (strPage == null) {
			strPage = "1";
		}
		
		int page = Integer.parseInt(strPage);
		searchVO.setPage(page);
	
		System.out.println("total: " + total);
		System.out.println("click page: " + strPage);
		System.out.println("pageStart: " + searchVO.getPageStart());
		System.out.println("pageEnd: " + searchVO.getPageEnd());
		System.out.println("pageTotal: " + searchVO.getTotPage());
		System.out.println("rowStart: " + searchVO.getRowStart());
		System.out.println("rosEnd: " + searchVO.getRowEnd());
		
		int rowStart = searchVO.getRowStart();
		int rowEnd = searchVO.getRowEnd();
		
//		ArrayList<BDto> list = null;
		if (btitle.equals("btitle") && bcontent.equals("")) {
//			total = iDao.selectBoardCount(searchKeyword, "1");
			model.addAttribute("list", iDao.list(rowStart, rowEnd, searchKeyword, "1"));
			System.out.println("total11111111111");
		} else if (btitle.equals("") && bcontent.equals("bcontent")) {
			model.addAttribute("list", iDao.list(rowStart, rowEnd, searchKeyword, "2"));
			System.out.println("total22222222222");
		} else if (btitle.equals("btitle") && bcontent.equals("bcontent")) {
			model.addAttribute("list", iDao.list(rowStart, rowEnd, searchKeyword, "3"));
			System.out.println("total33333333333");
		} else if (btitle.equals("") && bcontent.equals("")) {
			model.addAttribute("list", iDao.list(rowStart, rowEnd, searchKeyword, "4"));
			System.out.println("total44444444444");
		}
		
		
//		model.addAttribute("list", list);
		
		model.addAttribute("totRowCnt", total);
		model.addAttribute("searchVo", searchVO);
		
		return "list";
	}
	
	@RequestMapping("write_view")
	public String write_view(Model model) {
		System.out.println("wirte_view() ctr");
		return "write_view";
	}
	
	@RequestMapping("write")
	public String write(MultipartHttpServletRequest mtfRequest, Model model) {
		System.out.println("write() ctr");
		String bname = mtfRequest.getParameter("bname");
		String btitle = mtfRequest.getParameter("btitle");
		String bcontent = mtfRequest.getParameter("bcontent");
		iDao.write(bname, btitle, bcontent);
		
		// 경로 지정
		String workPath = System.getProperty("user.dir");
		String root = workPath + "\\src\\main\\resources\\static\\files";
		System.out.println(workPath);
		
		// 여러개올수 있으니 List
		List<MultipartFile> fileList = mtfRequest.getFiles("file");
		
		int bid = iDao.selBid();
		System.out.println("bid>>>>>" + bid);
		
		for (MultipartFile mf : fileList) {
			String originalFile = mf.getOriginalFilename();
			System.out.println("files: " + originalFile);
			long longtime = System.currentTimeMillis();
			
			String changeFile = longtime + "_" + originalFile;
			System.out.println("change files: " + changeFile);
			
			String pathfile = root + "\\" + changeFile;
			try {
				if (!originalFile.equals("")) {
					mf.transferTo(new File(pathfile));
					System.out.println("upload success");
					
					// db기록
					iDao.imgwrite(bid, originalFile, changeFile);
					System.out.println("rebrdimgtb write success");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return "redirect:list";
	}
	
	@GetMapping("content_view")
	public String content_view(HttpServletRequest request, Model model) {
		System.out.println("content_view() ctr");		
//		model.addAttribute("request", request);
//		command = new BContentCommand();
//		command.execute(model);
		
		String bid = request.getParameter("bid");
		iDao.upHit(bid);
		
		BDto dto = iDao.contentView(bid);
		model.addAttribute("content_view", dto);
		
		
		return "content_view";
	}
	
	@GetMapping("modify_view")
	public String modify_view(HttpServletRequest request, Model model) {
		System.out.println("modify_view() ctr");
//		model.addAttribute("request", request);
//		command = new BModifyViewCommand();
//		command.execute(model);
		
		String bid = request.getParameter("bid");
		BDto dto = iDao.modifyView(bid);
		
		model.addAttribute("content_view", dto);
		
		return "modify_view";
	}
	
	@PostMapping("modify")
	public String modify(HttpServletRequest request, Model model) {
		System.out.println("modify() ctr");		
//		model.addAttribute("request", request);
//		command = new BModifyCommand();
//		command.execute(model);
		
		String bid = request.getParameter("bid");
		String bname = request.getParameter("bname");
		String btitle = request.getParameter("btitle");
		String bcontent = request.getParameter("bcontent");
		
		iDao.modify(bid, bname, btitle, bcontent);
		
		return "redirect:list";
	}
	
	@GetMapping("reply_view")
	public String reply_view(HttpServletRequest request, Model model) {
		System.out.println("reply_view() ctr");		
//		model.addAttribute("request", request);
//		command = new BReplyViewCommand();
//		command.execute(model);
		
		String bid = request.getParameter("bid");
		
		BDto dto = iDao.reply_view(bid);
		model.addAttribute("reply_view", dto);
		
		return "reply_view";
	}
	
	@PostMapping("reply")
	public String reply(HttpServletRequest request, Model model) {
		System.out.println("reply() ctr");
//		model.addAttribute("request", request);
//		command = new BReplyCommand();
//		command.execute(model);
		String bid = request.getParameter("bid");
		String bname = request.getParameter("bname");
		String btitle = request.getParameter("btitle");
		String bcontent = request.getParameter("bcontent");
		
		String bgroup = request.getParameter("bgroup");
		String bstep = request.getParameter("bstep");
		String bindent = request.getParameter("bindent");
		
		iDao.replyShape(bgroup, bstep);
		iDao.reply(bid, bname, btitle, bcontent, bgroup, bstep, bindent);
		
		return "redirect:list";
	}
	
	@RequestMapping("delete")
	public String delete(HttpServletRequest request, Model model) {
		System.out.println("delete ctr");
//		model.addAttribute("request", request);
//		command = new BDeleteCommand();
//		command.execute(model);
		
		String bid = request.getParameter("bid");
		iDao.delete(bid);
		
		return "redirect:list";
	}
	
	
	
}
