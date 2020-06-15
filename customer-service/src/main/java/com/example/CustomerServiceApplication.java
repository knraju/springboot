package com.example;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.ribbon.proxy.annotation.Http.HttpMethod;

@EnableDiscoveryClient
@SpringBootApplication
public class CustomerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerServiceApplication.class, args);
	}

	@LoadBalanced
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}

@Component
class DiscoveryClientExample implements CommandLineRunner {

	@Autowired
	private DiscoveryClient discoveryClient;

	@Override
	public void run(String... strings) throws Exception {
		/*
		 * discoveryClient.getInstances("reservation-service").forEach((
		 * ServiceInstance s) -> {
		 * System.out.println(ToStringBuilder.reflectionToString(s)); });
		 */
		discoveryClient.getInstances("customer-service").forEach((ServiceInstance s) -> {
			System.out.println(ToStringBuilder.reflectionToString(s));
		});
	}
}

@RefreshScope
@RestController
class ServiceInstanceRestController {

	@Value("${message:Hello default}")
	private String message;

	@RequestMapping("/message")
	String getMessage() {
		return this.message;
	}

	@Autowired
	private DiscoveryClient discoveryClient;

	@RequestMapping("/service-instances/{applicationName}")
	public List<ServiceInstance> serviceInstancesByApplicationName(@PathVariable String applicationName) {
		return this.discoveryClient.getInstances(applicationName);
	}

	@RequestMapping("/employee")
	public List<Person> getPersonList() {
		
		List<Person> persons = new ArrayList<>();
		persons.add(new Person(1l, 30, "nagaraju", "kommineni"));
		persons.add(new Person(2l, 25, "anusha", "ch"));
		persons.add(new Person(3l, 35, "madhu", "tolusuri"));
		
		return persons;

	}
}

@Component
class RestTemplateExample implements CommandLineRunner {

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public void run(String... strings) throws Exception {
		// use the "smart" Eureka-aware RestTemplate
		/*
		 * ResponseEntity<List<Bookmark>> exchange = this.restTemplate.exchange(
		 * "http://reservation-service/message", HttpMethod.GET, null, new
		 * ParameterizedTypeReference<List<Bookmark>>() { }, (Object) "mstine");
		 * 
		 * 
		 * exchange.getBody().forEach(System.out::println);
		 */

		String msg = this.restTemplate.getForObject("http://CUSTOMER-SERVICE/message", String.class);
		System.out.println(msg);
		ResponseEntity<Person[]> personsArray = this.restTemplate.getForEntity("http://CUSTOMER-SERVICE/employee", Person[].class);
		
		for (Person person : personsArray.getBody()) {
			System.out.println(person);
		}
		

	}

}

class Person {
	private Long id;

	private Integer age;

	private String firstName;
	private String lastName;
	
	public Person() {
		// TODO Auto-generated constructor stub
	}
	
	

	public Person(Long id, Integer age, String firstName, String lastName) {
		super();
		this.id = id;
		this.age = age;
		this.firstName = firstName;
		this.lastName = lastName;
	}



	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		return "Person{" + "id=" + id + ", age=" + age + ", firstName='" + firstName + '\'' + ", lastName='" + lastName
				+ '\'' + '}';
	}

}
