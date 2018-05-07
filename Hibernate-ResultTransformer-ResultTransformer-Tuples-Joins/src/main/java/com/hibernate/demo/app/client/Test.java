package com.hibernate.demo.app.client;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.ResultTransformer;

import com.hibernate.demo.app.model.Department;
import com.hibernate.demo.app.model.Employee;

public class Test {

	public static SessionFactory getSessionFactory() {
		Configuration configuration = new Configuration().configure();
		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
				.applySettings(configuration.getProperties());
		SessionFactory factory = configuration.buildSessionFactory(builder.build());
		return factory;
	}

	@SuppressWarnings({ "unchecked", "serial", "rawtypes" })
	public void emloyeeProjectionCriteriaTransformer() {
		Session session = getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(Employee.class, "employee");
		criteria.createAlias("employee.department", "department");
		List<Employee> list = criteria
				.setProjection(
						Projections.projectionList().add(Projections.property("employee.firstname").as("firstname"))
								.add(Projections.property("employee.lastname").as("lastname"))
								.add(Projections.property("department.departmentName").as("departmentName")))
				.setResultTransformer(new ResultTransformer() {
					public Object transformTuple(Object[] tuple, String[] aliases) {
						Employee employee = new Employee();
						employee.setFirstName((String) tuple[0]);
						employee.setLastName((String) tuple[1]);

						Department department = new Department();
						department.setDepartmentName((String) tuple[2]);
						employee.setDepartment(department);

						return employee;
					}

					public List<Employee> transformList(List list) {
						return list;
					}
				}).list();
		System.out.println("__________________________________________________________________");
		System.out.println("| Employee Name \t\t | \t\t Department Name |");
		System.out.println("------------------------------------------------------------------");
		for (Employee employee : list) {
			System.out.println("|" + employee.getFirstName() + " " + employee.getLastName() + "\t\t\t\t\t\t"
					+ employee.getDepartment().getDepartmentName() + "|");
		}

	}

	@SuppressWarnings({ "unchecked", "serial", "rawtypes" })
	public void emloyeeProjectionHQLQueryTransformer() {
		Session session = getSessionFactory().openSession();
		List<Employee> list = session
				.createQuery(
						"select e.firstname, e.lastname, d from Employee e join e.department d where e.firstname=:fn")
				.setParameter("fn", "Rahul").setResultTransformer(new ResultTransformer() {
					public Object transformTuple(Object[] tuple, String[] aliases) {
						Employee employee = new Employee();
						employee.setFirstName((String) tuple[0]);
						employee.setLastName((String) tuple[1]);
						employee.setDepartment((Department) tuple[2]);
						return employee;
					}

					public List<Employee> transformList(List list) {
						return list;
					}
				}).list();

		System.out.println("__________________________________________________________________");
		System.out.println("| Employee Name \t\t | \t\t Department Name |");
		System.out.println("------------------------------------------------------------------");
		for (Employee employee : list) {
			System.out.println("|" + employee.getFirstName() + " " + employee.getLastName() + "\t\t\t\t\t\t"
					+ employee.getDepartment().getDepartmentName() + "|");
		}

	}

	public static void main(String[] args) {
		Test test = new Test();
		test.emloyeeProjectionCriteriaTransformer();
		test.emloyeeProjectionHQLQueryTransformer();
	}

}
