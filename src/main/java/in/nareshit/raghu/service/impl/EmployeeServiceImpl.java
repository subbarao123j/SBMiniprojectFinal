package in.nareshit.raghu.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import in.nareshit.raghu.exception.EmployeeNotFoundException;
import in.nareshit.raghu.model.Employee;
import in.nareshit.raghu.repo.EmployeeRepository;
import in.nareshit.raghu.service.IEmployeeService;

@Service
public class EmployeeServiceImpl implements IEmployeeService {

	@Autowired
	private EmployeeRepository repo;

	public Integer saveEmployee(Employee e) {
		calculateEmp(e);
		//save object 
		var id = repo.save(e).getEmpId();

		return id;
	}

	private void calculateEmp(Employee e) {
		// Local variable Type inference(DataType is decided by Java Compiler)
		var esal = e.getEmpSal();
		var hra = esal * 20/100.0;
		var ta = esal * 10/100.0;

		//set data back to object
		e.setEmpHra(hra);
		e.setEmpTa(ta);

	}

	public void updateEmployee(Employee e) {
		//if given id exist in db then update
		if(repo.existsById(e.getEmpId())) {
			calculateEmp(e);
			repo.save(e);
		}
	}


	public void deleteEmployee(Integer id) {
		//repo.deleteById(id);
		repo.delete(getOneEmployee(id));
	}


	public Employee getOneEmployee(Integer id) {
		Optional<Employee> opt = repo.findById(id);
		if(opt.isPresent()) {
			return opt.get();
		} else {
			throw new EmployeeNotFoundException("EMPLOYEE '"+id+"' NOT EXIST");
		}
	}


	public List<Employee> getAllEmployees() {
		List<Employee> emps = repo.findAll();
		return emps
				.stream()
				.sorted(
						//(e1,e2)->e1.getEmpId().compareTo(e2.getEmpId()) //ASC
						(e1,e2)->e2.getEmpId().compareTo(e1.getEmpId()) //DESC
						)
				.collect(Collectors.toList());
	}
	
	public boolean isEmployeeExistByName(String ename) {
		/*Integer count = repo.getCountOfEmpName(ename);
		if(count> 0) 
			return true;
		else 
			return false;*/
		return repo.getCountOfEmpName(ename)>0;
	}
	
	public Page<Employee> getAllEmployees(Pageable pageable) {
		Page<Employee> page = repo.findAll(pageable);
		return page;
	}

}
