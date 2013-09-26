package com.skp.milonga;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skp.milonga.externals.blog.model.Blog;

@Controller
public class RestfulController {
	
	@RequestMapping(value="/owners/{ownerId}/{team}", method=RequestMethod.GET)
	public String findOwner(@PathVariable String ownerId, @PathVariable String team, Model model) {
		model.addAttribute("owner", ownerId);
		model.addAttribute("team", team);
		return "displayOwner";
	}
	
	@RequestMapping(value = "/player/{name}", method = RequestMethod.GET,
			produces = {"application/json", "application/xml"})
	public @ResponseBody Blog getPlayer(@PathVariable String name) {
		Blog blog = new Blog();
		blog.setId(name);
		return blog;
	}

}
