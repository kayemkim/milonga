package com.km.milonga;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class RestfulController {
	
	@RequestMapping(value="/owners/{ownerId}/{team}", method=RequestMethod.GET)
	public String findOwner(@PathVariable String ownerId, @PathVariable String team, Model model) {
		model.addAttribute("owner", ownerId);
		model.addAttribute("team", team);
		return "displayOwner";
	}

}
