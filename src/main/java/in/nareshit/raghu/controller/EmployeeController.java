package in.nareshit.raghu.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import in.nareshit.raghu.exception.EmployeeNotFoundException;
import in.nareshit.raghu.model.Employee;
import in.nareshit.raghu.service.IEmployeeService;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
	
	private static final Logger LOG = LoggerFactory.getLogger(EmployeeController.class);

	@Autowired
	private IEmployeeService service;

	//1. show Register page
	/**
	 * If End user enters /register, GET type
	 *  then we should display one Register page in browser
	 */
	@GetMapping("/register")
	public String showReg() {
		return "EmployeeRegister";
	}
	/***
	 * On Click HTML FORM SUBMIT
	 * READ DATA AS MODLEATTRIBUTE
	 * SAVE USING SERVICE
	 * RETURN MESSAGE TO UI
	 */
	//2. save() : Click Form submit
	@PostMapping("/save")
	public String saveEmp(
			@ModelAttribute Employee employee,
			Model model
			) 
	{
		LOG.info("ENTERED INTO SAVE METHOD!");
		try {
			Integer id = service.saveEmployee(employee);
			String mesage = "Employee '"+id+"' created!!";
			LOG.debug(mesage);
			model.addAttribute("message", mesage);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in save {}",e.getMessage());
		}
		LOG.info("ABOUT TO LEAVE SAVE METHOD");
		return "EmployeeRegister";
	}

	/***
	 * FETCH DATA FROM DATABASE as LIST<T>
	 * SEND THIS TO UI.
	 * USE FOR EACH AND DISPLAY AS HTML TABLE
	 */
	//3. display all rows 
	//..../all?page=0
	@GetMapping("/all")
	public String getAllEmps( 
			@PageableDefault(page=0,size=3) Pageable pageable ,
			Model model) 
	{
		commonDataFetch(model,pageable);
		return "EmployeeData";
	}
	
	private void commonDataFetch(Model model,Pageable pageable) {
		Page<Employee> page = service.getAllEmployees(pageable);
		model.addAttribute("page", page);
		model.addAttribute("list", page.getContent());
	}

	//4. Delete by id
	@GetMapping("/delete")
	public String deleteById(
			@RequestParam Integer id,
			Model model
			)
	{
		LOG.info("ENTERED INTO DELETE METHOD");
		String message = null;
		try {
			//delete record by id
			service.deleteEmployee(id);
			message = "Employee '"+id+"' Deleted!!";
			LOG.debug(message);
		} catch (EmployeeNotFoundException e) {
			e.printStackTrace();
			message = e.getMessage();
			LOG.error("UNABLE TO DELETE {}",message);
		}

		//send message to ui
		model.addAttribute("message", message);
		//also load latest data
		commonDataFetch(model,PageRequest.of(0, 3));
		LOG.info("LEAVING FROM DELETE METHOD");
		return "EmployeeData";
	}

	//5. Show Data in Edit (by id)
	/***
	 * On click Edit Link , read id and load object from DB
	 * if exist goto Edit page, else redirect to all page
	 */
	@GetMapping("/edit")
	public String showEdit(
			@RequestParam Integer id,
			Model model) 
	{
		String page = null;
		try {
			//try to load data from DB
			Employee emp = service.getOneEmployee(id);
			model.addAttribute("employee", emp);
			page = "EmployeeEdit";
			
		} catch (EmployeeNotFoundException e) {
			e.printStackTrace();
			page = "EmployeeData";
			commonDataFetch(model,PageRequest.of(0, 3));
			model.addAttribute("message", e.getMessage());
		}
		
		return page;
	}

	//6. Update data
	/**
	 * On Click Update button, read Form data as ModelAttribute
	 * Update in DB and send success message to UI.
	 * Also load latest data
	 */
	@PostMapping("/update")
	public String doUpdate(
			@ModelAttribute Employee employee,
			Model model
			) 
	{
		service.updateEmployee(employee);
		//send message to ui
		model.addAttribute("message", "Employee '"+employee.getEmpId()+"' Updated!!");

		//also load latest data
		commonDataFetch(model,PageRequest.of(0, 3));
		return "EmployeeData";

	}
	
	//7. AJAX Validation
	/***
	 * read ename and return message back.
	 * Not page name
	 */
	@GetMapping("/validate")
	public @ResponseBody String validateEname(
			@RequestParam String ename
			) 
	{
		String message ="";
		if(service.isEmployeeExistByName(ename)) {
			message = ename+", already exist!! ";
		}
		return message; // You are not a ViewName 
	}
}
